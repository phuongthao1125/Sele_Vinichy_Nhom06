package testcases;

import common.Constant;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    @BeforeMethod
    public void setUp() {
        System.out.println("🚀 Start browser");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        Constant.WEBDRIVER = driver;
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("❌ Close browser");

        if (Constant.WEBDRIVER != null) {
            Constant.WEBDRIVER.quit();
        }
    }
}