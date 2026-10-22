import { test, expect } from "@fixtures/ui";
import {
  bySel,
  bySelLike,
  exposeAppServices,
  fillTransactionForm,
  loginViaForm,
  openFeed,
  searchUsersInput,
  selectContact,
  startNewTransaction,
  transactionItems,
  waitForApi,
  waitForFeed,
} from "@ui";
import { env } from "@config";
import { dollarsToCents } from "@utils/money";

test.describe("Journey: Alice pays Bob in separate browser contexts", { tag: ["@e2e"] }, () => {
  test("Bob sees Alice's payment in his own browser context", async ({ page, browser, db, users: [alice, bob] }) => {
    const payment = { amount: 42, description: `E2E payment ${Date.now()}` };
    let transactionId = "";
    const bobContext = await browser.newContext();

    try {
      const bobPage = await bobContext.newPage();
      await exposeAppServices(bobPage);

      await test.step("Alice signs in and sends Bob a payment", async () => {
        await loginViaForm(page, alice.username, env.defaultPassword);
        await startNewTransaction(page);

        const searched = waitForApi(page, { method: "GET", path: "/users/search" });
        await searchUsersInput(page).fill(bob.firstName);
        await searched;
        await selectContact(page, bob.firstName);
        await fillTransactionForm(page, String(payment.amount), payment.description);

        const created = waitForApi(page, { method: "POST", path: "/transactions" });
        await bySelLike(page, "submit-payment").click();
        const response = await created;
        expect(response.status()).toBe(200);
        transactionId = ((await response.json()) as { transaction: { id: string } }).transaction.id;
        await expect(bySel(page, "alert-bar-success")).toHaveText("Transaction Submitted!");
      });

      await test.step("Bob signs in in a separate browser context", async () => {
        await bobPage.goto(`${env.baseURL}/signin`);
        const login = await loginViaForm(bobPage, bob.username, env.defaultPassword);
        expect(login.status()).toBe(200);
        await expect(bobPage).not.toHaveURL(/\/signin$/);
      });

      await test.step("Bob sees the payment in his personal feed", async () => {
        const feed = waitForFeed(bobPage, "personal");
        await openFeed(bobPage, "personal");
        await feed;
        await expect(transactionItems(bobPage).filter({ hasText: payment.description }).first()).toBeVisible();
      });

      await test.step("The database records the payment and updated balances", async () => {
        const transaction = await db.find("transactions", { id: transactionId });
        const [aliceAfter, bobAfter] = await Promise.all([
          db.find("users", { id: alice.id }),
          db.find("users", { id: bob.id }),
        ]);

        expect(transaction).toMatchObject({
          amount: dollarsToCents(payment.amount),
          description: payment.description,
          senderId: alice.id,
          receiverId: bob.id,
        });
        expect(aliceAfter.balance).toBe(alice.balance - dollarsToCents(payment.amount));
        expect(bobAfter.balance).toBe(bob.balance + dollarsToCents(payment.amount));
      });
    } finally {
      await bobContext.close();
    }
  });
});