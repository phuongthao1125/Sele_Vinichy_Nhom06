package common;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import java.io.File;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports getInstance() {
        if (extent == null) {
            // Đảm bảo thư mục reports tồn tại
            File reportDir = new File("reports");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            ExtentSparkReporter spark = new ExtentSparkReporter("reports/extent.html");
            spark.config().setReportName("Automation Test Report");
            spark.config().setDocumentTitle("Extent Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);
        }

        return extent;
    }
}