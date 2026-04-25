package testcases;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.*;

import static common.Constant.USERNAME;
import static common.Constant.WEBDRIVER;

public class LoginTests {

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
    public void TC01() {
        System.out.println("TC01 - User can log into Railway with valid username and password");
        LoggerUtil.info("TC01 - User can log into Railway with valid username and password");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with valid username and password");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Verify welcome message");
        String actualMsg = WaitUtil.getTextOrEmpty(By.xpath("//div[@class='account']/strong"));
        String expectMsg = "Welcome " + Constant.USERNAME;
        Assert.assertEquals(actualMsg, expectMsg, "Welcome message is not displayed as expected");
    }

    @Test
    public void TC02() {
        System.out.println("TC02 - User can't login with blank Username textbox");
        LoggerUtil.info("TC02 - User can't login with blank Username textbox");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with blank username");
        loginPage.login("", Constant.PASSWORD);

        LoggerUtil.info("Verify error message");
        String actualErrorMsg = WaitUtil.getTextOrEmpty(By.xpath("//p[@class='message error LoginForm']"));
        String expectedErrorMsg = "There was a problem with your login and/or errors exist in your form.";
        Assert.assertEquals(actualErrorMsg, expectedErrorMsg, "Error message is not displayed as expected");
    }

    @Test
    public void TC03() {
        System.out.println("TC03 - User cannot log into Railway with invalid password");
        LoggerUtil.info("TC03 - User cannot log into Railway with invalid password");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with invalid password");
        loginPage.login(Constant.USERNAME, "123567894");

        LoggerUtil.info("Verify error message");
        String actualErrorMsg = WaitUtil.getTextOrEmpty(By.xpath("//p[@class='message error LoginForm']"));
        String expectedErrorMsg = "There was a problem with your login and/or errors exist in your form.";
        Assert.assertEquals(actualErrorMsg, expectedErrorMsg, "Error message is not displayed as expected");
    }

    @Test
    public void TC05() {
        System.out.println("TC05 - System shows message when user enters wrong password several times");
        LoggerUtil.info("TC05 - System shows message when user enters wrong password several times");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();

        for (int i = 0; i < 4; i++) {
            LoggerUtil.info("Login attempt number: " + (i + 1));
            WaitUtil.sendKeys(By.id("username"), USERNAME);
            WaitUtil.sendKeys(By.id("password"), "valid_password");
            WaitUtil.click(By.cssSelector("input[type='submit']"));
            WaitUtil.waitForVisible(By.id("username"));
        }

        LoggerUtil.info("Verify lockout/error message");
        String actualErrorMsg = WaitUtil.getTextOrEmpty(By.xpath("//p[@class='message error LoginForm']"));
        String expectedErrorMsg =
                "You have used 4 out of 5 login attempts. After all 5 have been used, you will be unable to login for 15 minutes.";
        Assert.assertEquals(actualErrorMsg, expectedErrorMsg, "Error message is not displayed as expected");
    }

    @Test
    public void TC06() {
        System.out.println("TC06 - Additional pages display once user logged in");
        LoggerUtil.info("TC06 - Additional pages display once user logged in");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();
        LoggerUtil.info("Login with valid account");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Verify tabs displayed");
        Assert.assertTrue(WaitUtil.isVisible(By.linkText("My ticket")),
                "My ticket tab is not displayed");
        Assert.assertTrue(WaitUtil.isVisible(By.linkText("Change password")),
                "Change password tab is not displayed");
        Assert.assertTrue(WaitUtil.isVisible(By.linkText("Logout")),
                "Log out tab is not displayed");

        LoggerUtil.info("Open My ticket page");
        WaitUtil.click(By.linkText("My ticket"));
        MyTicketPage myTicketPage = new MyTicketPage();
        Assert.assertTrue(myTicketPage.isMyTicketPageDisplayed(), "User is not redirected to My ticket page");

        LoggerUtil.info("Open Change password page");
        WaitUtil.click(By.linkText("Change password"));
        ChangePasswordPage changePasswordPage = new ChangePasswordPage();
        Assert.assertTrue(changePasswordPage.isChangePasswordPageDisplayed(),
                "User is not redirected to Change password page");
    }

    @Test
    public void TC08() {
        System.out.println("TC08 - User can't login with an account hasn't been activated");
        LoggerUtil.info("TC08 - User can't login with an account hasn't been activated");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Login Page");
        LoginPage loginPage = homePage.gotoLoginPage();

        LoggerUtil.info("Enter inactive username and password");
        loginPage.getTxtUsername().sendKeys(Constant.INACTIVATED_USERNAME);
        loginPage.getTxtPassword().sendKeys(Constant.INACTIVATED_PASSWORD);

        LoggerUtil.info("Click Login button");
        loginPage.getBtnLogin().click();

        LoggerUtil.info("Verify inactive account error message");
        String actualErrorMsg = WaitUtil.getTextOrEmpty(
                By.xpath("//p[@class='message error LoginForm']")
        );
        String expectedErrorMsg = "Invalid username or password. Please try again.";
        Assert.assertEquals(actualErrorMsg, expectedErrorMsg,
                "Error message is not displayed as expected");
    }
}