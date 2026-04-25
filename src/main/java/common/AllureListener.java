package common;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class AllureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtil.error("ALLURE ATTACH SCREENSHOT FOR: " + result.getMethod().getMethodName());

        if (Constant.WEBDRIVER == null) {
            LoggerUtil.warn("WEBDRIVER is null, cannot attach screenshot to Allure");
            return;
        }

        LoggerUtil.info("Capture screenshot bytes for Allure report");
        byte[] screenshot = ((TakesScreenshot) Constant.WEBDRIVER).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot", new ByteArrayInputStream(screenshot));
        LoggerUtil.info("Screenshot attached to Allure report");
    }
}