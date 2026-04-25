package pageobjects;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class GeneralPage {

    private final By tabLogin = By.xpath("//div[@id='menu']//a[@href='/Account/Login.cshtml']");
    private final By tabLogout = By.xpath("//div[@id='menu']//a[@href='/Account/Logout']");
    private final By lblwelcomeMessage = By.xpath("//div[@class='account']/strong");

    protected WebElement getTabLogin() {
        return Constant.WEBDRIVER.findElement(tabLogin);
    }

    protected WebElement getTabLogout() {
        return Constant.WEBDRIVER.findElement(tabLogout);
    }

    protected WebElement getLblWelcomeMessage() {
        return Constant.WEBDRIVER.findElement(lblwelcomeMessage);
    }

    public String getWelcomeMessage() {
        LoggerUtil.info("Get welcome message");
        return this.getLblWelcomeMessage().getText();
    }

    public LoginPage gotoLoginPage() {
        LoggerUtil.info("Navigate to Login Page");

        scrollToElement(getTabLogin());
        this.getTabLogin().click();

        return new LoginPage();
    }

    private void scrollToElement(WebElement element) {
        LoggerUtil.info("Scroll to element");

        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
}