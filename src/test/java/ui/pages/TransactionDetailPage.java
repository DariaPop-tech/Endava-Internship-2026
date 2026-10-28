package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TransactionDetailPage {

    private Locator transactionHeader;
    private Locator transactionDescription;
    private Locator transactionAmount;
    private Locator commentInput;
    private Locator comments;

    public void init(Page page) {
        transactionHeader = page.locator("[data-test='transaction-detail-header']");
        transactionDescription = page.locator("[data-test='transaction-description']");
        transactionAmount = page.locator("[data-test^='transaction-amount-']");
        commentInput = page.locator("[data-test^='transaction-comment-input-']");
        comments = page.locator("[data-test='comments-list'] [data-test^='comment-list-item-']");
    }

    public void waitForTransactionDetail() {
        transactionHeader.waitFor();
    }

    public boolean isTransactionVisible() {
        return transactionHeader.isVisible();
    }

    public boolean hasNote(String note) {
        return transactionDescription.textContent().contains(note);
    }

    public boolean hasAmount(String amount) {
        return transactionAmount.textContent().contains(amount);
    }

    public void addComment(String comment) {
        commentInput.fill(comment);
        commentInput.press("Enter");
        comments
                .filter(new Locator.FilterOptions().setHasText(comment))
                .waitFor();
    }

    public boolean hasComment(String comment) {
        return comments
                .filter(new Locator.FilterOptions().setHasText(comment))
                .isVisible();
    }
}
