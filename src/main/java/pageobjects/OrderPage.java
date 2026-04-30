package pageobjects;

import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;

public class OrderPage extends GeneralPage {

    // Breadcrumbs
    private final By _lnkHomeBreadcrumb = By.xpath("//div[contains(@class,'breadcrumb')]//a[contains(text(),'Trang chủ')]");
    private final By _lnkCartBreadcrumb = By.xpath("//div[contains(@class,'breadcrumb')]//a[contains(text(),'Giỏ hàng')]");
    private final By _lblOrderBreadcrumb = By.xpath("//div[contains(@class,'breadcrumb')]//span[contains(text(),'Đặt hàng')]");

    // Shipping Info
    private final By _btnChangeAddress = By.id("btnChangeAddress");
    private final By _btnAddAddress = By.cssSelector(".btn-add-new-addr");
    private final By _txtAddName = By.id("addName");
    private final By _txtAddPhone = By.id("addPhone");
    private final By _txtAddAddress = By.id("addStreet");
    private final By _btnSubmitAddress = By.xpath("//button[contains(@onclick,'saveNewAddress')] | //button[contains(normalize-space(),'Thêm địa chỉ')]");
    private final By _listAddressItems = By.className("addr-list-item");
    private final By _btnCloseModal = By.cssSelector("#modalAddressList .modal-close");
    
    // Payment Method
    private final By _rbCOD = By.xpath("//input[@type='radio' and @checked]");
    
    // Order Notes
    private final By _txtOrderNote = By.id("orderNote");
    
    // Discount Code
    private final By _txtDiscountCode = By.id("promoCode");
    private final By _btnApplyDiscount = By.id("btnApplyPromo");
    
    // Order Button & Status
    private final By _btnOrder = By.xpath("//button[contains(@onclick,'submitOrder')] | //button[contains(normalize-space(),'ĐẶT HÀNG')]");
    private final By _lblSuccessMsg = By.xpath("//*[contains(normalize-space(),'Đặt hàng thành công')]");
    private final By _lblErrorMsg = By.className("error-msg");

    // Methods
    public void clickHomeBreadcrumb() {
        LoggerUtil.info("Click vào breadcrumb 'Trang chủ'");
        WaitUtil.click(_lnkHomeBreadcrumb);
    }

    public void clickCartBreadcrumb() {
        LoggerUtil.info("Click vào breadcrumb 'Giỏ hàng'");
        WaitUtil.click(_lnkCartBreadcrumb);
    }

    public boolean isBreadcrumbCorrect(String expectedPath) {
        LoggerUtil.info("Kiểm tra breadcrumb: " + expectedPath);
        String[] parts = expectedPath.split("[>/]");
        for (String part : parts) {
            String p = part.trim();
            if (!p.isEmpty() && !WaitUtil.isVisible(By.xpath("//*[contains(normalize-space(),'" + p + "')]"), 2)) {
                LoggerUtil.warn("Không tìm thấy phần tử breadcrumb: " + p);
                return false;
            }
        }
        return true;
    }

    public boolean handleShippingAddress() {
        LoggerUtil.info("Bắt đầu xử lý thông tin giao hàng");
        WaitUtil.click(_btnChangeAddress);
        try { Thread.sleep(1000); } catch (Exception ignored) {}

        // Kiểm tra nếu đã có danh sách địa chỉ
        if (WaitUtil.isVisible(_listAddressItems, 3)) {
            LoggerUtil.info("Phát hiện danh sách địa chỉ có sẵn. Chọn địa chỉ đầu tiên.");
            WaitUtil.click(_listAddressItems);
            // Sau khi chọn, cần đóng modal để quay lại màn hình đặt hàng
            WaitUtil.click(_btnCloseModal);
            return true; 
        } 
        
        // Nếu không có địa chỉ, nhấn Thêm mới
        if (WaitUtil.isVisible(_btnAddAddress, 3)) {
            LoggerUtil.info("Không có địa chỉ sẵn có. Tiến hành thêm mới.");
            WaitUtil.click(_btnAddAddress);
            return false; // Cần nhập thông tin mới
        }
        
        LoggerUtil.warn("Không xác định được trạng thái form địa chỉ");
        return false;
    }

    public void clickConfirmAddress() {
        LoggerUtil.info("Nhấn nút 'Xác nhận/Thêm' địa chỉ");
        WaitUtil.click(_btnSubmitAddress);
    }

    public void fillOrderInfo(String fullName, String phone, String address, String note) {
        if (fullName != null && !fullName.isEmpty()) {
            LoggerUtil.info("Nhập họ tên: " + fullName);
            WaitUtil.sendKeys(_txtAddName, fullName);
        }
        if (phone != null && !phone.isEmpty()) {
            LoggerUtil.info("Nhập số điện thoại: " + phone);
            WaitUtil.sendKeys(_txtAddPhone, phone);
        }
        if (address != null && !address.isEmpty()) {
            LoggerUtil.info("Nhập địa chỉ: " + address);
            WaitUtil.sendKeys(_txtAddAddress, address);
        }
        if (note != null && !note.isEmpty()) {
            LoggerUtil.info("Nhập ghi chú: " + note);
            WaitUtil.sendKeys(_txtOrderNote, note);
        }
    }

    public void clickOrder() {
        LoggerUtil.info("Click nút 'ĐẶT HÀNG'");
        WaitUtil.click(_btnOrder);
    }

    public String getSuccessMessage() {
        return WaitUtil.getTextOrEmpty(_lblSuccessMsg);
    }

    public String getErrorMessage() {
        // Ưu tiên lấy text từ element hiển thị lỗi
        String msg = WaitUtil.getTextOrEmpty(_lblErrorMsg);
        if (msg.isEmpty()) {
            // Thử lấy validation message từ các input (HTML5 required)
            try {
                msg = (String) ((org.openqa.selenium.JavascriptExecutor) common.Constant.WEBDRIVER)
                        .executeScript("return document.querySelector('input:invalid')?.validationMessage || ''");
            } catch (Exception ignored) {}
        }
        return msg;
    }
}
