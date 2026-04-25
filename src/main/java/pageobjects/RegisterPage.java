package pageobjects;

import common.WaitUtil;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;

public class RegisterPage extends GeneralPage {

    private final By txtEmail = By.id("email");
    private final By txtPassword = By.id("password");
    private final By txtConfirmPassword = By.id("confirmPassword");
    private final By txtPid = By.id("pid");

    private final By btnRegister = By.xpath("//input[@value='Register']");

    private final By lblErrorMessage = By.xpath("//p[@class='message error']");
    private final By lblErrorPassword = By.xpath("//*[@id='RegisterForm']/fieldset/ol/li[2]/label[2]");
    private final By lblErrorPid = By.xpath("//*[@id='RegisterForm']/fieldset/ol/li[4]/label[2]");
    private final By lblSuccessMessage = By.xpath("//*[contains(text(),'Thank you for registering')]");

    public void enterEmail(String email) {
        LoggerUtil.info("Enter email: " + email);
        WaitUtil.sendKeys(txtEmail, email);
    }

    public void enterPassword(String password) {
        LoggerUtil.info("Enter password");
        WaitUtil.sendKeys(txtPassword, password);
    }

    public void enterConfirmPassword(String confirmPassword) {
        LoggerUtil.info("Enter confirm password");
        WaitUtil.sendKeys(txtConfirmPassword, confirmPassword);
    }

    public void enterPID(String pid) {
        LoggerUtil.info("Enter PID: " + pid);
        WaitUtil.sendKeys(txtPid, pid);
    }

    public void clickRegisterButton() {
        LoggerUtil.info("Click Register button");
        WaitUtil.click(btnRegister);
    }

    public void register(String email, String password, String confirmPassword, String passportNumber) {
        LoggerUtil.info("Register account flow");

        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(confirmPassword);
        enterPID(passportNumber);
    }

    public void registerNewAccount(String email, String password, String confirmPassword, String pid) {
        LoggerUtil.info("Register new account and submit");

        register(email, password, confirmPassword, pid);
        clickRegisterButton();
    }

    public String getRegisterErrorMessage() {
        LoggerUtil.info("Get register error message");
        return WaitUtil.waitForVisible(lblErrorMessage).getText().trim();
    }

    public String getPasswordErrorMessage() {
        LoggerUtil.info("Get password error message");
        return WaitUtil.waitForVisible(lblErrorPassword).getText().trim();
    }

    public String getPIDErrorMessage() {
        LoggerUtil.info("Get PID error message");
        return WaitUtil.waitForVisible(lblErrorPid).getText().trim();
    }

    public String getSuccessMessage() {
        LoggerUtil.info("Get success message");
        return WaitUtil.waitForVisible(lblSuccessMessage).getText().trim();
    }

    public boolean isErrorMessageDisplayed() {
        LoggerUtil.info("Check error message displayed");
        return isElementDisplayed(lblErrorMessage);
    }

    public boolean isSuccessMessageDisplayed() {
        LoggerUtil.info("Check success message displayed");
        return isElementDisplayed(lblSuccessMessage);
    }

    private boolean isElementDisplayed(By locator) {
        try {
            return WaitUtil.waitForVisible(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            LoggerUtil.warn("Element not displayed: " + locator);
            return false;
        }
    }
}