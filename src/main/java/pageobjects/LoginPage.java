package pageobjects;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class LoginPage extends GeneralPage {

    private final By _txtUsername = By.xpath("//input[@id='username']");
    private final By _txtPassword = By.xpath("//input[@id='password']");
    private final By _btnLogin = By.xpath("//input[@value='login']");
    private final By _lblLoginErrorMsg = By.xpath("//p[@class='message error LoginForm']");

    public WebElement getTxtUsername(){
        LoggerUtil.info("Find Username textbox");
        return Constant.WEBDRIVER.findElement(_txtUsername);
    }

    public WebElement getTxtPassword(){
        LoggerUtil.info("Find Password textbox");
        return Constant.WEBDRIVER.findElement(_txtPassword);
    }

    public WebElement getBtnLogin(){
        LoggerUtil.info("Find Login button");
        return Constant.WEBDRIVER.findElement(_btnLogin);
    }

    public WebElement getLblLoginErrorMsg(){
        LoggerUtil.info("Find Login error message");
        return Constant.WEBDRIVER.findElement(_lblLoginErrorMsg);
    }

    public HomePage login(String username, String password) {
        LoggerUtil.info("Login with username: " + username);

        this.getTxtUsername().sendKeys(username);
        this.getTxtPassword().sendKeys(password);

        scrollToElement(getBtnLogin());
        this.getBtnLogin().click();

        LoggerUtil.info("Click login button");

        return new HomePage();
    }

    private void scrollToElement(WebElement element) {
        LoggerUtil.info("Scroll to element (Login)");

        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
}