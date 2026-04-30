package pageobjects;

import common.Constant;
import common.LoggerUtil;
import common.WaitUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class DeliveryInfoPage extends GeneralPage {

    // Locators for Shipping Info List Modal
    private final By _btnShippingInfo = By.id("btnShippingInfo");
    private final By _modalList = By.id("siModalList");
    private final By _lblListTitle = By.xpath("//div[@id='siModalList']//h3");
    private final By _btnCloseList = By.xpath("//div[@id='siModalList']//button[@class='si-close']");
    private final By _btnAddAddress = By.xpath("//button[@onclick='siOpenAdd()']");
    private final By _addrItems = By.xpath("//div[contains(@class,'si-addr-item')]");
    private final By _emptyMsg = By.xpath("//div[contains(@class,'si-empty')]");

    // Locators for Add Address Modal
    private final By _modalAdd = By.id("siModalAdd");
    private final By _lblAddTitle = By.xpath("//div[@id='siModalAdd']//h3");
    private final By _txtNameAdd = By.id("siAddName");
    private final By _txtPhoneAdd = By.id("siAddPhone");
    private final By _txtStreetAdd = By.id("siAddStreet");
    private final By _chkDefaultAdd = By.id("siAddDefault");
    private final By _btnConfirmAdd = By.xpath("//div[@id='siModalAdd']//button[contains(@class,'si-btn-confirm')]");
    private final By _btnCancelAdd = By.xpath("//div[@id='siModalAdd']//button[contains(@class,'si-btn-cancel')]");
    private final By _btnCloseAdd = By.xpath("//div[@id='siModalAdd']//button[@class='si-close']");
    private final By _lblAddName = By.xpath("//div[@id='siModalAdd']//label[contains(.,'Họ và tên')]");
    private final By _lblAddPhone = By.xpath("//div[@id='siModalAdd']//label[contains(.,'Số điện thoại')]");
    private final By _lblAddStreet = By.xpath("//div[@id='siModalAdd']//label[contains(.,'Địa chỉ giao hàng')]");

    // Locators for Edit Address Modal
    private final By _modalEdit = By.id("siModalEdit");
    private final By _txtNameEdit = By.id("siEditName");
    private final By _txtPhoneEdit = By.id("siEditPhone");
    private final By _txtStreetEdit = By.id("siEditStreet");
    private final By _chkDefaultEdit = By.id("siEditDefault");
    private final By _btnConfirmEdit = By.xpath("//div[@id='siModalEdit']//button[contains(@class,'si-btn-confirm')]");
    private final By _btnCancelEdit = By.xpath("//div[@id='siModalEdit']//button[contains(@class,'si-btn-cancel')]");
    private final By _btnCloseEdit = By.xpath("//div[@id='siModalEdit']//button[@class='si-close']");

    // Locators for Delete Confirmation
    private final By _modalDelete = By.id("deleteAddressConfirmModal");
    private final By _btnConfirmDelete = By.id("btnConfirmDeleteAddress");
    private final By _btnCancelDelete = By.xpath("//div[@id='deleteAddressConfirmModal']//button[@class='btn-no']");

    public void openShippingInfo() {
        LoggerUtil.info("Mở modal Thông tin giao hàng");
        openAccountMenu();
        WaitUtil.click(_btnShippingInfo);
        WaitUtil.waitForVisible(_modalList);
    }

    public void prepareCheckout() {
        LoggerUtil.info("Chuẩn bị màn hình Đặt hàng");
        WaitUtil.waitForPageLoad();
        Constant.WEBDRIVER.get(Constant.URL + "dat-hang");
        WaitUtil.waitForPageLoad();
        try { Thread.sleep(1000); } catch (Exception e) {}
        
        if (Constant.WEBDRIVER.getCurrentUrl().contains("gio-hang") || Constant.WEBDRIVER.getCurrentUrl().equals(Constant.URL)) {
            LoggerUtil.info("Giỏ hàng trống, thêm sản phẩm mẫu");
            Constant.WEBDRIVER.get(Constant.URL + "san-pham/1");
            WaitUtil.waitForPageLoad();
            try { Thread.sleep(1000); } catch (Exception e) {}
            
            By btnAdd = By.className("btn-add-cart");
            WaitUtil.click(btnAdd);
            try { Thread.sleep(1500); } catch (Exception e) {}
            
            Constant.WEBDRIVER.get(Constant.URL + "dat-hang");
            WaitUtil.waitForPageLoad();
            try { Thread.sleep(1000); } catch (Exception e) {}
        }
    }

    public void openShippingInfoFromCheckout() {
        LoggerUtil.info("Mở modal Thông tin giao hàng từ màn hình Checkout");
        prepareCheckout();
        By btnChange = By.id("btnChangeAddress");
        WaitUtil.click(btnChange);
        WaitUtil.waitForVisible(_modalList);
    }

    public void deleteFirstAddressFromCheckout() {
        LoggerUtil.info("Xóa địa chỉ đầu tiên từ màn hình Checkout");
        WaitUtil.waitForPageLoad();
        By btnDel = By.xpath("(//button[contains(@class,'btn-addr-del')])[1]");
        WaitUtil.click(btnDel);
        try {
            Thread.sleep(1000);
            Constant.WEBDRIVER.switchTo().alert().accept();
            LoggerUtil.info("Đã xác nhận alert xóa");
            Thread.sleep(1500);
            WaitUtil.waitForPageLoad();
        } catch (Exception e) {
            LoggerUtil.warn("Không thấy alert hoặc lỗi khi xóa: " + e.getMessage());
        }
    }

    public void clickAddNewAddress() {
        LoggerUtil.info("Click '+ Thêm địa chỉ mới'");
        WaitUtil.click(_btnAddAddress);
        WaitUtil.waitForVisible(_modalAdd);
    }

    public void fillAddForm(String name, String phone, String street, boolean isDefault) {
        LoggerUtil.info("Nhập form thêm địa chỉ: " + name + ", " + phone + ", " + street);
        WaitUtil.sendKeys(_txtNameAdd, name);
        WaitUtil.sendKeys(_txtPhoneAdd, phone);
        WaitUtil.sendKeys(_txtStreetAdd, street);
        WebElement chk = Constant.WEBDRIVER.findElement(_chkDefaultAdd);
        if (chk.isSelected() != isDefault) {
            chk.click();
        }
    }

    public void submitAddForm() {
        LoggerUtil.info("Click 'Thêm địa chỉ'");
        WaitUtil.click(_btnConfirmAdd);
        if (!WaitUtil.isNotVisible(_modalAdd, 5)) {
            String msg = getToastMessage();
            throw new RuntimeException("Modal Thêm địa chỉ không đóng sau khi click Confirm. Có thể do lỗi validate hoặc trùng lặp: " + msg);
        }
    }

    public void cancelAddForm() {
        LoggerUtil.info("Click 'Hủy' trên form thêm");
        WaitUtil.click(_btnCancelAdd);
    }

    public void closeAddPopup() {
        LoggerUtil.info("Click 'X' trên form thêm");
        WaitUtil.click(_btnCloseAdd);
    }

    public void fillEditForm(String name, String phone, String street, boolean isDefault) {
        LoggerUtil.info("Nhập form cập nhật: " + name + ", " + phone + ", " + street);
        WaitUtil.sendKeys(_txtNameEdit, name);
        WaitUtil.sendKeys(_txtPhoneEdit, phone);
        WaitUtil.sendKeys(_txtStreetEdit, street);
        WebElement chk = Constant.WEBDRIVER.findElement(_chkDefaultEdit);
        if (chk.isSelected() != isDefault) {
            chk.click();
        }
    }

    public void submitEditForm() {
        LoggerUtil.info("Click 'Lưu thay đổi'");
        WaitUtil.click(_btnConfirmEdit);
        if (!WaitUtil.isNotVisible(_modalEdit, 5)) {
            String msg = getToastMessage();
            throw new RuntimeException("Modal Chỉnh sửa không đóng sau khi click Lưu. Có thể do lỗi validate hoặc trùng lặp: " + msg);
        }
    }

    public void cancelEditForm() {
        LoggerUtil.info("Click 'Hủy' trên form cập nhật");
        WaitUtil.click(_btnCancelEdit);
    }

    public void closeEditPopup() {
        LoggerUtil.info("Click 'X' trên form cập nhật");
        WaitUtil.click(_btnCloseEdit);
    }

    public void deleteFirstAddress() {
        LoggerUtil.info("Xóa địa chỉ đầu tiên trong danh sách");
        By btnDel = By.xpath("(//button[@class='si-btn-del'])[1]");
        WaitUtil.click(btnDel);
        WaitUtil.waitForVisible(_modalDelete);
        WaitUtil.click(_btnConfirmDelete);
    }

    public void cancelDeleteFirstAddress() {
        LoggerUtil.info("Hủy xóa địa chỉ");
        By btnDel = By.xpath("(//button[@class='si-btn-del'])[1]");
        WaitUtil.click(btnDel);
        WaitUtil.waitForVisible(_modalDelete);
        WaitUtil.click(_btnCancelDelete);
    }

    public void editFirstAddress() {
        LoggerUtil.info("Mở form chỉnh sửa địa chỉ đầu tiên");
        By btnEdit = By.xpath("(//button[@class='si-btn-edit'])[1]");
        WaitUtil.click(btnEdit);
        WaitUtil.waitForVisible(_modalEdit);
    }

    public boolean isAddressDisplayed(String name, String phone, String street) {
        String xpath = String.format("//div[@class='si-addr-info'][.//div[contains(@class,'ai-name') and contains(text(),'%s')] and .//div[contains(@class,'ai-phone') and contains(text(),'%s')] and .//div[contains(@class,'ai-street') and contains(text(),'%s')]]", name, phone, street);
        return WaitUtil.isVisible(By.xpath(xpath), 5);
    }

    public boolean isDefaultBadgeDisplayedForFirstAddress() {
        return WaitUtil.isVisible(By.xpath("(//div[contains(@class,'si-addr-item')])[1]//span[contains(@class,'si-default-badge')]"), 5);
    }

    public int getAddressCount() {
        List<WebElement> items = Constant.WEBDRIVER.findElements(_addrItems);
        return items.size();
    }

    public String getEmptyMessage() {
        return WaitUtil.getTextOrEmpty(_emptyMsg);
    }

    public String getListTitle() { return WaitUtil.getTextOrEmpty(_lblListTitle); }
    public String getAddTitle() { return WaitUtil.getTextOrEmpty(_lblAddTitle); }
    public String getAddNamePlaceholder() { return Constant.WEBDRIVER.findElement(_txtNameAdd).getAttribute("placeholder"); }
    public String getAddPhonePlaceholder() { return Constant.WEBDRIVER.findElement(_txtPhoneAdd).getAttribute("placeholder"); }
    public String getAddStreetPlaceholder() { return Constant.WEBDRIVER.findElement(_txtStreetAdd).getAttribute("placeholder"); }
    public String getAddNameLabel() { return WaitUtil.getTextOrEmpty(_lblAddName); }
    public String getAddPhoneLabel() { return WaitUtil.getTextOrEmpty(_lblAddPhone); }
    public String getAddStreetLabel() { return WaitUtil.getTextOrEmpty(_lblAddStreet); }
    public String getEditTitle() { return WaitUtil.getTextOrEmpty(By.xpath("//div[@id='siModalEdit']//h3")); }
    public String getEditNameValue() { return Constant.WEBDRIVER.findElement(_txtNameEdit).getAttribute("value"); }
    public String getEditPhoneValue() { return Constant.WEBDRIVER.findElement(_txtPhoneEdit).getAttribute("value"); }
    public String getEditStreetValue() { return Constant.WEBDRIVER.findElement(_txtStreetEdit).getAttribute("value"); }
    public String getDeleteConfirmMessage() { return WaitUtil.getTextOrEmpty(By.xpath("//div[@id='deleteAddressConfirmModal']//p")); }

    public void closeListPopup() { WaitUtil.click(_btnCloseList); }

    public boolean isListModalDisplayed() { return WaitUtil.isVisible(_modalList, 2); }
    public boolean isAddModalDisplayed() { return WaitUtil.isVisible(_modalAdd, 2); }
    public boolean isEditModalDisplayed() { return WaitUtil.isVisible(_modalEdit, 2); }
    public boolean isDeleteModalDisplayed() { return WaitUtil.isVisible(_modalDelete, 2); }

    public void selectFirstAddress() {
        WaitUtil.click(By.xpath("(//input[@type='radio'])[1]"));
    }

    public String getFirstAddressName() {
        return WaitUtil.getTextOrEmpty(By.xpath("(//div[@class='ai-name'])[1]"));
    }

    public String getToastMessage() {
        LoggerUtil.info("Lấy thông báo từ Toast");
        try {
            String[] captured = {""};
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                    .until(driver -> {
                        Object text = ((org.openqa.selenium.JavascriptExecutor) driver)
                                .executeScript(
                                        "var el = document.getElementById('toastNotification');" +
                                                "if (!el) return null;" +
                                                "var t = el.textContent.trim();" +
                                                "return t.length > 0 ? t : null;"
                                );
                        if (text != null && !text.toString().isEmpty()) {
                            captured[0] = text.toString();
                            return true;
                        }
                        return false;
                    });
            return captured[0];
        } catch (Exception e) {
            LoggerUtil.warn("Toast không bắt được: " + e.getMessage());
            return "";
        }
    }
}
