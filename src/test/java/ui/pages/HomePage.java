package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class HomePage {

    private Page page;

    // Locators
    private Locator logoutButton;
    private Locator transactionList;

    public void init(Page page) {
        this.page = page;
        logoutButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Logout"));
        transactionList = page.locator("//div[@data-test='transaction-list']");
        transactionList.isVisible();
    }

    public boolean isLogoutButtonVisible() {
        return logoutButton.isVisible();
    }

    public boolean isHomePageVisible() {
        return transactionList.isVisible();
    }
}
