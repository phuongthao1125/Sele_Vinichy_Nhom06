package pageobjects;

import common.LoggerUtil;
import org.openqa.selenium.By;

import static common.Constant.WEBDRIVER;

public class ChangePasswordPage extends GeneralPage {

    private final By changePasswordTab = By.xpath("//a[@href='/Account/ChangePassword']");

    public ChangePasswordPage clickChangePasswordTab() {
        LoggerUtil.info("Click Change Password tab");
        WEBDRIVER.findElement(changePasswordTab).click();
        return this;
    }

    public boolean isChangePasswordPageDisplayed() {
        LoggerUtil.info("Check Change Password page displayed");
        return WEBDRIVER.getCurrentUrl().contains("ChangePassword");
    }
}