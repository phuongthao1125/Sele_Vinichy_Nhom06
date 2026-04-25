package pageobjects;

import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static common.Constant.WEBDRIVER;

public class MyTicketPage extends GeneralPage {

    private final By myTicketTab = By.xpath("//a[@href='/Page/ManageTicket.cshtml']");
    private final By cancelTicketButton = By.xpath("//input[@value='Cancel']");
    private final By confirmationDialog = By.id("confirmButton");

    public MyTicketPage clickMyTicketTab() {
        LoggerUtil.info("Click My Ticket tab");
        WEBDRIVER.findElement(myTicketTab).click();
        return this;
    }

    public boolean isMyTicketPageDisplayed() {
        LoggerUtil.info("Check MyTicket page displayed");
        return WEBDRIVER.getCurrentUrl().contains("ManageTicket");
    }

    public void cancelTicketWithId(String ticketId) {
        LoggerUtil.info("Cancel ticket with ID: " + ticketId);

        WebElement ticket = WEBDRIVER.findElement(By.id(ticketId));
        ticket.findElement(cancelTicketButton).click();
    }

    public void confirmCancellation() {
        LoggerUtil.info("Confirm ticket cancellation");

        WebElement confirmButton = WEBDRIVER.findElement(confirmationDialog);
        confirmButton.click();
    }
}