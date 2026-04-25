package common;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScreenshotListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult result) {
        LoggerUtil.log.error("Test failed: " + result.getName());

        if (Constant.WEBDRIVER == null) {
            LoggerUtil.log.warn("WEBDRIVER is null, cannot capture screenshot");
            return;
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String methodName = result.getName();

        try {
            File scrFile = ((TakesScreenshot) Constant.WEBDRIVER).getScreenshotAs(OutputType.FILE);

            String reportDirectory = System.getProperty("user.dir")
                    + File.separator + "target"
                    + File.separator + "surefire-reports";

            File screenshotDir = new File(reportDirectory + File.separator + "failure_screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            String screenshotName = methodName + "_" + formatter.format(calendar.getTime()) + ".png";
            File destFile = new File(screenshotDir, screenshotName);

            FileUtils.copyFile(scrFile, destFile);

            Reporter.log(
                    "<div style='margin:10px'>" +
                            "<p><b>Screenshot:</b></p>" +
                            "<a href='failure_screenshots/" + screenshotName + "' target='_blank'>" +
                            "<img src='failure_screenshots/" + screenshotName + "' height='200' style='border:1px solid #ccc'/>" +
                            "</a>" +
                            "</div>"
            );

            LoggerUtil.info("Screenshot saved at: " + destFile.getAbsolutePath());

        } catch (IOException e) {
            LoggerUtil.log.error("Failed to save screenshot", e);
            e.printStackTrace();
        }
    }
}