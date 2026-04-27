package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LoginPage extends GeneralPage {

    // Locators chính xác theo cấu trúc thực tế của website
    private final By _lblTitle = By.xpath("//div[div[text()='ĐĂNG NHẬP']]");
    private final By _lblEmail = By.xpath("//label[contains(.,'Tên đăng nhập')]");
    private final By _lblPassword = By.xpath("//label[contains(.,'Mật khẩu')]");

    private final By _txtEmail = By.id("loginEmail");
    private final By _txtPassword = By.id("loginPassword");
    private final By _btnLogin = By.xpath("//button[contains(.,'Đăng nhập')]");

    private final By _linkForgetPassword = By.xpath("//span[contains(.,'Quên mật khẩu')]");
    private final By _linkRegister = By.xpath("//span[contains(.,'Bạn chưa có tài khoản? Đăng ký tài khoản')]");

    // Locators cho thông báo lỗi
    private final By _lblEmailError = By.id("loginEmailError");
    private final By _lblPasswordError = By.id("loginPasswordError");
    private final By _toast = By.id("toastNotification");
    private final By _toastVisible = By.xpath("//*[@id='toastNotification' and contains(@class,'show')]");

    // Elements
    public WebElement getLblTitle() { return WaitUtil.waitForVisible(_lblTitle); }
    public WebElement getLblEmail() { return WaitUtil.waitForVisible(_lblEmail); }
    public WebElement getLblPassword() { return WaitUtil.waitForVisible(_lblPassword); }

    public WebElement getTxtEmail() { return WaitUtil.waitForVisible(_txtEmail); }
    public WebElement getTxtPassword() { return WaitUtil.waitForVisible(_txtPassword); }
    public WebElement getBtnLogin() { return WaitUtil.waitForVisible(_btnLogin); }

    public WebElement getLinkForgetPassword() { return WaitUtil.waitForVisible(_linkForgetPassword); }
    public WebElement getLinkRegister() { return WaitUtil.waitForVisible(_linkRegister); }

    public String getGeneralErrorMsg() {
        // 1. Lỗi inline email trống
        if (WaitUtil.isVisible(_lblEmailError, 2)) {
            String msg = Constant.WEBDRIVER.findElement(_lblEmailError).getText().trim();
            if (!msg.isEmpty()) return msg;
        }
        // 2. Lỗi inline password trống
        if (WaitUtil.isVisible(_lblPasswordError, 2)) {
            String msg = Constant.WEBDRIVER.findElement(_lblPasswordError).getText().trim();
            if (!msg.isEmpty()) return msg;
        }
        // 3. Toast — lấy text trực tiếp qua JS, không phụ thuộc vào class 'show'
        //    vì toast có thể hiện/tắt rất nhanh trong 3 giây
        try {
            String toastText = (String) ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript("return document.getElementById('toastNotification')?.textContent?.trim() || ''");
            if (toastText != null && !toastText.isEmpty()) return toastText;
        } catch (Exception ignored) {}
        return "";
    }

    // Các hàm hành động
    public void login(String email, String password) {
        LoggerUtil.info("Đang đăng nhập với email: " + email);
        getTxtEmail().clear();
        getTxtEmail().sendKeys(email);
        getTxtPassword().clear();
        getTxtPassword().sendKeys(password);
        WaitUtil.click(_btnLogin);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // chờ API response + toast hiển thị
    }

    public void clickForgetPassword() {
        LoggerUtil.info("Click vào 'Quên mật khẩu'");
        getLinkForgetPassword().click();
    }

    public void clickRegister() {
        LoggerUtil.info("Click vào 'Đăng ký tài khoản'");
        getLinkRegister().click();
    }
}