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

    @Test(description = "TC05-UI001 - Textbox Email")
    public void TC05_UI001() {
        LoggerUtil.info("START TEST: TC05-UI001");
        String actual = loginPage.getTxtEmail().getAttribute("placeholder").trim();
        String expected = "Nhập email";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertEquals(actual, expected, "Email placeholder is incorrect");
    }

    @Test(description = "TC05-UI002 - Textbox Password")
    public void TC05_UI002() {
        LoggerUtil.info("START TEST: TC05-UI002");
        String actualPlaceholder = loginPage.getTxtPassword().getAttribute("placeholder").trim();
        String actualType = loginPage.getTxtPassword().getAttribute("type").trim();
        String expectedPlaceholder = "Nhập mật khẩu";
        String expectedType = "password";
        
        LoggerUtil.info("Result: ExpPlaceholder=" + expectedPlaceholder + " | ActPlaceholder=" + actualPlaceholder);
        Assert.assertEquals(actualPlaceholder, expectedPlaceholder, "Password placeholder is incorrect");
        Assert.assertEquals(actualType, expectedType, "Password field should hide input");
    }

    @Test(description = "TC05-UI003 - Link Quên mật khẩu")
    public void TC05_UI003() {
        LoggerUtil.info("START TEST: TC05-UI003");
        String actual = loginPage.getLinkForgetPassword().getText().trim();
        String expected = "Quên mật khẩu";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertEquals(actual, expected, "Forget password link text is incorrect");
    }

    @Test(description = "TC05-UI004 - Button Đăng nhập")
    public void TC05_UI004() {
        LoggerUtil.info("START TEST: TC05-UI004");
        String actual = loginPage.getBtnLogin().getText().trim();
        if(actual.isEmpty()) actual = loginPage.getBtnLogin().getAttribute("value").trim();
        String expected = "Đăng nhập";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertTrue(actual.contains(expected), "Login button text is incorrect");
    }

    @Test(description = "TC05-UI005 - Link Đăng ký")
    public void TC05_UI005() {
        LoggerUtil.info("START TEST: TC05-UI005");
        String actual = loginPage.getLinkRegister().getText().trim();
        String expected = "Đăng ký tài khoản";
        
        LoggerUtil.info("Result: ActText contains '" + expected + "'");
        Assert.assertTrue(actual.contains(expected), "Register link text is incorrect");
    }

    @Test(description = "TC05-UI006 - Tiêu đề")
    public void TC05_UI006() {
        LoggerUtil.info("START TEST: TC05-UI006");
        String actual = loginPage.getLblTitle().getText().trim();
        String expected = "ĐĂNG NHẬP - Nhập email và mật khẩu của bạn:";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertTrue(actual.contains("ĐĂNG NHẬP"), "Page title is incorrect");
    }

    @Test(description = "TC05-UI007 - Label Email")
    public void TC05_UI007() {
        LoggerUtil.info("START TEST: TC05-UI007");
        String actual = loginPage.getLblEmail().getText().trim();
        String expected = "Tên đăng nhập";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertEquals(actual, expected, "Email label is incorrect");
    }

    @Test(description = "TC05-UI008 - Label Password")
    public void TC05_UI008() {
        LoggerUtil.info("START TEST: TC05-UI008");
        String actual = loginPage.getLblPassword().getText().trim();
        String expected = "Mật khẩu";
        
        LoggerUtil.info("Result: Exp=" + expected + " | Act=" + actual);
        Assert.assertEquals(actual, expected, "Password label is incorrect");
    }
}

