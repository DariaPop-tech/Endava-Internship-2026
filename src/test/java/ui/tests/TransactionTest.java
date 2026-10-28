package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.NewTransactionPage;
import ui.pages.TransactionDetailPage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest extends BaseTest {

    @Test
    void userCanFindCreatedTransactionInMine() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        String amount = "5";
        String note = "UI test transaction " + UUID.randomUUID();

        NewTransactionPage newTransactionPage = homePage.navigateToNewTransaction();
        newTransactionPage.selectFirstUser();
        newTransactionPage.pay(amount, note);

        homePage.navigateHome();
        homePage.selectMineTab();

        TransactionDetailPage transactionDetailPage = homePage.openTransaction(note);

        assertTrue(transactionDetailPage.isTransactionVisible(),
                "Transaction detail should be visible");
        assertTrue(transactionDetailPage.hasNote(note),
                "Transaction note should match the created transaction");
        assertTrue(transactionDetailPage.hasAmount("$5.00"),
                "Transaction amount should match the created transaction");
    }

    @Test
    void userCanCommentOnTransaction() {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        TransactionDetailPage transactionDetailPage = homePage.openFirstTransaction();
        String comment = "UI test comment " + UUID.randomUUID();

        transactionDetailPage.addComment(comment);

        assertTrue(transactionDetailPage.hasComment(comment),
                "The submitted comment should appear in the transaction comments");
    }
}
