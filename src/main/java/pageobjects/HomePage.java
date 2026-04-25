package pageobjects;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static common.Constant.WEBDRIVER;

public class HomePage extends GeneralPage {

    public HomePage open() {
        LoggerUtil.info("Open URL: " + Constant.RAILWAY_URL);
        WEBDRIVER.navigate().to(Constant.RAILWAY_URL);
        return this;
    }

    private final By myTicketTab = By.xpath("//a[@href='/Page/ManageTicket.cshtml']");
    private final By changePasswordTab = By.xpath("//a[@href='/Account/ChangePassword.cshtml']");
    private final By logoutTab = By.xpath("//a[@href='/Account/Logout']");

    public MyTicketPage clickMyTicketTab() {
        LoggerUtil.info("Click My Ticket tab");
        WEBDRIVER.findElement(myTicketTab).click();
        return new MyTicketPage();
    }

    public ChangePasswordPage clickChangePasswordTab() {
        LoggerUtil.info("Click Change Password tab");
        WEBDRIVER.findElement(changePasswordTab).click();
        return new ChangePasswordPage();
    }

    public LoginPage clickLogoutTab() {
        LoggerUtil.info("Click Logout tab");
        WEBDRIVER.findElement(logoutTab).click();
        return new LoginPage();
    }

    public RegisterPage gotoRegisterPage() {
        LoggerUtil.info("Navigate to Register Page");
        WEBDRIVER.findElement(By.xpath("//a[@href='/Account/Register.cshtml']")).click();
        return new RegisterPage();
    }

    public BookTicketPage clickBookTicketTab() {
        LoggerUtil.info("Click Book Ticket tab");
        WEBDRIVER.findElement(By.xpath("//a[@href='/Page/BookTicketPage.cshtml']")).click();
        return new BookTicketPage();
    }

    public TimeTable gotoTimeTable() {
        LoggerUtil.info("Navigate to TimeTable");

        WebDriverWait wait = new WebDriverWait(WEBDRIVER, Duration.ofSeconds(10));
        WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Timetable")));

        tab.click();

        LoggerUtil.info("Wait for TimeTable page loaded");
        wait.until(ExpectedConditions.urlContains("TrainTimeListPage"));

        return new TimeTable();
    }
}