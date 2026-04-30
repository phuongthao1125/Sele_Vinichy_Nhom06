package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class GeneralPage {

    private final By _imgAccount = By.xpath("//img[@alt='Tài khoản']");
    private final By _lblAccountMenuTitle = By.xpath("//h5[contains(.,'TÀI KHOẢN CỦA TÔI')]");
    private final By _letterAvatar = By.className("letter-avatar");
    private final By _lblUserEmail = By.xpath("//div[@id='accountPopup']//p[contains(text(), '@')]");
    private final By _btnOrderHistory = By.xpath("//button[contains(.,'Lịch sử đơn hàng')]");
    private final By _btnShippingInfo = By.id("btnShippingInfo");
    private final By _btnLogout = By.xpath("//p[contains(.,'Đăng xuất')]");

    public WebElement getBtnLogout() {
        // Dùng JS tránh lỗi encoding UTF-8 với XPath tiếng Việt khi chạy Maven
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
        WebElement btn = (WebElement) js.executeScript(
                "return Array.from(document.querySelectorAll('p,button,a,span'))" +
                        ".find(el => el.textContent.trim().includes('Đăng xuất'));"
        );
        if (btn != null) return btn;
        // Fallback
        return WaitUtil.waitForVisible(By.xpath("//*[contains(@onclick,'doLogout') or contains(@onclick,'logout')]"));
    }
    public WebElement getLblAccountMenuTitle() { return WaitUtil.waitForVisible(_lblAccountMenuTitle); }
    public WebElement getLetterAvatar() { return WaitUtil.waitForVisible(_letterAvatar); }
    public WebElement getLblUserEmail() { return WaitUtil.waitForVisible(_lblUserEmail); }
    public WebElement getBtnOrderHistory() { return WaitUtil.waitForVisible(_btnOrderHistory); }
    public WebElement getBtnShippingInfo() { return WaitUtil.waitForVisible(_btnShippingInfo); }

    public void open(String url) {
        LoggerUtil.info("Mở URL: " + url);
        Constant.WEBDRIVER.navigate().to(url);
        WaitUtil.waitForPageLoad();
    }

    public void openAccountMenu() {
        // Theo HTML mới: Login Modal dùng <div>, Account Popup dùng <h5>
        By loginTitle = By.xpath("//div[text()='ĐĂNG NHẬP']");
        By accountTitle = By.xpath("//h5[contains(.,'TÀI KHOẢN CỦA TÔI')]");
        
        // Kiểm tra xem menu đã thực sự hiển thị chưa
        boolean isMenuOpen = WaitUtil.isVisible(loginTitle, 1) || WaitUtil.isVisible(accountTitle, 1);
        
        if (!isMenuOpen) {
            LoggerUtil.info("Mở menu tài khoản");
            WaitUtil.click(_imgAccount);
            
            // Chờ menu/modal hiển thị
            WaitUtil.isVisible(By.xpath("//div[text()='ĐĂNG NHẬP'] | //h5[contains(.,'TÀI KHOẢN CỦA TÔI')]"), 5);
            
            // Thêm một khoảng nghỉ ngắn cho animation của modal
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }
    }

    public void logout() {
        LoggerUtil.info("Thực hiện đăng xuất");
        openAccountMenu();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER;
        WebElement btn = (WebElement) js.executeScript(
                "return Array.from(document.querySelectorAll('p,button,a,span'))" +
                        ".find(el => el.textContent.trim().includes('Đăng xuất'));"
        );
        if (btn != null) btn.click();
        else Constant.WEBDRIVER.findElement(By.xpath("//*[contains(@onclick,'doLogout')]")).click();

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
    }

    public boolean isAccountMenuDisplayed() {
        // Kiểm tra xem Account Popup (dành cho người đã login) có hiển thị không
        return WaitUtil.isVisible(By.xpath("//h5[contains(.,'TÀI KHOẢN CỦA TÔI')]"), 2);
    }

    protected void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }
}