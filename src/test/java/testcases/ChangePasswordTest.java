package testcases;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
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
import pageobjects.HomePage;
import pageobjects.LoginPage;

import java.time.Duration;

import static common.Constant.WEBDRIVER;

public class ChangePasswordTest {

    private WebDriverWait wait;

    @BeforeMethod
    public void beforeTest() {
        LoggerUtil.info("Start ChangePasswordTest");
        WEBDRIVER = new ChromeDriver();
        WEBDRIVER.manage().window().fullscreen();
        wait = new WebDriverWait(WEBDRIVER, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("End test");
        if (Constant.WEBDRIVER != null) WEBDRIVER.quit();
    }

    @Test
    public void TC09() {
        LoggerUtil.info("TC09 - Change password");

        HomePage homePage = new HomePage();
        homePage.open();

        LoggerUtil.info("Login");
        LoginPage loginPage = homePage.gotoLoginPage();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        LoggerUtil.info("Open change password");
        WebElement changePasswordLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@id='menu']//a[@href='/Account/ChangePassword.cshtml']")));
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", changePasswordLink);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("currentPassword")));

        LoggerUtil.info("Input data");
        WEBDRIVER.findElement(By.id("currentPassword")).sendKeys(Constant.PASSWORD);
        WEBDRIVER.findElement(By.id("newPassword")).sendKeys("123456789012");
        WEBDRIVER.findElement(By.id("confirmPassword")).sendKeys("123456789012");

        LoggerUtil.info("Submit");
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[value='Change Password']")));
        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", btn);

        LoggerUtil.info("Verify result");
        String actual = WaitUtil.getTextOrEmpty(By.xpath("//p[@class='message success']"));
        Assert.assertEquals(actual, "Your password has been updated",
                "Success message mismatch");
    }
}