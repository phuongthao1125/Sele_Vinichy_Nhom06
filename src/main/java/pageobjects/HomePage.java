package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class HomePage extends GeneralPage {

    private final By _lblWelcome = By.xpath("//*[contains(., 'TÀI KHOẢN CỦA TÔI')] | //*[contains(., 'Chào')]");
    private final By _heroSection = By.className("hero");
    private final By _imgBanner = By.cssSelector(".hero-img-box img");
    private final By _catBoxes = By.className("cat-box");
    private final By _featuredProducts = By.className("product-card");
    private final By _btnViewMore = By.className("btn-view");

    public boolean isWelcomeDisplayed() {
        LoggerUtil.info("Kiểm tra trạng thái đăng nhập qua thuộc tính data-logged-in");
        try {
            // Chờ trang reload xong và data-logged-in = 'true' với timeout 15 giây
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(15));
            wait.until(driver -> {
                Object status = ((org.openqa.selenium.JavascriptExecutor) driver)
                        .executeScript("return document.body.getAttribute('data-logged-in')");
                LoggerUtil.info("Current data-logged-in status: " + status);
                return "true".equals(status);
            });
            LoggerUtil.info("Login confirmed successfully");
            return true;
        } catch (Exception e) {
            LoggerUtil.warn("Không thể xác nhận đăng nhập thành công: " + e.getMessage());
            // Thêm một lần check nữa trước khi fail
            try {
                Object status = ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                        .executeScript("return document.body.getAttribute('data-logged-in')");
                LoggerUtil.info("Final data-logged-in status: " + status);
            } catch (Exception e2) {
                LoggerUtil.warn("Could not check data-logged-in: " + e2.getMessage());
            }
            return false;
        }
    }

    public boolean isHomePageDisplayed() {
        return isBannerDisplayed();
    }

    public boolean isBannerDisplayed() {
        return WaitUtil.isVisible(_heroSection, 5) && WaitUtil.isVisible(_imgBanner, 5);
    }

    public boolean isCategoriesDisplayed() {
        return WaitUtil.isVisible(_catBoxes, 5);
    }

    public boolean isFeaturedProductsDisplayed() {
        return WaitUtil.isVisible(_featuredProducts, 5);
    }

    public void selectCategory(String categoryName) {
        LoggerUtil.info("Chọn danh mục: " + categoryName);
        By catLocator = By.xpath("//span[@class='cat-title' and contains(text(),'" + categoryName + "')]");
        WaitUtil.click(catLocator);
    }

    public void openFeaturedProduct(int index) {
        LoggerUtil.info("Mở sản phẩm nổi bật thứ " + (index + 1));
        Constant.WEBDRIVER.findElements(_featuredProducts).get(index).click();
    }

    public void clickViewMore() {
        LoggerUtil.info("Click Xem thêm sản phẩm");
        WaitUtil.click(_btnViewMore);
    }

    public void selectProductCategoryFromPopup(String categoryName) {
        LoggerUtil.info("Chọn danh mục trong popup: " + categoryName);
        openProductPopup();
        By catLocator = By.xpath("//div[@id='productPopup']//a[contains(.,'" + categoryName + "')]");
        WaitUtil.click(catLocator);
    }
}