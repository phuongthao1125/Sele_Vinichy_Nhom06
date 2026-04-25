package pageobjects;

import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static common.Constant.WEBDRIVER;

public class TimeTable extends GeneralPage {

    public void clickBookTicketHueToSaigon() {

        LoggerUtil.info("Start click Book Ticket Huế → Sài Gòn");

        WebDriverWait wait = new WebDriverWait(WEBDRIVER, Duration.ofSeconds(15));

        LoggerUtil.info("Current URL: " + WEBDRIVER.getCurrentUrl());
        LoggerUtil.info("Page title: " + WEBDRIVER.getTitle());

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//table//tbody//tr"), 23
        ));

        LoggerUtil.info("Table loaded successfully");

        String xpath = "//table//tbody//tr//a[contains(@href, 'BookTicketPage') " +
                "and contains(@href, 'id1=5') " +
                "and contains(@href, 'id2=1')]";

        WebElement link = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(xpath))
        );

        ((JavascriptExecutor) WEBDRIVER).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", link
        );

        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        LoggerUtil.info("Click Book Ticket link via JS");

        ((JavascriptExecutor) WEBDRIVER).executeScript("arguments[0].click();", link);
    }
}