package testcases;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
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
import pageobjects.TimeTable;

import java.time.Duration;

import static common.Constant.WEBDRIVER;

public class TimeTableTest {

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
    public void TC15() {
        System.out.println("TC15 - Open Book Ticket from Timetable");
        LoggerUtil.info("TC15 - Open Book Ticket from Timetable");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with valid account");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Open TimeTable page");
        TimeTable timeTable = homePage.gotoTimeTable();
        LoggerUtil.info("Click Book Ticket Huế to Sài Gòn");
        timeTable.clickBookTicketHueToSaigon();

        // Chờ URL chuyển sang BookTicketPage
        LoggerUtil.info("Wait until URL contains BookTicketPage");
        WebDriverWait wait = new WebDriverWait(WEBDRIVER, Duration.ofSeconds(60));
        wait.until(ExpectedConditions.urlContains("BookTicketPage"));

        // Chờ select đầu tiên trong form xuất hiện (thay vì By.name("Date"))
        LoggerUtil.info("Wait until select appears");
        WaitUtil.waitForVisible(By.xpath("//select"));

        BookTicketPage bookTicketPage = new BookTicketPage();

        LoggerUtil.info("Get depart and arrive values");
        String actualDepartFrom = bookTicketPage.getDepartFrom().trim();
        String actualArriveAt = bookTicketPage.getArriveAt().trim();

        System.out.println("Actual depart  = " + actualDepartFrom);
        System.out.println("Actual arrive  = " + actualArriveAt);
        System.out.println("Current URL    = " + WEBDRIVER.getCurrentUrl());

        LoggerUtil.info("Actual depart = " + actualDepartFrom);
        LoggerUtil.info("Actual arrive = " + actualArriveAt);
        LoggerUtil.info("Current URL = " + WEBDRIVER.getCurrentUrl());

        Assert.assertEquals(actualDepartFrom, "Hu\u1EBF",      "Depart from value is incorrect");
        Assert.assertEquals(actualArriveAt,   "S\u00E0i G\u00F2n", "Arrive at value is incorrect");
    }
}