package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage {

    private Page page;

    // Locators
    private Locator logoutButton;
    private Locator transactionList;
    private Locator myAccountLink;
    private Locator newTransactionButton;
    private Locator homeLink;
    private Locator mineTab;
    private Locator transactions;

    public void init(Page page) {
        this.page = page;
        logoutButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Logout"));
        transactionList = page.locator("//div[@data-test='transaction-list']");
        myAccountLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("My Account"));
        newTransactionButton = page.locator("[data-test='nav-top-new-transaction']");
        homeLink = page.locator("[data-test='sidenav-home']");
        mineTab = page.locator("[data-test='nav-personal-tab']");
        transactions = page.locator("[data-test^='transaction-item-']");
        transactionList.isVisible();
    }

    public boolean isLogoutButtonVisible() {
        return logoutButton.isVisible();
    }

    public boolean isHomePageVisible() {
        return transactionList.isVisible();
    }

    public MyAccountPage navigateToMyAccount(){
        myAccountLink.click();
        MyAccountPage myAccountPage = new MyAccountPage();
        myAccountPage.init(page);
        return myAccountPage;
    }

    public NewTransactionPage navigateToNewTransaction() {
        newTransactionButton.click();
        NewTransactionPage newTransactionPage = new NewTransactionPage();
        newTransactionPage.init(page);
        return newTransactionPage;
    }

    public void navigateHome() {
        homeLink.click();
        transactionList.waitFor();
    }

    public void selectMineTab() {
        mineTab.click();
    }

    public TransactionDetailPage openTransaction(String note) {
        transactions
                .filter(new Locator.FilterOptions().setHasText(note))
                .click();

        TransactionDetailPage transactionDetailPage = new TransactionDetailPage();
        transactionDetailPage.init(page);
        transactionDetailPage.waitForTransactionDetail();
        return transactionDetailPage;
    }

    public TransactionDetailPage openFirstTransaction() {
        transactions.first().click();

        TransactionDetailPage transactionDetailPage = new TransactionDetailPage();
        transactionDetailPage.init(page);
        transactionDetailPage.waitForTransactionDetail();
        return transactionDetailPage;
    }

}

