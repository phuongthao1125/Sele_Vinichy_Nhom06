package testcases;

import common.Constant;
import common.LoggerUtil;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.OrderHistoryPage;

import static common.Constant.WEBDRIVER;

public class HomePageTests {

    HomePage homePage = new HomePage();
    LoginPage loginPage = new LoginPage();

    @BeforeMethod
    public void beforeMethod() {
        LoggerUtil.info("Thiết lập WebDriver và mở Trang chủ");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WEBDRIVER = new ChromeDriver(options);
        WEBDRIVER.manage().window().maximize();
        WEBDRIVER.get(Constant.URL);
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("Quitting WebDriver");
        if (WEBDRIVER != null) {
            WEBDRIVER.quit();
        }
    }

    // =========================================================================
    // FUNCTIONAL TESTS (F0001 - F0018)
    // =========================================================================

    @Test(description = "TC01-F0001 - Mở trang chủ thành công")
    public void TC01_F0001_openHomePage() {
        LoggerUtil.info("START TEST: TC01-F0001");
        Assert.assertTrue(homePage.isBannerDisplayed(), "Trang chủ nên hiển thị banner chính");
        Assert.assertEquals(WEBDRIVER.getCurrentUrl(), Constant.URL, "URL nên là trang chủ");
    }

    @Test(description = "TC01-F0002 - Chọn 1 danh mục từ trang chủ")
    public void TC01_F0002_selectCategory() {
        LoggerUtil.info("START TEST: TC01-F0002");
        homePage.selectCategory("Balo");
        Assert.assertTrue(WEBDRIVER.getCurrentUrl().contains("loai=balo"), "Nên chuyển đến trang danh sách sản phẩm theo danh mục balo");
    }

    @Test(description = "TC01-F0003 - Mở chi tiết sản phẩm từ trang chủ")
    public void TC01_F0003_openProductDetail() {
        LoggerUtil.info("START TEST: TC01-F0003");
        homePage.openFeaturedProduct(0);
        Assert.assertTrue(WEBDRIVER.getCurrentUrl().contains("/san-pham/"), "Nên mở đúng trang chi tiết của sản phẩm");
    }

    @Test(description = "TC01-F0004 - Click nút Xem thêm sản phẩm")
    public void TC01_F0004_clickViewMore() {
        LoggerUtil.info("START TEST: TC01-F0004");
        homePage.clickViewMore();
        Assert.assertTrue(WEBDRIVER.getCurrentUrl().endsWith("/san-pham"), "Nên chuyển đến trang danh sách tất cả sản phẩm");
    }

    @Test(description = "TC01-F0005 - Tìm kiếm với từ khóa hợp lệ")
    public void TC01_F0005_searchProduct() {
        LoggerUtil.info("START TEST: TC01-F0005");
        String keyword = "Tui"; // Dùng ký tự ASCII để tránh URL encoding làm fail assertion
        homePage.search(keyword);
        String currentUrl = WEBDRIVER.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("q=") && currentUrl.contains("san-pham"),
                "Nên chuyển đến trang kết quả tìm kiếm");
    }

    @Test(description = "TC01-F0006 - Chọn danh mục trong popup sản phẩm")
    public void TC01_F0006_selectCategoryFromPopup() {
        LoggerUtil.info("START TEST: TC01-F0006");
        homePage.selectProductCategoryFromPopup("Ví");
        Assert.assertTrue(WEBDRIVER.getCurrentUrl().contains("loai=vi"), "Nên chuyển đến đúng trang danh sách sản phẩm tương ứng (Ví)");
    }

    @Test(description = "TC01-F0007 - Click menu Liên hệ")
    public void TC01_F0007_clickContactMenu() {
        LoggerUtil.info("START TEST: TC01-F0007");
        homePage.clickContactMenu();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        Object isAtContact = ((org.openqa.selenium.JavascriptExecutor) WEBDRIVER)
                .executeScript("var el = document.getElementById('contact'); " +
                        "var rect = el.getBoundingClientRect(); " +
                        "return (rect.top < window.innerHeight && rect.bottom > 0);");
        Assert.assertTrue((Boolean) isAtContact, "Trang nên cuộn xuống khu vực liên hệ");
    }

    @Test(description = "TC01-F0008 - Mở giỏ hàng khi đã đăng nhập")
    public void TC01_F0008_openCartLoggedIn() {
        LoggerUtil.info("START TEST: TC01-F0008");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openCart();
        Assert.assertTrue(WEBDRIVER.getCurrentUrl().contains("/gio-hang"), "Nên chuyển đến trang giỏ hàng");
    }

    @Test(description = "TC01-F0009 - Mở giỏ hàng khi chưa đăng nhập")
    public void TC01_F0009_openCartNotLoggedIn() {
        LoggerUtil.info("START TEST: TC01-F0009");
        homePage.openCart();
        Assert.assertTrue(homePage.isLoginPopupDisplayed(), "Nên hiển thị popup đăng nhập khi chưa đăng nhập");
    }

    @Test(description = "TC01-F0010 - Đăng nhập với thông tin hợp lệ")
    public void TC01_F0010_loginSuccess() {
        LoggerUtil.info("START TEST: TC01-F0010");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        Assert.assertTrue(homePage.isWelcomeDisplayed(), "Đăng nhập thành công, hiển thị trạng thái người dùng");
    }

    @Test(description = "TC01-F0011 - Mở lịch sử đơn hàng từ popup tài khoản")
    public void TC01_F0011_openOrderHistory() {
        LoggerUtil.info("START TEST: TC01-F0011");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openAccountMenu();
        WEBDRIVER.findElement(By.xpath("//button[contains(.,'Lịch sử đơn hàng')]")).click();
        OrderHistoryPage historyPage = new OrderHistoryPage();
        Assert.assertTrue(historyPage.isPageDisplayed(), "Nên chuyển đến trang lịch sử đơn hàng");
    }

    @Test(description = "TC01-F0012 - Click icon logo VINICHY")
    public void TC01_F0012_clickLogo() {
        LoggerUtil.info("START TEST: TC01-F0012");
        WEBDRIVER.get(Constant.URL + "san-pham");
        homePage.clickLogo();
        Assert.assertEquals(WEBDRIVER.getCurrentUrl(), Constant.URL, "Nên chuyển về trang chủ");
    }

    @Test(description = "TC01-F0013 - Click menu Trang chủ")
    public void TC01_F0013_clickHomeMenu() {
        LoggerUtil.info("START TEST: TC01-F0013");
        WEBDRIVER.get(Constant.URL + "san-pham");
        homePage.clickHomeMenu();
        Assert.assertEquals(WEBDRIVER.getCurrentUrl(), Constant.URL, "Nên chuyển về trang chủ");
    }

    @Test(description = "TC01-F0014 - Click icon tài khoản khi đã đăng nhập")
    public void TC01_F0014_clickAccountIconLoggedIn() {
        LoggerUtil.info("START TEST: TC01-F0014");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openAccountMenu();
        Assert.assertTrue(homePage.isAccountMenuDisplayed(), "Nên hiển thị popup tài khoản");
    }

    @Test(description = "TC01-F0015 - Click icon tài khoản khi chưa đăng nhập")
    public void TC01_F0015_clickAccountIconNotLoggedIn() {
        LoggerUtil.info("START TEST: TC01-F0015");
        homePage.openAccountMenu();
        Assert.assertTrue(homePage.isLoginPopupDisplayed(), "Nên hiển thị popup đăng nhập");
    }

    @Test(description = "TC01-F0016 - Đăng xuất tài khoản")
    public void TC01_F0016_logout() {
        LoggerUtil.info("START TEST: TC01-F0016");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.logout();
        Assert.assertFalse(homePage.isWelcomeDisplayed(), "Đăng xuất thành công, trở về trạng thái chưa đăng nhập");
    }

    @Test(description = "TC01-F0017 - Mở popup tài khoản khi đã đăng nhập")
    public void TC01_F0017_openAccountPopup() {
        LoggerUtil.info("START TEST: TC01-F0017");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openAccountMenu();
        Assert.assertTrue(WEBDRIVER.findElement(By.id("accountPopup")).getText().contains(Constant.USERNAME), "Popup tài khoản nên hiển thị email người dùng");
    }

    @Test(description = "TC01-F0018 - Click Sản phẩm ở footer")
    public void TC01_F0018_clickFooterProduct() {
        LoggerUtil.info("START TEST: TC01-F0018");
        // Footer 'Sản phẩm' gọi openProductFromFooter() - cuộn lên đầu trang và mở product popup
        // Không navigate sang /san-pham
        homePage.clickFooterProduct();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        String display = WEBDRIVER.findElement(By.id("productPopup")).getCssValue("display");
        Assert.assertEquals(display, "block", "Popup danh mục sản phẩm nên mở sau khi click footer");
    }

    // =========================================================================
    // GUI TESTS (UI0001 - UI0011)
    // =========================================================================

    @Test(description = "TC01-UI0001 - Mở thông tin giao hàng từ popup tài khoản")
    public void TC01_UI0001_openShippingInfo() {
        LoggerUtil.info("START TEST: TC01-UI0001");
        homePage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openShippingInfo();
        Assert.assertTrue(homePage.isShippingInfoDisplayed(), "Popup/thông tin giao hàng nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0002 - Hiển thị logo và tên thương hiệu")
    public void TC01_UI0002_displayLogoAndBrand() {
        LoggerUtil.info("START TEST: TC01-UI0002");
        Assert.assertTrue(homePage.isLogoDisplayed(), "Logo VINICHY nên hiển thị");
        Assert.assertTrue(homePage.isBrandDisplayed(), "Tên VINICHY nên hiển thị");
    }

    @Test(description = "TC01-UI0003 - Hiển thị menu điều hướng")
    public void TC01_UI0003_displayNavigationMenu() {
        LoggerUtil.info("START TEST: TC01-UI0003");
        Assert.assertTrue(homePage.isMenuDisplayed(), "Các mục Trang chủ / Sản phẩm / Liên hệ nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0004 - Hiển thị banner chính")
    public void TC01_UI0004_displayBanner() {
        LoggerUtil.info("START TEST: TC01-UI0004");
        Assert.assertTrue(homePage.isBannerDisplayed(), "Banner, tiêu đề, mô tả và hình ảnh chính nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0005 - Hiển thị danh sách danh mục")
    public void TC01_UI0005_displayCategories() {
        LoggerUtil.info("START TEST: TC01-UI0005");
        Assert.assertTrue(homePage.isCategoriesDisplayed(), "Danh sách danh mục nên hiển thị đầy đủ");
    }

    @Test(description = "TC01-UI0006 - Hiển thị danh sách sản phẩm nổi bật")
    public void TC01_UI0006_displayFeaturedProducts() {
        LoggerUtil.info("START TEST: TC01-UI0006");
        Assert.assertTrue(homePage.isFeaturedProductsDisplayed(), "Danh sách sản phẩm nổi bật nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0007 - Mở popup menu sản phẩm")
    public void TC01_UI0007_openProductPopup() {
        LoggerUtil.info("START TEST: TC01-UI0007");
        homePage.openProductPopup();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        String display = WEBDRIVER.findElement(By.id("productPopup")).getCssValue("display");
        Assert.assertEquals(display, "block", "Popup danh mục sản phẩm nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0008 - Mở popup đăng nhập khi chưa đăng nhập")
    public void TC01_UI0008_openLoginPopup() {
        LoggerUtil.info("START TEST: TC01-UI0008");
        homePage.openAccountMenu();
        Assert.assertTrue(homePage.isLoginPopupDisplayed(), "Popup đăng nhập nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0009 - Mở popup đăng ký")
    public void TC01_UI0009_openRegisterPopup() {
        LoggerUtil.info("START TEST: TC01-UI0009");
        homePage.openRegisterPopup();
        Assert.assertTrue(homePage.isRegisterPopupDisplayed(), "Popup đăng ký nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0010 - Mở popup quên mật khẩu")
    public void TC01_UI0010_openForgotPasswordPopup() {
        LoggerUtil.info("START TEST: TC01-UI0010");
        homePage.openForgotPassPopup();
        Assert.assertTrue(homePage.isForgotPassPopupDisplayed(), "Popup khôi phục mật khẩu nên hiển thị đúng");
    }

    @Test(description = "TC01-UI0011 - Hiển thị logo VINICHY")
    public void TC01_UI0011_displayVinichyLogo() {
        LoggerUtil.info("START TEST: TC01-UI0011");
        Assert.assertTrue(homePage.isLogoDisplayed(), "Logo VINICHY hiển thị rõ ràng, đúng vị trí");
    }
}
