package common;

import org.openqa.selenium.WebDriver;

public class Constant {
    static {
        LoggerUtil.info("Load Constant class");
    }

    public static WebDriver WEBDRIVER;
    public static final String URL = "http://localhost:8080/";
    public static final String USERNAME = "phuongthao@gmail.com";
    public static final String PASSWORD = "123456";
}