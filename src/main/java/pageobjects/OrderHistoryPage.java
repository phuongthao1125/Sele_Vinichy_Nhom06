package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OrderHistoryPage extends GeneralPage {

    private final By _firstOrderDate = By.cssSelector(".order-card:first-of-type .ch-code");
    private final By _secondOrderDate = By.cssSelector(".order-card:nth-of-type(2) .ch-code");
    private final By _breadcrumbHome = By.xpath("//div[contains(@class,'breadcrumb')]//a[text()='Trang chủ']");
    private final By _representativeImage = By.cssSelector(".order-card:first-of-type img");
    private final By _pageTitle = By.xpath("//h1[contains(.,'LỊCH SỬ ĐƠN HÀNG')]");
    private final By _orderCards = By.cssSelector(".order-card");
    private final By _cancelledOrderCard = By.xpath("//div[contains(@class,'order-card')][.//span[contains(normalize-space(.),'Đã hủy')]]");
    private final By _emptyState = By.xpath("//div[contains(@class,'empty-history')]/p[contains(.,'Bạn chưa có đơn hàng')]");
    private final By _firstOrderCode = By.cssSelector(".order-card:first-of-type .ch-code");
    private final By _firstOrderStatus = By.cssSelector(".order-card:first-of-type .ch-right span");
    private final By _firstOrderTotal = By.cssSelector(".order-card:first-of-type .ct-footer strong");
    private final By _viewMoreButtons = By.cssSelector(".order-card .view-more");
    private final By _breadcrumb = By.xpath("//div[contains(@class,'breadcrumb')]");

    public void openOrderHistoryPage() {
        LoggerUtil.info("Mở trang Lịch sử đơn hàng");
        open(Constant.URL + "lich-su-don-hang");
    }

    public boolean isPageDisplayed() {
        return WaitUtil.isVisible(_pageTitle, 10);
    }

    public int getOrderCount() {
        List<WebElement> cards = Constant.WEBDRIVER.findElements(_orderCards);
        return cards.size();
    }

    public boolean isEmptyHistoryDisplayed() {
        return WaitUtil.isVisible(_emptyState, 5);
    }

    public String getFirstOrderCode() {
        return WaitUtil.waitForVisible(_firstOrderCode).getText().trim();
    }

    public String getFirstOrderStatus() {
        return WaitUtil.waitForVisible(_firstOrderStatus).getText().trim();
    }

    public String getFirstOrderTotal() {
        return WaitUtil.waitForVisible(_firstOrderTotal).getText().trim();
    }

    public boolean hasExpandButtonForFirstOrder() {
        return !Constant.WEBDRIVER.findElements(_viewMoreButtons).isEmpty();
    }

    public void openFirstOrderDetail() {
        LoggerUtil.info("Mở chi tiết đơn hàng đầu tiên");
        WaitUtil.click(_orderCards);
    }

    public boolean hasCancelledOrder() {
        return !Constant.WEBDRIVER.findElements(_cancelledOrderCard).isEmpty();
    }

    public void openCancelledOrderDetail() {
        LoggerUtil.info("Mở chi tiết đơn hàng đã hủy");
        if (!hasCancelledOrder()) {
            throw new IllegalStateException("Không tìm thấy đơn hàng đã hủy trong lịch sử đơn hàng");
        }
        WaitUtil.click(_cancelledOrderCard);
    }

    public boolean isNoOrdersMessageDisplayed() {
        return isEmptyHistoryDisplayed();
    }

    public String getFirstOrderDate() {
        return WaitUtil.waitForVisible(_firstOrderDate).getText().trim();
    }

    public String getSecondOrderDate() {
        return WaitUtil.waitForVisible(_secondOrderDate).getText().trim();
    }

    public boolean isSortedByDateDescending(String firstDate, String secondDate) {
        try {
            int id1 = Integer.parseInt(firstDate.replace("#HD", "").trim());
            int id2 = Integer.parseInt(secondDate.replace("#HD", "").trim());
            return id1 >= id2;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickBreadcrumbHome() {
        LoggerUtil.info("Click breadcrumb về trang chủ");
        WaitUtil.click(_breadcrumbHome);
    }

    public boolean isTitleDisplayed() {
        return WaitUtil.isVisible(_pageTitle, 5);
    }

    public boolean isRepresentativeImageDisplayed() {
        return WaitUtil.isVisible(_representativeImage, 5);
    }

    public boolean isOrderStatusDisplayed() {
        return WaitUtil.isVisible(_firstOrderStatus, 5);
    }

    public boolean isTotalDisplayed() {
        return WaitUtil.isVisible(_firstOrderTotal, 5);
    }

    public boolean isBreadcrumbDisplayed() {
        return WaitUtil.isVisible(_breadcrumb, 5);
    }

    public String getPageTitle() {
        return WaitUtil.waitForVisible(_pageTitle).getText().trim();
    }

    public void clickViewMoreForFirstOrder() {
        LoggerUtil.info("Click Xem thêm cho đơn hàng đầu tiên");
        WaitUtil.click(_viewMoreButtons);
    }

    public void clickCollapseForFirstOrder() {
        LoggerUtil.info("Click Thu gọn cho đơn hàng đầu tiên");
        WaitUtil.click(_viewMoreButtons);
    }

    public boolean isExpandedForFirstOrder() {
        List<WebElement> hiddenItems = Constant.WEBDRIVER.findElements(By.cssSelector(".order-card:first-of-type .product-item.hidden"));
        return hiddenItems.isEmpty();
    }

    public boolean isCollapsedForFirstOrder() {
        List<WebElement> hiddenItems = Constant.WEBDRIVER.findElements(By.cssSelector(".order-card:first-of-type .product-item.hidden"));
        return !hiddenItems.isEmpty();
    }

    public boolean isProductInfoDisplayed() {
        List<WebElement> items = Constant.WEBDRIVER.findElements(By.cssSelector(".order-card:first-of-type .product-item"));
        if (items.isEmpty()) return false;
        WebElement firstItem = items.get(0);
        return firstItem.findElement(By.cssSelector(".product-name")).isDisplayed() &&
               firstItem.findElement(By.cssSelector(".product-price")).isDisplayed() &&
               firstItem.findElement(By.cssSelector(".product-qty")).isDisplayed();
    }
}
