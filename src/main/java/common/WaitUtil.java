package common;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WaitUtil {

    private static final int TIMEOUT_SECONDS = 15;

    private static WebDriverWait getWait() {
        LoggerUtil.info("Create WebDriverWait with timeout: " + TIMEOUT_SECONDS + " seconds");
        return new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(TIMEOUT_SECONDS));
    }

    public static WebElement waitForVisible(By locator) {
        LoggerUtil.info("Wait for visible: " + locator);
        try {
            return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LoggerUtil.error("=== TIMEOUT waiting for: " + locator);
            LoggerUtil.error("=== Current URL: " + Constant.WEBDRIVER.getCurrentUrl());
            LoggerUtil.error("=== Page title: " + Constant.WEBDRIVER.getTitle());
            throw e;
        }
    }

    // Thêm vào WaitUtil.java

    public static String getTextOrEmpty(By locator) {
        LoggerUtil.info("Get text from element: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(TIMEOUT_SECONDS));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            return element.getText();
        } catch (TimeoutException | NoSuchElementException e) {
            String summary = "";
            try {
                // Ưu tiên lấy text từ các element thông báo phổ biến
                String[] candidates = {
                        "//p[contains(@class,'message')]",
                        "//p[contains(@class,'error')]",
                        "//p[contains(@class,'success')]",
                        "//h1", "//h2",
                        "//div[@class='title']"
                };
                for (String xpath : candidates) {
                    try {
                        WebElement el = Constant.WEBDRIVER.findElement(By.xpath(xpath));
                        String text = el.getText().trim();
                        if (!text.isEmpty()) {
                            summary = text;
                            break;
                        }
                    } catch (Exception ignored) {}
                }
                // Nếu không tìm được gì thì lấy dòng đầu tiên không rỗng của body
                if (summary.isEmpty()) {
                    String bodyText = Constant.WEBDRIVER.findElement(By.tagName("body")).getText();
                    for (String line : bodyText.split("\n")) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            summary = line;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
            LoggerUtil.warn("Element not found: " + locator + " → Actual page shows: [" + summary + "]");
            return summary;
        }
    }

    public static boolean isVisible(By locator) {
        return isVisible(locator, TIMEOUT_SECONDS);
    }

    public static boolean isVisible(By locator, int timeout) {
        try {
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static WebElement waitForClickable(By locator) {
        LoggerUtil.info("Wait for clickable: " + locator);
        try {
            return getWait().until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            LoggerUtil.error("=== TIMEOUT waiting for clickable: " + locator);
            LoggerUtil.error("=== Current URL: " + Constant.WEBDRIVER.getCurrentUrl());
            LoggerUtil.error("=== Page title: " + Constant.WEBDRIVER.getTitle());
            throw e;
        }
    }

    public static boolean waitForText(By locator, String text) {
        LoggerUtil.info("Wait for text [" + text + "] in element: " + locator);
        try {
            return getWait().until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            LoggerUtil.error("=== TIMEOUT waiting for text [" + text + "] in: " + locator);
            LoggerUtil.error("=== Current URL: " + Constant.WEBDRIVER.getCurrentUrl());
            LoggerUtil.error("=== Page title: " + Constant.WEBDRIVER.getTitle());
            try {
                String actualText = Constant.WEBDRIVER.findElement(locator).getText();
                LoggerUtil.error("=== Actual text found in element: " + actualText);
            } catch (NoSuchElementException ex) {
                LoggerUtil.error("=== Element not found at all: " + locator);
            }
            throw e;
        }
    }

    public static void click(By locator) {
        LoggerUtil.info("Click element: " + locator);
        WebElement element = waitForClickable(locator);

        try {
            element.click();
            LoggerUtil.info("Click success: " + locator);
        } catch (StaleElementReferenceException | ElementNotInteractableException e) {
            LoggerUtil.warn("Normal click failed (" + e.getClass().getSimpleName() + "), retry with JS click: " + locator);
            try {
                element = waitForPresent(locator);
                JavascriptExecutor js = (JavascriptExecutor) Constant.WEBDRIVER;
                js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
                js.executeScript("arguments[0].click();", element);
                LoggerUtil.info("JS click success: " + locator);
            } catch (Exception ex) {
                LoggerUtil.error("JS click also failed: " + ex.getMessage());
                throw e;
            }
        }
    }

    public static void waitForPageLoad() {
        LoggerUtil.info("Wait for page to load completely");
        getWait().until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public static void sendKeys(By locator, String text) {
        LoggerUtil.info("Send keys to element: " + locator + " | text: " + text);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        LoggerUtil.info("Send keys success: " + locator);
    }

    public static WebElement waitForPresent(By locator) {
        LoggerUtil.info("Wait for present: " + locator);
        try {
            return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            LoggerUtil.error("=== TIMEOUT waiting for presence of: " + locator);
            throw e;
        }
    }

    public static boolean isNotVisible(By locator, int timeout) {
        try {
            return new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(timeout))
                    .until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            return false;
        }
    }
}