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

import static common.Constant.WEBDRIVER;

public class ForgetPasswordTest {

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
    public void TC12() {
        System.out.println("TC12 - Errors display when password reset token is blank");
        LoggerUtil.info("TC12 - Errors display when password reset token is blank");

        LoggerUtil.info("Open Login page");
        HomePage homePage = new HomePage();
        homePage.open();
        homePage.gotoLoginPage();

        LoggerUtil.info("Click Forgot Password page");
        WaitUtil.click(By.linkText("Forgot Password page"));

        LoggerUtil.info("Enter email and submit forgot password form");
        WaitUtil.sendKeys(By.id("email"), Constant.USERNAME);
        WaitUtil.click(By.cssSelector("input[type='submit'][value='Send Instructions']"));

        LoggerUtil.info("Verify confirmation message displayed");
        String actualMsg = WaitUtil.getTextOrEmpty(
                By.xpath("//*[contains(text(),'Instructions have been sent')]")
        );
        Assert.assertTrue(actualMsg.contains("Instructions have been sent"),
                "Confirmation message not displayed. Actual message found: [" + actualMsg + "]");
    }

    @Test
    public void TC13() {
        System.out.println("TC13 - Errors display if password and confirm password don't match");
        LoggerUtil.info("TC13 - Errors display if password and confirm password don't match");

        LoggerUtil.info("Open Login page");
        HomePage homePage = new HomePage();
        homePage.open();
        homePage.gotoLoginPage();

        LoggerUtil.info("Click Forgot Password page");
        WaitUtil.click(By.linkText("Forgot Password page"));

        LoggerUtil.info("Enter email and submit forgot password form");
        WaitUtil.sendKeys(By.id("email"), Constant.USERNAME);
        WaitUtil.click(By.cssSelector("input[type='submit'][value='Send Instructions']"));

        LoggerUtil.info("Verify confirmation message displayed");
        String actualMsg = WaitUtil.getTextOrEmpty(
                By.xpath("//*[contains(text(),'Instructions have been sent')]")
        );
        Assert.assertTrue(actualMsg.contains("Instructions have been sent"),
                "Confirmation message not displayed. Actual message found: [" + actualMsg + "]");
    }
}