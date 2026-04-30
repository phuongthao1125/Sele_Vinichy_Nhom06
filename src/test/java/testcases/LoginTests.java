package testcases;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.HomePage;
import pageobjects.LoginPage;

import static common.Constant.WEBDRIVER;

public class LoginTests {

    LoginPage loginPage = new LoginPage();

    @BeforeMethod
    public void beforeMethod() {
        LoggerUtil.info("Thiết lập WebDriver và mở Modal đăng nhập");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WEBDRIVER = new ChromeDriver(options);
        WEBDRIVER.manage().window().maximize();
        WEBDRIVER.get(Constant.URL);
        
        // Mở Modal đăng nhập trước khi thực hiện các bước tiếp theo
        loginPage.openAccountMenu();
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("Quitting WebDriver");
        if (WEBDRIVER != null) {
            WEBDRIVER.quit();
        }
    }

    @Test(description = "TC05-F0001 - Đăng nhập thành công")
    public void TC05_F0001() {
        LoggerUtil.info("START TEST: TC05-F0001");
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        
        HomePage homePage = new HomePage();
        boolean isDisplayed = homePage.isWelcomeDisplayed();
        
        String expected = "Đăng nhập thành công";
        String actual = isDisplayed ? "Đăng nhập thành công" : "Đăng nhập thất bại";
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        
        Assert.assertTrue(isDisplayed, "Welcome message should be displayed after successful login");
    }

    @Test(description = "TC05-F0002 - Trống email")
    public void TC05_F0002() {
        LoggerUtil.info("START TEST: TC05-F0002");
        loginPage.login("", Constant.PASSWORD);
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Không được để trống ô này!";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi trống email không đúng");
    }

    @Test(description = "TC05-F0003 - Trống mật khẩu")
    public void TC05_F0003() {
        LoggerUtil.info("START TEST: TC05-F0003");
        loginPage.login(Constant.USERNAME, "");
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Không được để trống ô này!";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi trống mật khẩu không đúng");
    }

    @Test(description = "TC05-F0004 - Trống cả 2")
    public void TC05_F0004() {
        LoggerUtil.info("START TEST: TC05-F0004");
        loginPage.login("", "");
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Không được để trống ô này!";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi trống cả 2 trường không đúng");
    }

    @Test(description = "TC05-F0005 - Email sai định dạng")
    public void TC05_F0005() {
        LoggerUtil.info("START TEST: TC05-F0005");
        loginPage.login("phuongthaogmail.com", Constant.PASSWORD);
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Tên đăng nhập hoặc mật khẩu không hợp lệ. Vui lòng nhập lại";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi định dạng email không đúng");
    }

    @Test(description = "TC05-F0006 - Email không tồn tại")
    public void TC05_F0006() {
        LoggerUtil.info("START TEST: TC05-F0006");
        loginPage.login("abcdef@gmail.com", Constant.PASSWORD);
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Tên đăng nhập hoặc mật khẩu không hợp lệ. Vui lòng nhập lại";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi email không tồn tại không đúng");
    }

    @Test(description = "TC05-F0007 - Sai mật khẩu")
    public void TC05_F0007() {
        LoggerUtil.info("START TEST: TC05-F0007");
        loginPage.login(Constant.USERNAME, "111111");
        
        String actualMsg = loginPage.getGeneralErrorMsg().trim();
        String expectedMsg = "Tên đăng nhập hoặc mật khẩu không hợp lệ. Vui lòng nhập lại";
        
        LoggerUtil.info("Result: Exp=" + expectedMsg + " | Act=" + actualMsg);
        Assert.assertTrue(actualMsg.contains(expectedMsg), "Thông báo lỗi sai mật khẩu không đúng");
    }

    @Test(description = "TC05-F0008 - Chuyển trang quên mật khẩu")
    public void TC05_F0008() {
        LoggerUtil.info("START TEST: TC05-F0008");
        loginPage.clickForgetPassword();

        WebElement popup = Constant.WEBDRIVER.findElement(By.id("forgotPopup"));
        boolean isActive = popup.getAttribute("class").contains("active");
        LoggerUtil.info("Result: forgotPopup active=" + isActive);
        Assert.assertTrue(isActive, "Should show forgot password popup");
    }

    @Test(description = "TC05-F0009 - Chuyển trang đăng ký")
    public void TC05_F0009() {
        LoggerUtil.info("START TEST: TC05-F0009");
        loginPage.clickRegister();

        WebElement popup = Constant.WEBDRIVER.findElement(By.id("registerPopup"));
        boolean isActive = popup.getAttribute("class").contains("active");
        LoggerUtil.info("Result: registerPopup active=" + isActive);
        Assert.assertTrue(isActive, "Should show register popup");
    }

    @Test(description = "TC05-UI001 - Kiểm tra các thành phần hiển thị của popup đăng nhập")
    public void TC05_UI001() {
        LoggerUtil.info("START TEST: TC05-UI001");
        
        // 1. Kiểm tra Tiêu đề
        String title = loginPage.getLblTitle().getText().trim();
        Assert.assertEquals(title, "ĐĂNG NHẬP", "Tiêu đề popup không đúng");
        
        // 2. Kiểm tra Mô tả
        String desc = loginPage.getLblDescription().getText().trim();
        Assert.assertEquals(desc, "Nhập email và mật khẩu của bạn", "Mô tả popup không đúng");
        
        // 3. Kiểm tra Label và Input Email
        Assert.assertEquals(loginPage.getLblEmail().getText().trim(), "Tên đăng nhập");
        Assert.assertEquals(loginPage.getTxtEmail().getAttribute("placeholder"), "Nhập email");
        
        // 4. Kiểm tra Label và Input Password
        Assert.assertEquals(loginPage.getLblPassword().getText().trim(), "Mật khẩu");
        Assert.assertEquals(loginPage.getTxtPassword().getAttribute("placeholder"), "Nhập mật khẩu");
        Assert.assertEquals(loginPage.getTxtPassword().getAttribute("type"), "password");
        
        // 5. Kiểm tra Link Quên mật khẩu
        Assert.assertEquals(loginPage.getLinkForgetPassword().getText().trim(), "Quên mật khẩu");
        
        // 6. Kiểm tra Nút Đăng nhập
        Assert.assertTrue(loginPage.getBtnLogin().getText().contains("Đăng nhập"), "Nút Đăng nhập không đúng text");
        
        // 7. Kiểm tra Link Đăng ký
        Assert.assertTrue(loginPage.getLinkRegister().getText().contains("Đăng ký tài khoản"), "Link đăng ký không đúng text");
    }
}

