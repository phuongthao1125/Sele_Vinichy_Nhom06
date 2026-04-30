package pageobjects;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class HomePage extends GeneralPage {

    private final By _lblWelcome = By.xpath("//*[contains(., 'TÀI KHOẢN CỦA TÔI')] | //*[contains(., 'Chào')]");

    public boolean isWelcomeDisplayed() {
        LoggerUtil.info("Kiểm tra trạng thái đăng nhập qua thuộc tính data-logged-in");
        try {
            // Chờ trang reload xong và data-logged-in = 'true'
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(10));
            wait.until(driver -> {
                Object status = ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return document.body.getAttribute('data-logged-in')");
                return "true".equals(status);
            });
            return true;
        } catch (Exception e) {
            LoggerUtil.warn("Không thể xác nhận đăng nhập thành công: " + e.getMessage());
            return false;
        }
    }

    public boolean isHomePageDisplayed() {
        return isWelcomeDisplayed();
    }
}