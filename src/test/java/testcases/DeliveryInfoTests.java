package testcases;

import common.Constant;
import common.LoggerUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.DeliveryInfoPage;
import pageobjects.LoginPage;

import static common.Constant.WEBDRIVER;

public class DeliveryInfoTests {

    DeliveryInfoPage deliveryPage = new DeliveryInfoPage();
    LoginPage loginPage = new LoginPage();

    @BeforeMethod
    public void beforeMethod() {
        LoggerUtil.info("Thiết lập WebDriver và đăng nhập");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WEBDRIVER = new ChromeDriver(options);
        WEBDRIVER.manage().window().maximize();
        WEBDRIVER.get(Constant.URL);

        loginPage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }
    }

    @AfterMethod
    public void afterMethod() {
        LoggerUtil.info("Quitting WebDriver");
        if (WEBDRIVER != null) {
            WEBDRIVER.quit();
        }
    }

    @Test(description = "TC12_F0001 - Mở form thêm địa chỉ mới")
    public void TC12_F0001() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        Assert.assertTrue(deliveryPage.isAddModalDisplayed(), "Form thêm địa chỉ mới không hiển thị");
    }

    @Test(description = "TC12_F0002 - Thêm mới thông tin giao hàng thành công")
    public void TC12_F0002() {
        String name = "Phan Phương Thảo";
        String phone = "0901234567";
        String street = "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam";
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm(name, phone, street, false);
        deliveryPage.submitAddForm();
        Assert.assertTrue(deliveryPage.isAddressDisplayed(name, phone, street),
                "Địa chỉ mới không hiển thị trong danh sách");
    }

    @Test(description = "TC12_F0003 - Không nhập họ tên khi thêm mới")
    public void TC12_F0003() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Vui lòng không để trống thông tin");
    }

    @Test(description = "TC12_F0004 - Không nhập số điện thoại khi thêm mới")
    public void TC12_F0004() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Phương Thảo", "", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Vui lòng không để trống thông tin");
    }

    @Test(description = "TC12_F0005 - Không nhập địa chỉ khi thêm mới")
    public void TC12_F0005() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Phương Thảo", "0901234567", "", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Vui lòng không để trống thông tin");
    }

    @Test(description = "TC12_F0006 - Số điện thoại không hợp lệ khi thêm mới")
    public void TC12_F0006() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Phương Thảo", "91234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Số điện thoại không hợp lệ!");
    }

    @Test(description = "TC12_F0007 - Thông tin giao hàng bị trùng khi thêm mới")
    public void TC12_F0007() {
        deliveryPage.openShippingInfo();
        String name = "Trùng Lặp";
        String phone = "0901234567";
        String street = "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam";
        if (!deliveryPage.isAddressDisplayed(name, phone, street)) {
            deliveryPage.clickAddNewAddress();
            deliveryPage.fillAddForm(name, phone, street, false);
            deliveryPage.submitAddForm();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm(name, phone, street, false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Thông tin giao hàng này đã tồn tại");
    }

    @Test(description = "TC12_F0008 - Hủy thêm mới")
    public void TC12_F0008() {
        deliveryPage.openShippingInfo();
        int count = deliveryPage.getAddressCount();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Hủy Bỏ", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng", false);
        deliveryPage.cancelAddForm();
        Assert.assertFalse(deliveryPage.isAddModalDisplayed(), "Modal Add vẫn hiển thị sau khi hủy");
        Assert.assertEquals(deliveryPage.getAddressCount(), count, "Số lượng địa chỉ thay đổi sau khi hủy");
    }

    @Test(description = "TC12_F0009 - Đặt địa chỉ mới làm mặc định")
    public void TC12_F0009() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Mặc Định", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", true);
        deliveryPage.submitAddForm();
        Assert.assertTrue(deliveryPage.isDefaultBadgeDisplayedForFirstAddress(),
                "Badge 'Mặc định' không hiển thị ở vị trí đầu tiên");
    }

    @Test(description = "TC12_F0010 - Đóng form thêm bằng nút X")
    public void TC12_F0010() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();

        // Bổ sung bước 2 theo Manual: Nhập dữ liệu bất kỳ (mô phỏng người dùng đang gõ dở)
        deliveryPage.fillAddForm("Đóng X", "0911112222", "123 Đường Test, Đà Nẵng, Việt Nam", false);

        // Bước 3: Click nút X
        deliveryPage.closeAddPopup();
        Assert.assertFalse(deliveryPage.isAddModalDisplayed(), "Modal Add vẫn hiển thị sau khi click nút X");
    }

    @Test(description = "TC12_F0011 - Hiển thị đúng thông tin sau khi thêm")
    public void TC12_F0011() {
        String name = "Kiểm Tra Hiển Thị";
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Phương Thảo", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertTrue(
                deliveryPage.isAddressDisplayed("Phương Thảo", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam"));
    }

    @Test(description = "TC12_F0012 - Địa chỉ quá ngắn (dưới 20 ký tự)")
    public void TC12_F0012() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Thảo", "0901234567", "Số 1 Lê Lợi", false);
        deliveryPage.submitAddForm();
        Assert.assertTrue(deliveryPage.getToastMessage().contains("tối thiểu 20 ký tự"));
    }

    @Test(description = "TC12_F0013 - Họ tên chứa ký tự đặc biệt")
    public void TC12_F0013() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Thảo @@@", "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Họ tên không được chứa số hoặc ký tự đặc biệt!");
    }

    @Test(description = "TC12_F0014 - Số điện thoại không đúng định dạng (11 số)")
    public void TC12_F0014() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Thảo", "01234567890", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Số điện thoại không hợp lệ!");
    }

    @Test(description = "TC12_F0015 - SĐT chứa chữ cái/ký tự đặc biệt")
    public void TC12_F0015() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Thảo", "0901abc567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Số điện thoại không hợp lệ!");
    }

    @Test(description = "TC12_F0016 - Mở form cập nhật thông tin giao hàng")
    public void TC12_F0016() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        deliveryPage.editFirstAddress();
        Assert.assertTrue(deliveryPage.isEditModalDisplayed());
    }

    @Test(description = "TC12_F0017 - Cập nhật thông tin giao hàng thành công")
    public void TC12_F0017() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        String name = "Phan Thảo Cập Nhật";
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm(name, "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitEditForm();
        Assert.assertTrue(
                deliveryPage.isAddressDisplayed(name, "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam"));
    }

    @Test(description = "TC12_F0018 - Không nhập thông tin khi cập nhật")
    public void TC12_F0018() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm("", "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitEditForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Vui lòng không để trống thông tin");
    }

    @Test(description = "TC12_F0019 - Số điện thoại không hợp lệ khi cập nhật")
    public void TC12_F0019() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm("Phan Thảo", "12345", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitEditForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Số điện thoại không hợp lệ!");
    }

    @Test(description = "TC12_F0020 - Trùng lặp thông tin với địa chỉ khác khi cập nhật")
    public void TC12_F0020() {
        deliveryPage.openShippingInfo();
        while (deliveryPage.getAddressCount() < 2)
            createDummyAddress();
        String name = "Trùng Lặp";
        String phone = "0901234567";
        String street = "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam";
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm(name, phone, street, false);
        deliveryPage.submitEditForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Thông tin giao hàng này đã tồn tại");
    }

    @Test(description = "TC12_F0021 - Hủy cập nhật thông tin")
    public void TC12_F0021() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();

        // 1. Mở form cập nhật thông tin giao hàng
        deliveryPage.editFirstAddress();

        // 2. Chỉnh sửa dữ liệu bất kỳ (BƯỚC MỚI BỔ SUNG THEO TEST DATA)
        deliveryPage.fillEditForm("Phan Thảo", "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng", false);

        // 3. Click vào nút “Hủy”
        deliveryPage.cancelEditForm();

        // Xác nhận form đã được đóng
        Assert.assertFalse(deliveryPage.isEditModalDisplayed(), "Modal Edit vẫn hiển thị sau khi bấm Hủy");
    }

    @Test(description = "TC12_F0022 - Cập nhật thất bại (Tên không hợp lệ)")
    public void TC12_F0022() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm("Thảo 123", "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitEditForm();
        Assert.assertEquals(deliveryPage.getToastMessage(), "Họ tên không được chứa số hoặc ký tự đặc biệt!");
    }

    @Test(description = "TC12_F0023 - Cập nhật địa chỉ thành mặc định")
    public void TC12_F0023() {
        deliveryPage.openShippingInfo();
        while (deliveryPage.getAddressCount() < 2)
            createDummyAddress();
        deliveryPage.editFirstAddress();
        deliveryPage.fillEditForm("Mặc Định", "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", true);
        deliveryPage.submitEditForm();
        Assert.assertTrue(deliveryPage.isDefaultBadgeDisplayedForFirstAddress());
    }

    @Test(description = "TC12_F0024 - Đóng form cập nhật bằng nút X")
    public void TC12_F0024() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();

        // 1. Mở form cập nhật thông tin giao hàng
        deliveryPage.editFirstAddress();

        // 2. Chỉnh sửa dữ liệu bất kỳ (BƯỚC MỚI BỔ SUNG)
        deliveryPage.fillEditForm("Sửa dở rồi đóng X", "0988888888", "Đang gõ địa chỉ thì bấm X", false);

        // 3. Click vào nút “X”
        deliveryPage.closeEditPopup();

        // Xác nhận form đã được đóng
        Assert.assertFalse(deliveryPage.isEditModalDisplayed(), "Modal Edit vẫn hiển thị sau khi bấm X");
    }

    @Test(description = "TC12_F0025 - Kiểm tra dữ liệu cũ vẫn hiển thị trên form cập nhật")
    public void TC12_F0025() {
        // --- TIỀN ĐIỀU KIỆN ---
        // Đảm bảo đang ở màn hình đặt hàng và có ít nhất 1 địa chỉ (ưu tiên địa chỉ mặc định) để hiển thị ra ngoài màn hình
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0) {
            deliveryPage.clickAddNewAddress();
            deliveryPage.fillAddForm("Tên Ban Đầu", "0912345678", "15 Trần Phú, Hải Châu, Đà Nẵng", true);
            deliveryPage.submitAddForm();
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        }

        // --- THỰC THI CÁC BƯỚC TEST ---

        // Bước 1: Mở form cập nhật thông tin giao hàng (Click icon Edit ở địa chỉ đầu tiên)
        deliveryPage.editFirstAddress();

        // Bước 2: Chỉnh sửa thông tin hợp lệ
        String updatedName = "Người Nhận Cập Nhật";
        String updatedPhone = "0988888888";
        String updatedAddress = "456 Đường Mới, Thanh Khê, Đà Nẵng";

        // Ghi chú: Tick 'true' để set làm mặc định, đảm bảo địa chỉ này chắc chắn được chọn ra màn hình ngoài
        deliveryPage.fillEditForm(updatedName, updatedPhone, updatedAddress, true);

        // Bước 3: Click vào nút “Lưu thay đổi”
        deliveryPage.submitEditForm();
        try {
            Thread.sleep(1000); // Chờ API lưu dữ liệu
        } catch (InterruptedException ignored) {}

        // Bước 4: Quay lại màn hình đặt hàng
        // Thông thường sau khi Lưu, hệ thống sẽ quay lại popup danh sách. Ta cần đóng popup này lại.
        if (deliveryPage.isListModalDisplayed()) {
            deliveryPage.closeListPopup();
            try {
                Thread.sleep(500); // Chờ popup đóng hoàn toàn, UI màn hình đặt hàng load lại
            } catch (InterruptedException ignored) {}
        }

        // Bước 5: Quan sát khối Thông tin giao hàng
        // Xác nhận dữ liệu vừa cập nhật đã hiển thị đúng trên trang đặt hàng (Order Screen)
        String pageSource = WEBDRIVER.getPageSource();

        Assert.assertTrue(pageSource.contains(updatedName), "Tên mới chưa được cập nhật ra màn hình đặt hàng!");
        Assert.assertTrue(pageSource.contains(updatedPhone), "Số điện thoại mới chưa được cập nhật ra màn hình đặt hàng!");
        Assert.assertTrue(pageSource.contains(updatedAddress), "Địa chỉ mới chưa được cập nhật ra màn hình đặt hàng!");
    }


    // =========================================================================
    // CHECK FUNC - XÓA (TC12_F0028 -> TC12_F0035)
    // =========================================================================

    @Test(description = "TC12_F0026 - Xóa thông tin giao hàng thành công")
    public void TC12_F0026() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        int count = deliveryPage.getAddressCount();
        deliveryPage.deleteFirstAddress();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Assert.assertEquals(deliveryPage.getAddressCount(), count - 1);
    }

    @Test(description = "TC12_F0027 - Hủy xóa thông tin giao hàng")
    public void TC12_F0027() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        int count = deliveryPage.getAddressCount();
        deliveryPage.cancelDeleteFirstAddress();
        Assert.assertEquals(deliveryPage.getAddressCount(), count);
    }

    @Test(description = "TC12_F0028 - Xóa địa chỉ đang được đặt làm mặc định")
    public void TC12_F0028() {
        deliveryPage.openShippingInfo();

        
        // Tạo 1 địa chỉ thường
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Địa chỉ A ", "0900000000", "123 Đường Phụ, Quận Liên Chiểu, Đà Nẵng", false);
        deliveryPage.submitAddForm();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Tạo 1 địa chỉ mặc định
        deliveryPage.clickAddNewAddress();
        deliveryPage.fillAddForm("Địa chỉ B ", "0901234567", "15 Trần Phú, Hải Châu, Đà Nẵng, Việt Nam", true);
        deliveryPage.submitAddForm();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        int initialCount = deliveryPage.getAddressCount();

        // Tiến hành xóa dòng đầu tiên (đang là mặc định)
        deliveryPage.deleteFirstAddress();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Xác nhận 1: Số lượng giảm đi 1
        Assert.assertEquals(deliveryPage.getAddressCount(), initialCount - 1);
        // Xác nhận 2: Nhãn mặc định đã được gán cho địa chỉ còn lại
        Assert.assertTrue(deliveryPage.isDefaultBadgeDisplayedForFirstAddress(), "Trạng thái mặc định chưa được cập nhật cho địa chỉ khác");
    }

    @Test(description = "TC12_F0029 - Xóa thông tin khi chỉ có duy nhất 1 địa chỉ")
    public void TC12_F0029() {
        deliveryPage.openShippingInfo();
        while (deliveryPage.getAddressCount() > 1) {
            deliveryPage.deleteFirstAddress();
            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {
            }
        }
        deliveryPage.deleteFirstAddress();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Assert.assertEquals(deliveryPage.getAddressCount(), 0);
    }

    @Test(description = "TC12_F0030 - Trạng thái danh sách trống sau khi xóa hết")
    public void TC12_F0030() {
        deliveryPage.openShippingInfo();
        while (deliveryPage.getAddressCount() > 0) {
            deliveryPage.deleteFirstAddress();
            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {
            }
        }
        Assert.assertEquals(deliveryPage.getEmptyMessage(), "Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ mới.");
    }

    @Test(description = "TC12_F0031 - Nút thêm mới vẫn hoạt động sau khi xóa hết địa chỉ")
    public void TC12_F0031() {
        deliveryPage.openShippingInfo();
        while (deliveryPage.getAddressCount() > 0) {
            deliveryPage.deleteFirstAddress();
            try {
                Thread.sleep(800);
            } catch (InterruptedException ignored) {
            }
        }
        deliveryPage.clickAddNewAddress();
        Assert.assertTrue(deliveryPage.isAddModalDisplayed());
    }

    @Test(description = "TC12_F0032 - Địa chỉ đã xóa không còn hiển thị trong danh sách")
    public void TC12_F0032() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0)
            createDummyAddress();
        String name = deliveryPage.getFirstAddressName();
        deliveryPage.deleteFirstAddress();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        Assert.assertFalse(WEBDRIVER.getPageSource().contains(name));
    }
    // =========================================================================
    // CHECK GUI (TC12_UI001, TC12_UI005, TC12_UI009, TC12_UI013)
    // =========================================================================

    @Test(description = "TC12_UI001 - Kiểm tra hiển thị popup danh sách địa chỉ")
    public void TC12_UI001() {
        deliveryPage.openShippingInfo();
        // 1. Tiêu đề
        Assert.assertEquals(deliveryPage.getListTitle(), "Thông tin giao hàng");
        // 2. Danh sách (nếu có)
        if (deliveryPage.getAddressCount() == 0) createDummyAddress();
        Assert.assertTrue(deliveryPage.getAddressCount() > 0, "Danh sách địa chỉ không hiển thị");
        // 3. Các thành phần GUI cơ bản
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//button[@onclick='siOpenAdd()']")).isDisplayed(), "Nút thêm mới không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='siModalList']//button[@class='si-close']")).isDisplayed(), "Nút đóng X không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//button[contains(@class,'si-btn-edit')]")).isDisplayed(), "Icon sửa không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//button[contains(@class,'si-btn-del')]")).isDisplayed(), "Icon xóa không hiển thị");
    }

    @Test(description = "TC12_UI005 - Kiểm tra hiển thị popup thêm địa chỉ")
    public void TC12_UI005() {
        deliveryPage.openShippingInfo();
        deliveryPage.clickAddNewAddress();

        // Tiêu đề
        Assert.assertEquals(deliveryPage.getAddTitle(), "Thêm địa chỉ mới");

        // Labels & Placeholders
        Assert.assertTrue(deliveryPage.getAddNameLabel().contains("Họ và tên *"));
        Assert.assertEquals(deliveryPage.getAddNamePlaceholder(), "Ví dụ: Nguyễn Văn An");

        Assert.assertTrue(deliveryPage.getAddPhoneLabel().contains("Số điện thoại *"));
        Assert.assertEquals(deliveryPage.getAddPhonePlaceholder(), "Ví dụ: 0901234567");

        Assert.assertTrue(deliveryPage.getAddStreetLabel().contains("Địa chỉ giao hàng *"));
        Assert.assertEquals(deliveryPage.getAddStreetPlaceholder(), "Nhập đầy đủ địa chỉ (thôn/xóm, xã/phường, huyện/quận, tỉnh/thành phố)");

        // Checkbox & Buttons
        Assert.assertTrue(WEBDRIVER.findElement(By.id("siAddDefault")).isDisplayed(), "Checkbox mặc định không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='siModalAdd']//button[contains(@class,'si-btn-cancel')]")).isDisplayed(), "Nút Hủy không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='siModalAdd']//button[contains(@class,'si-btn-confirm')]")).isDisplayed(), "Nút Thêm địa chỉ không hiển thị");
    }

    @Test(description = "TC12_UI009 - Kiểm tra hiển thị popup chỉnh sửa")
    public void TC12_UI009() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0) createDummyAddress();

        deliveryPage.editFirstAddress();

        // Tiêu đề
        Assert.assertEquals(deliveryPage.getEditTitle(), "Chỉnh sửa thông tin");

        // Dữ liệu sẵn có
        Assert.assertFalse(deliveryPage.getEditNameValue().isEmpty(), "Trường Họ tên không có dữ liệu sẵn");
        Assert.assertFalse(deliveryPage.getEditPhoneValue().isEmpty(), "Trường SĐT không có dữ liệu sẵn");
        Assert.assertFalse(deliveryPage.getEditStreetValue().isEmpty(), "Trường Địa chỉ không có dữ liệu sẵn");

        // Buttons
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='siModalEdit']//button[contains(@class,'si-btn-cancel')]")).isDisplayed(), "Nút Hủy không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='siModalEdit']//button[contains(@class,'si-btn-confirm')]")).isDisplayed(), "Nút Lưu thay đổi không hiển thị");
    }

    @Test(description = "TC12_UI013 - Kiểm tra hiển thị popup xác nhận xóa")
    public void TC12_UI013() {
        deliveryPage.openShippingInfo();
        if (deliveryPage.getAddressCount() == 0) createDummyAddress();

        WEBDRIVER.findElement(By.xpath("(//button[@class='si-btn-del'])[1]")).click();

        // Nội dung
        Assert.assertEquals(deliveryPage.getDeleteConfirmMessage(), "Bạn có chắc chắn muốn xóa địa chỉ này?");

        // Nút
        Assert.assertTrue(WEBDRIVER.findElement(By.id("btnConfirmDeleteAddress")).isDisplayed(), "Nút XÓA không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElement(By.xpath("//div[@id='deleteAddressConfirmModal']//button[@class='btn-no']")).isDisplayed(), "Nút HỦY không hiển thị");
    }

    // Helper
    private void createDummyAddress() {
        deliveryPage.clickAddNewAddress();
        String uniqueName = getUniqueName("Người Nhận Tin ");
        deliveryPage.fillAddForm(uniqueName, "0901234567", "12 Lê Lợi, Hải Châu, Đà Nẵng, Việt Nam", false);
        deliveryPage.submitAddForm();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    private String getUniqueName(String prefix) {
        String ts = String.valueOf(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder(prefix);
        for (char c : ts.toCharArray()) {
            sb.append((char) ('a' + (c - '0')));
        }
        return sb.toString();
    }
}
