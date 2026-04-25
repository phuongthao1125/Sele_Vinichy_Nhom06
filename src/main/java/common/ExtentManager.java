package common;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        LoggerUtil.log.info("Get ExtentReports instance");

        if (extent == null) {
            LoggerUtil.info("Create new ExtentReports instance");

            ExtentSparkReporter spark = new ExtentSparkReporter("reports/extent.html");
            spark.config().setReportName("Automation Test Report");
            spark.config().setDocumentTitle("Extent Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            LoggerUtil.info("ExtentReports initialized successfully");
        }

        return extent;
    }
}