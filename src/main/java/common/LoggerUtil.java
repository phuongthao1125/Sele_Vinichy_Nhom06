package common;

import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Reporter;

public class LoggerUtil {

    public static final Logger log = LogManager.getLogger(LoggerUtil.class);

    private static ExtentTest getTest() {
        try {
            return ExtentManager.getTest();
        } catch (Exception e) {
            return null;
        }
    }

    public static void info(String message) {
        log.info(message);
        Reporter.log(message, true); // giữ lại (TestNG)

        ExtentTest test = getTest();
        if (test != null) {
            test.info(message); // 🔥 thêm vào Extent
        }
    }

    public static void error(String message) {
        log.error(message);
        Reporter.log("<span style='color:red'>" + message + "</span>", true);

        ExtentTest test = getTest();
        if (test != null) {
            test.fail(message); // 🔥 thêm vào Extent
        }
    }

    public static void warn(String message) {
        log.warn(message);
        Reporter.log("<span style='color:orange'>" + message + "</span>", true);

        ExtentTest test = getTest();
        if (test != null) {
            test.warning(message);
        }
    }
}