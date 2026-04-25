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
import pageobjects.HomePage;
import pageobjects.RegisterPage;

import static common.Constant.WEBDRIVER;

public class RegisterTest {

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
    public void TC07() {
        System.out.println("TC07 - User can create new account");
        LoggerUtil.info("TC07 - User can create new account");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Click Register");
        WaitUtil.click(By.linkText("Register"));

        String email = "test" + System.currentTimeMillis() + "@gmail.com";
        String password = "123456789";
        String passportNumber = "123456789";

        LoggerUtil.info("Enter registration data");
        WaitUtil.sendKeys(By.id("email"), email);
        WaitUtil.sendKeys(By.id("password"), password);
        WaitUtil.sendKeys(By.id("confirmPassword"), password);
        WaitUtil.sendKeys(By.id("pid"), passportNumber);

        LoggerUtil.info("Submit registration form");
        WaitUtil.click(By.xpath("//input[@value='Register']"));

        LoggerUtil.info("Verify success message");
        String actualMsg = WaitUtil.getTextOrEmpty(
                By.xpath("//*[contains(text(),'Thank you for registering')]")
        );
        Assert.assertTrue(actualMsg.contains("Thank you for registering"),
                "Failed to create new account. Actual message found: [" + actualMsg + "]");
    }

    @Test
    public void TC10() {
        System.out.println("TC10 - Confirm password not match");
        LoggerUtil.info("TC10 - Confirm password not match");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Register Page");
        RegisterPage registerPage = homePage.gotoRegisterPage();

        String email = "test" + System.currentTimeMillis() + "@gmail.com";
        String password = "123456";
        String confirmPassword = "999999";
        String passportNumber = "123456789";

        LoggerUtil.info("Enter register data with mismatched confirm password");
        registerPage.register(email, password, confirmPassword, passportNumber);
        LoggerUtil.info("Click Register button");
        registerPage.clickRegisterButton();

        LoggerUtil.info("Verify error message");
        Assert.assertTrue(WaitUtil.isVisible(By.xpath("//p[@class='message error']")),
                "Error message is not displayed as expected");
    }

    @Test
    public void TC11() {
        System.out.println("TC11 - Password & PID empty");
        LoggerUtil.info("TC11 - Password & PID empty");

        LoggerUtil.info("Open Home Page");
        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Go to Register Page");
        RegisterPage registerPage = homePage.gotoRegisterPage();

        String email = "test" + System.currentTimeMillis() + "@gmail.com";

        LoggerUtil.info("Enter email only, leave password and PID empty");
        WaitUtil.sendKeys(By.id("email"), email);
        WaitUtil.sendKeys(By.id("password"), "");
        WaitUtil.sendKeys(By.id("confirmPassword"), "");
        WaitUtil.sendKeys(By.id("pid"), "");

        LoggerUtil.info("Click Register button");
        WaitUtil.click(By.xpath("//input[@value='Register']"));

        LoggerUtil.info("Verify main error message");
        Assert.assertTrue(WaitUtil.isVisible(By.xpath("//p[contains(@class,'error')]")),
                "Main error message not displayed");

        LoggerUtil.info("Verify password error message");
        Assert.assertTrue(WaitUtil.isVisible(By.xpath("//*[contains(text(),'Invalid password length')]")),
                "Password error message not displayed");

        LoggerUtil.info("Verify PID error message");
        Assert.assertTrue(WaitUtil.isVisible(By.xpath("//*[contains(text(),'Invalid ID length')]")),
                "PID error message not displayed");
    }
}