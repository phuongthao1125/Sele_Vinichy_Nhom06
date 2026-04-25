package pageobjects;

import common.WaitUtil;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Random;

import static common.Constant.WEBDRIVER;

public class BookTicketPage extends GeneralPage {

    private final By selectDepartFrom = By.xpath("//select[@name='DepartStation']");
    private final By selectArriveAt = By.xpath("//select[@name='ArriveStation']");
    private final By selectSeatType = By.xpath("//select[@name='SeatType']");
    private final By selectTicketAmount = By.xpath("//select[@name='TicketAmount']");
    private final By btnBookTicket = By.xpath("//input[@value='Book ticket']");
    private final By selectDepartDate = By.xpath("//select[@name='Date']");

    private final By departDateField = By.xpath("//table[@class='MyTable WideTable']//tr[@class='OddRow']/td[4]");
    private final By departStationField = By.xpath("//table[@class='MyTable WideTable']//tr[@class='OddRow']/td[1]");
    private final By arriveStationField = By.xpath("//table[@class='MyTable WideTable']//tr[@class='OddRow']/td[2]");
    private final By seatTypeField = By.xpath("//table[@class='MyTable WideTable']//tr[@class='OddRow']/td[3]");
    private final By amountField = By.xpath("//table[@class='MyTable WideTable']//tr[@class='OddRow']/td[7]");

    private final By successMessage = By.xpath("//div[@id='content']//h1[contains(text(),'Ticket booked successfully!')]");

    public String getDepartDate() {
        LoggerUtil.info("Get Depart Date");
        return WaitUtil.waitForVisible(departDateField).getText();
    }

    public String getDepartStation() {
        LoggerUtil.info("Get Depart Station");
        return WaitUtil.waitForVisible(departStationField).getText();
    }

    public String getArriveStation() {
        LoggerUtil.info("Get Arrive Station");
        return WaitUtil.waitForVisible(arriveStationField).getText();
    }

    public String getSeatType() {
        LoggerUtil.info("Get Seat Type");
        return WaitUtil.waitForVisible(seatTypeField).getText();
    }

    public String getAmount() {
        LoggerUtil.info("Get Amount");
        return WaitUtil.waitForVisible(amountField).getText();
    }

    public boolean isTicketBookedSuccessfullyDisplayed() {
        LoggerUtil.info("Check ticket booked success message");
        try {
            return WaitUtil.waitForVisible(successMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void selectRandomDepartDate(int i) {
        LoggerUtil.info("Select random depart date");

        WebElement dropdown = WaitUtil.waitForVisible(selectDepartDate);
        scrollToElement(dropdown);

        Select select = new Select(dropdown);
        List<WebElement> options = select.getOptions();

        int randomIndex = new Random().nextInt(options.size());
        LoggerUtil.info("Random index: " + randomIndex);

        select.selectByIndex(randomIndex);
    }

    private void selectOptionInDropdown(By dropdownLocator, String option) {
        LoggerUtil.info("Select option: " + option + " in " + dropdownLocator);

        WebElement dropdown = WaitUtil.waitForVisible(dropdownLocator);
        scrollToElement(dropdown);

        Select select = new Select(dropdown);
        select.selectByVisibleText(option);
    }

    public void selectDepartFrom(String station) {
        LoggerUtil.info("Select Depart From: " + station);
        selectOptionInDropdown(selectDepartFrom, station);
    }

    public void selectArriveAt(String station) {
        LoggerUtil.info("Select Arrive At: " + station);
        selectOptionInDropdown(selectArriveAt, station);
    }

    public void selectSeatType(String seatType) {
        LoggerUtil.info("Select Seat Type: " + seatType);
        selectOptionInDropdown(selectSeatType, seatType);
    }

    public void selectTicketAmount(String amount) {
        LoggerUtil.info("Select Ticket Amount: " + amount);
        selectOptionInDropdown(selectTicketAmount, amount);
    }

    public void clickBookTicketButton() {
        LoggerUtil.info("Click Book Ticket Button");

        WebElement button = WaitUtil.waitForClickable(btnBookTicket);
        scrollToElement(button);

        try {
            button.click();
        } catch (Exception e) {
            LoggerUtil.warn("Click failed → using JS");
            ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", button);
        }
    }

    private void scrollToElement(WebElement element) {
        LoggerUtil.info("Scroll to element");
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    public String getDepartFrom() {
        LoggerUtil.info("Get Depart From dropdown value");
        WebElement e = WaitUtil.waitForVisible(selectDepartFrom);
        return new Select(e).getFirstSelectedOption().getText();
    }

    public String getArriveAt() {
        LoggerUtil.info("Get Arrive At dropdown value");
        WebElement e = WaitUtil.waitForVisible(selectArriveAt);
        return new Select(e).getFirstSelectedOption().getText();
    }
}