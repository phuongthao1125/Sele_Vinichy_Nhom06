package testcases;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;

import java.util.ArrayList;
import java.util.List;

import static common.Constant.WEBDRIVER;

public class LogoutTests {

    LoginPage loginPage = new LoginPage();
    HomePage homePage = new HomePage();

    @BeforeMethod
    public void beforeMethod() {
        LoggerUtil.info("Thiết lập WebDriver và mở Modal đăng nhập");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WEBDRIVER = new ChromeDriver(options);
        WEBDRIVER.manage().window().maximize();
        WEBDRIVER.get(Constant.URL);
        
        // Mở Modal đăng nhập
        loginPage.openAccountMenu();
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("Đóng WebDriver");
        if (WEBDRIVER != null) {
            WEBDRIVER.quit();
        }
    }

    @Test(description = "TC06-F0001 - Đăng xuất thành công")
    public void TC06_F0001() {
        LoggerUtil.info("START TEST: TC06-F0001");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        Assert.assertTrue(homePage.isWelcomeDisplayed(), "Đăng nhập không thành công");

        homePage.logout();
        
        // Sau khi logout, click vào icon tài khoản sẽ hiện lại modal đăng nhập
        homePage.openAccountMenu();
        boolean isLoggedOut = loginPage.getLblTitle().isDisplayed();
        
        String expected = "Hệ thống đăng xuất thành công";
        String actual = isLoggedOut ? "Hệ thống đăng xuất thành công" : "Vẫn còn trong trạng thái đăng nhập";
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        
        Assert.assertTrue(isLoggedOut, "Không hiển thị màn hình đăng nhập sau khi logout");
    }

    @Test(description = "TC06-F0002 - Kiểm tra phiên đăng nhập sau khi đăng xuất (Back button)")
    public void TC06_F0002() {
        LoggerUtil.info("START TEST: TC06-F0002");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        
        homePage.logout();
        
        LoggerUtil.info("Nhấn nút Back trên trình duyệt");
        WEBDRIVER.navigate().back();
        
        LoggerUtil.info("Kiểm tra xem có còn trong trạng thái đăng nhập không");
        boolean isAccountMenuVisible = homePage.isAccountMenuDisplayed();
        
        String expected = "Hệ thống không cho truy cập lại trang tài khoản";
        String actual = isAccountMenuVisible ? "Người dùng vẫn truy cập được trang tài khoản" : "Hệ thống chặn truy cập thành công";
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        
        Assert.assertFalse(isAccountMenuVisible, "Vẫn truy cập được trang tài khoản sau khi nhấn Back");
    }

    @Test(description = "TC06-F0003 - Kiểm tra điều hướng khi nhấn Đăng xuất")
    public void TC06_F0003() {
        LoggerUtil.info("START TEST: TC06-F0003");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        
        homePage.logout();
        String currentUrl = WEBDRIVER.getCurrentUrl();
        
        String expected = "Hệ thống điều hướng đúng";
        String actual = currentUrl.contains(Constant.URL) ? "Hệ thống điều hướng đúng" : "Điều hướng sai: " + currentUrl;
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        
        Assert.assertTrue(currentUrl.contains(Constant.URL), "Hệ thống phải điều hướng về trang chủ hoặc trang login");
    }

    @Test(description = "TC06-F0004 - Logout nhiều tab")
    public void TC06_F0004() {
        LoggerUtil.info("START TEST: TC06-F0004");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        
        LoggerUtil.info("Mở tab thứ 2");
        WEBDRIVER.switchTo().newWindow(WindowType.TAB);
        WEBDRIVER.get(Constant.URL);
        
        List<String> tabs = new ArrayList<>(WEBDRIVER.getWindowHandles());
        
        LoggerUtil.info("Logout ở tab 2");
        homePage.logout();
        
        LoggerUtil.info("Quay lại tab 1 và refresh");
        WEBDRIVER.switchTo().window(tabs.get(0));
        WEBDRIVER.navigate().refresh();
        
        LoggerUtil.info("Kiểm tra tab 1 đã bị logout chưa");
        homePage.openAccountMenu();
        boolean isLoggedOut = loginPage.getLblTitle().isDisplayed();
        
        String expected = "Tab 1 bị logout";
        String actual = isLoggedOut ? "Tab 1 bị logout" : "Tab 1 vẫn còn đăng nhập";
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        
        Assert.assertTrue(isLoggedOut, "Tab 1 vẫn còn đăng nhập sau khi Tab 2 logout");
    }

    @Test(description = "TC06-UI001 - Kiểm tra các thành phần hiển thị của popup tài khoản")
    public void TC06_UI001() {
        LoggerUtil.info("START TEST: TC06_UI001");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        homePage.openAccountMenu();
        
        // 1. Kiểm tra Tiêu đề
        String title = homePage.getLblAccountMenuTitle().getText().trim();
        Assert.assertEquals(title, "TÀI KHOẢN CỦA TÔI", "Tiêu đề popup tài khoản không đúng");
        
        // 2. Kiểm tra Avatar (chữ cái)
        WebElement avatar = homePage.getLetterAvatar();
        Assert.assertTrue(avatar.isDisplayed(), "Avatar không hiển thị");
        Assert.assertFalse(avatar.getText().trim().isEmpty(), "Avatar không hiển thị chữ cái");
        
        // 3. Kiểm tra Email người dùng
        String email = homePage.getLblUserEmail().getText().trim();
        Assert.assertEquals(email, Constant.USERNAME, "Email hiển thị không đúng");
        
        // 4. Kiểm tra các mục chức năng
        Assert.assertTrue(homePage.getBtnOrderHistory().isDisplayed(), "Mục Lịch sử đơn hàng không hiển thị");
        Assert.assertTrue(homePage.getBtnShippingInfo().isDisplayed(), "Mục Thông tin giao hàng không hiển thị");
        Assert.assertTrue(homePage.getBtnLogout().isDisplayed(), "Mục Đăng xuất không hiển thị");
        
        // Kiểm tra text của các mục
        Assert.assertEquals(homePage.getBtnOrderHistory().getText().trim(), "Lịch sử đơn hàng");
        Assert.assertEquals(homePage.getBtnShippingInfo().getText().trim(), "Thông tin giao hàng");
        Assert.assertTrue(homePage.getBtnLogout().getText().trim().contains("Đăng xuất"));
    }
}


