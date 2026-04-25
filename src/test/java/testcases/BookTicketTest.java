package testcases;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.BookTicketPage;
import pageobjects.HomePage;
import pageobjects.LoginPage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static common.Constant.WEBDRIVER;

public class BookTicketTest {

    private WebDriverWait wait;

    @BeforeMethod
    public void beforeTest() {
        LoggerUtil.info("Pre-condition");
        WEBDRIVER = new ChromeDriver();
        WEBDRIVER.manage().window().fullscreen();
        wait = new WebDriverWait(WEBDRIVER, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("Post-condition");
        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
        }
    }

    @Test
    public void TC04() {
        LoggerUtil.info("TC04 - Open Book Ticket without login");

        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Click Book Ticket tab");
        WebElement bookTicketTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='menu']//a[@href='/Page/BookTicketPage.cshtml']")));
        bookTicketTab.click();

        wait.until(ExpectedConditions.urlContains("/Account/Login.cshtml"));

        LoggerUtil.info("Verify login page displayed");
        WebElement loginForm = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[@id='content']")));
        Assert.assertTrue(loginForm.isDisplayed());
    }

    @Test
    public void TC14() {
        LoggerUtil.info("TC14 - Book ticket");

        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Login");
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Click Book Ticket tab");
        WebElement bookTicketTab = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/Page/BookTicketPage.cshtml']")));
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", bookTicketTab);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='Date']")));

        LoggerUtil.info("Fill ticket info");
        BookTicketPage bookTicketPage = new BookTicketPage();
        bookTicketPage.selectRandomDepartDate(5);
        bookTicketPage.selectDepartFrom("Sài Gòn");
        bookTicketPage.selectArriveAt("Nha Trang");
        bookTicketPage.selectSeatType("Soft bed with air conditioner");
        bookTicketPage.selectTicketAmount("1");

        String expectedDepartDate = bookTicketPage.getDepartDate() != null
                ? bookTicketPage.getDepartDate()
                : LocalDate.now().plusDays(5)
                .format(DateTimeFormatter.ofPattern("M/d/yyyy"));

        LoggerUtil.info("Click Book ticket");
        bookTicketPage.clickBookTicketButton();

        LoggerUtil.info("Verify booking success");
        Assert.assertTrue(bookTicketPage.isTicketBookedSuccessfullyDisplayed());

        String actualDepartStation = bookTicketPage.getDepartStation();
        String actualArriveStation = bookTicketPage.getArriveStation();
        String actualSeatType = bookTicketPage.getSeatType();
        String actualAmount = bookTicketPage.getAmount();

        Assert.assertEquals(actualDepartStation, "Sài Gòn");
        Assert.assertEquals(actualArriveStation, "Nha Trang");
        Assert.assertEquals(actualSeatType, "Soft bed with air conditioner");
        Assert.assertEquals(actualAmount, "1");
    }
}