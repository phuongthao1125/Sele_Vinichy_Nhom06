package testcases;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.BookTicketPage;
import pageobjects.HomePage;
import pageobjects.LoginPage;

import java.util.List;

import static common.Constant.WEBDRIVER;

public class MyTicketTest {

    @BeforeMethod
    public void beforeTest() {
        System.out.println("Pre-condition");
        LoggerUtil.info("Pre-condition");
        WEBDRIVER = new ChromeDriver();
        WEBDRIVER.manage().window().fullscreen();
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("Post-condition");
        LoggerUtil.info("Post-condition");
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
        }
    }

    @Test
    public void TC16() {
        System.out.println("TC16 - User can cancel a ticket");
        LoggerUtil.info("TC16 - User can cancel a ticket");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with valid account");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Open Book Ticket page");
        WaitUtil.click(By.xpath("//a[@href='/Page/BookTicketPage.cshtml']"));

        LoggerUtil.info("Book a ticket");
        BookTicketPage bookTicketPage = new BookTicketPage();
        bookTicketPage.selectRandomDepartDate(5);
        bookTicketPage.selectDepartFrom("Quảng Ngãi");
        bookTicketPage.selectArriveAt("Huế");
        bookTicketPage.selectSeatType("Soft seat");
        bookTicketPage.selectTicketAmount("1");
        bookTicketPage.clickBookTicketButton();

        LoggerUtil.info("Open My ticket page");
        WaitUtil.click(By.linkText("My ticket"));

        LoggerUtil.info("Find cancel buttons");
        List<WebElement> cancelButtons = WEBDRIVER.findElements(By.xpath("//input[@value='Cancel']"));
        Assert.assertFalse(cancelButtons.isEmpty(), "No tickets available to cancel.");

        WebElement cancelButton = cancelButtons.get(0);
        String onClickValue = cancelButton.getAttribute("onclick");
        String idString = onClickValue.split("\\(")[1].split("\\)")[0];
        int id = Integer.parseInt(idString);

        LoggerUtil.info("Cancel ticket with ID: " + id);
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].scrollIntoView(true);", cancelButton);
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", cancelButton);

        try {
            LoggerUtil.info("Accept alert if displayed");
            Alert alert = WEBDRIVER.switchTo().alert();
            alert.accept();
        } catch (NoAlertPresentException e) {
            System.out.println("No confirmation alert displayed.");
            LoggerUtil.info("No confirmation alert displayed");
        }

        LoggerUtil.info("Verify ticket is cancelled");
        WebElement pageBody = WaitUtil.waitForVisible(By.tagName("body"));
        boolean isTicketCancelled = !pageBody.getText().contains(Integer.toString(id));

        Assert.assertTrue(isTicketCancelled, "Ticket cancellation failed for ID " + id);
        System.out.println("Ticket with ID " + id + " has been successfully cancelled.");
        LoggerUtil.info("Ticket with ID " + id + " has been successfully cancelled");
    }
}