package common;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentListener implements ITestListener {

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        LoggerUtil.info("Create Extent test node: " + result.getMethod().getMethodName());
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName());
        test.set(extentTest);
        LoggerUtil.info("START TEST: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LoggerUtil.info("Mark test as passed in Extent: " + result.getMethod().getMethodName());
        test.get().pass("Test Passed");
        LoggerUtil.info("PASS TEST: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get().fail(result.getThrowable());

        if (Constant.WEBDRIVER != null) {
            String base64 = ((TakesScreenshot) Constant.WEBDRIVER)
                    .getScreenshotAs(OutputType.BASE64);

            test.get().addScreenCaptureFromBase64String(base64, "Failure Screenshot");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LoggerUtil.warn("Mark test as skipped in Extent: " + result.getMethod().getMethodName());
        test.get().skip("Test Skipped");
        LoggerUtil.warn("SKIP TEST: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LoggerUtil.info("Flush Extent report");
        extent.flush();
        LoggerUtil.info("Extent report flushed successfully");
    }
}