package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class OrderDetailPage extends GeneralPage {

    private final By _pageTitle = By.xpath("//h1[contains(.,'CHI TIẾT ĐƠN HÀNG')]");
    private final By _orderStatus = By.cssSelector(".status-text");
    private final By _orderCode = By.xpath("//div[contains(@class,'order-meta')]//span[contains(text(),'Mã đơn hàng')]/strong");
    private final By _orderDate = By.xpath("//div[contains(@class,'order-meta')]//span[contains(text(),'Ngày đặt')]/strong");
    private final By _productItems = By.cssSelector(".cart-item-detail");
    private final By _totalAmount = By.xpath("//div[contains(@class,'summary-total')]/span[contains(@class,'total-price-red')]");
    private final By _subtotalAmount = By.xpath("//div[contains(@class,'summary-row')]/span[contains(text(),'đ')]");
    
    // Account
    private final By _accountName = By.xpath("//h3[text()='Tài khoản']/following-sibling::div//b");
    private final By _accountEmail = By.xpath("//h3[text()='Tài khoản']/following-sibling::div//span");
    
    // Shipping
    private final By _shippingInfo = By.cssSelector(".shipping-info-content");
    private final By _recipientName = By.xpath("//span[text()='Người nhận:']/following-sibling::strong");
    private final By _phoneNumber = By.xpath("//span[text()='Số điện thoại:']/following-sibling::span");
    private final By _address = By.xpath("//span[text()='Địa chỉ:']/following-sibling::span");
    
    // Payment
    private final By _paymentMethod = By.xpath("//h3[text()='Phương thức thanh toán']/following-sibling::div//span[contains(text(),'Thanh toán')]");
    
    // Note
    private final By _noteBox = By.cssSelector(".note-box");
    
    // Action buttons
    private final By _cancelButton = By.cssSelector(".btn-cancel");
    private final By _contactButton = By.cssSelector(".btn-contact");
    
    // Modal
    private final By _cancelModal = By.cssSelector(".cancel-modal");
    private final By _cancelModalTitle = By.xpath("//div[contains(@class,'cancel-box')]/h3");
    private final By _cancelReasonInput = By.cssSelector(".cancel-reason-input");
    private final By _btnConfirmCancel = By.id("btnConfirmCancel");
    private final By _btnCancelModalNo = By.cssSelector(".btn-cancel-modal-no");

    public boolean isPageDisplayed() { return WaitUtil.isVisible(_pageTitle, 10); }
    public String getOrderStatus() { return WaitUtil.waitForVisible(_orderStatus).getText().trim(); }
    public String getOrderCode() { return WaitUtil.waitForVisible(_orderCode).getText().trim(); }
    public String getOrderDate() { return WaitUtil.waitForVisible(_orderDate).getText().trim(); }
    public int getProductCount() { return Constant.WEBDRIVER.findElements(_productItems).size(); }
    public String getTotalAmountText() { return WaitUtil.waitForVisible(_totalAmount).getText().trim(); }
    public String getSubtotalAmountText() { return WaitUtil.waitForVisible(_subtotalAmount).getText().trim(); }
    
    public String getAccountName() { return WaitUtil.waitForVisible(_accountName).getText().trim(); }
    public String getAccountEmail() { return WaitUtil.waitForVisible(_accountEmail).getText().trim(); }
    public boolean isShippingInfoDisplayed() { return WaitUtil.isVisible(_shippingInfo, 5); }
    public String getRecipientName() { return WaitUtil.waitForVisible(_recipientName).getText().trim(); }
    public String getPhoneNumber() { return WaitUtil.waitForVisible(_phoneNumber).getText().trim(); }
    public String getAddress() { return WaitUtil.waitForVisible(_address).getText().trim(); }
    public String getPaymentMethod() { return WaitUtil.waitForVisible(_paymentMethod).getText().trim(); }
    public String getNote() { return WaitUtil.waitForVisible(_noteBox).getText().trim(); }

    public boolean isContactButtonDisplayed() { return WaitUtil.isVisible(_contactButton, 5); }
    public void clickContactButton() { WaitUtil.click(_contactButton); }
    
    public boolean isCancelButtonDisplayed() { return WaitUtil.isVisible(_cancelButton, 5); }
    public boolean isCancelButtonEnabled() { return WaitUtil.waitForVisible(_cancelButton).isEnabled(); }
    public void clickCancelButton() { WaitUtil.click(_cancelButton); }

    public void openCancelModal() {
        LoggerUtil.info("Mở popup hủy đơn hàng");
        WaitUtil.click(_cancelButton);
        WaitUtil.isVisible(_cancelModal, 5);
    }

    public void cancelOrder(String reason) {
        openCancelModal();
        WaitUtil.sendKeys(_cancelReasonInput, reason);
        WaitUtil.click(_btnConfirmCancel);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    public boolean isCancelModalDisplayed() { return WaitUtil.isVisible(_cancelModal, 5); }
    public String getCancelModalTitle() { return WaitUtil.waitForVisible(_cancelModalTitle).getText().trim(); }
    public boolean isCancelReasonInputDisplayed() { return WaitUtil.isVisible(_cancelReasonInput, 5); }
    public boolean isConfirmCancelButtonDisplayed() { return WaitUtil.isVisible(_btnConfirmCancel, 5); }
    public boolean isCancelModalNoButtonDisplayed() { return WaitUtil.isVisible(_btnCancelModalNo, 5); }
    
    public void inputCancelReason(String reason) {
        WaitUtil.sendKeys(_cancelReasonInput, reason);
    }
    public void confirmCancel() {
        WaitUtil.click(_btnConfirmCancel);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }
    public void dismissCancelModal() {
        WaitUtil.click(_btnCancelModalNo);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }

    public boolean areProductDetailsDisplayed() {
        List<WebElement> items = Constant.WEBDRIVER.findElements(_productItems);
        if (items.isEmpty()) return false;
        WebElement firstItem = items.get(0);
        return firstItem.findElement(By.cssSelector(".cart-img")).isDisplayed() &&
               firstItem.findElement(By.cssSelector(".cart-info-title")).isDisplayed() &&
               firstItem.findElement(By.cssSelector(".cart-info-price")).isDisplayed() &&
               firstItem.findElement(By.cssSelector(".cart-info-qty")).isDisplayed();
    }
}
