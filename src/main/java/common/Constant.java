package common;

import org.openqa.selenium.WebDriver;

public class Constant {
    static {
        LoggerUtil.info("Load Constant class");
    }

    public static WebDriver WEBDRIVER;
    public static final String RAILWAY_URL = "http://railwayb1.somee.com";
    public static final String USERNAME = "phanthanhthao892005@gmail.com";
    public static final String PASSWORD = "1234567890123";
    public static final String RESET_PASSWORD_URL = "http://railwayb1.somee.com/Account/ChangePassword.cs";
    public static final String INACTIVATED_USERNAME = "tester_nhom06@gmail.com";
    public static final String INACTIVATED_PASSWORD = "123456789";
}