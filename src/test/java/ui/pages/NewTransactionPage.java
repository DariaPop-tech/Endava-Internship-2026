package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class NewTransactionPage {

    private Page page;
    private Locator users;
    private Locator amountInput;
    private Locator noteInput;
    private Locator payButton;
    private Locator paymentConfirmation;

    public void init(Page page) {
        this.page = page;
        users = page.locator("[data-test^='user-list-item-']");
        amountInput = page.locator("[data-test='transaction-create-amount-input'] input");
        noteInput = page.locator("[data-test='transaction-create-description-input'] input");
        payButton = page.locator("[data-test='transaction-create-submit-payment']");
        paymentConfirmation = page.locator("[data-test='new-transaction-return-to-transactions']");
    }

    public void selectFirstUser() {
        users.first().click();
        amountInput.waitFor();
    }

    public void pay(String amount, String note) {
        amountInput.fill(amount);
        noteInput.fill(note);
        payButton.click();
        paymentConfirmation.waitFor();
    }
}
