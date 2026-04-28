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
import pageobjects.HomePage;
import pageobjects.LoginPage;
import pageobjects.OrderDetailPage;
import pageobjects.OrderHistoryPage;

import static common.Constant.WEBDRIVER;

public class OrderHistoryTests {

    private final LoginPage loginPage = new LoginPage();
    private final HomePage homePage = new HomePage();
    private final OrderHistoryPage orderHistoryPage = new OrderHistoryPage();
    private final OrderDetailPage orderDetailPage = new OrderDetailPage();

    @BeforeMethod
    public void beforeMethod() {
        LoggerUtil.info("Thiết lập WebDriver và mở trang chính");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WEBDRIVER = new ChromeDriver(options);
        WEBDRIVER.manage().window().maximize();

        // Retry getting the URL up to 10 times with longer wait
        int retries = 10;
        for (int i = 0; i < retries; i++) {
            try {
                WEBDRIVER.get(Constant.URL);
                break;
            } catch (Exception e) {
                LoggerUtil.warn("Failed to load page, retrying... " + (i + 1) + "/" + retries);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
                if (i == retries - 1)
                    throw e;
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        loginPage.openAccountMenu();
        loginPage.login(Constant.USERNAME, Constant.PASSWORD);
        try {
            Thread.sleep(3000);
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

    @Test(description = "TC13-F0001 - Hiển thị danh sách đơn hàng")
    public void TC13_F0001_viewOrderHistory() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isPageDisplayed(), "Trang lịch sử đơn hàng chưa hiển thị");
        Assert.assertTrue(orderHistoryPage.getOrderCount() > 0, "Không có đơn hàng nào hiển thị");
        Assert.assertTrue(orderHistoryPage.getFirstOrderCode().startsWith("#HD"), "Mã đơn hàng không đúng định dạng");
    }

    @Test(description = "TC13-F0002 - Chưa có đơn hàng")
    public void TC13_F0002_noOrders() {
        WEBDRIVER.manage().deleteAllCookies();
        WEBDRIVER.navigate().refresh();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }
        loginPage.openAccountMenu();
        loginPage.login(Constant.NO_ORDER_USERNAME, Constant.NO_ORDER_PASSWORD);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }

        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isNoOrdersMessageDisplayed(), "Thông báo chưa có đơn hàng không hiển thị");
    }

    @Test(description = "TC13-F0003 - Nhiều đơn hàng sắp xếp")
    public void TC13_F0003_multipleOrdersSorting() {
        orderHistoryPage.openOrderHistoryPage();
        if (orderHistoryPage.getOrderCount() >= 2) {
            String first = orderHistoryPage.getFirstOrderDate();
            String second = orderHistoryPage.getSecondOrderDate();
            Assert.assertTrue(orderHistoryPage.isSortedByDateDescending(first, second),
                    "Đơn hàng không được sắp xếp đúng");
        }
    }

    @Test(description = "TC13-F0004 - Thông tin tổng quan đơn hàng")
    public void TC13_F0004_verifyOrderInformation() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.getOrderCount() > 0, "Không có đơn hàng");
        Assert.assertFalse(orderHistoryPage.getFirstOrderStatus().isEmpty(), "Trạng thái đơn hàng không được bỏ trống");
        Assert.assertTrue(orderHistoryPage.getFirstOrderTotal().matches("\\d+(?:\\.\\d{3})* ?đ"),
                "Tổng tiền không đúng định dạng");
    }

    @Test(description = "TC13-F0005 - Nút Xem thêm khi có nhiều sản phẩm")
    public void TC13_F0005_viewMoreCollapse() {
        orderHistoryPage.openOrderHistoryPage();
        if (orderHistoryPage.hasExpandButtonForFirstOrder()) {
            orderHistoryPage.clickViewMoreForFirstOrder();
            Assert.assertTrue(orderHistoryPage.isExpandedForFirstOrder(), "Chế độ xem mở rộng chưa hiển thị");
            orderHistoryPage.clickCollapseForFirstOrder();
            Assert.assertTrue(orderHistoryPage.isCollapsedForFirstOrder(), "Chế độ xem thu gọn chưa hiển thị");
        }
    }

    @Test(description = "TC13-F0006 - Breadcrumb điều hướng")
    public void TC13_F0006_breadcrumbHome() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.clickBreadcrumbHome();
        Assert.assertTrue(homePage.isHomePageDisplayed(), "Không chuyển về trang chủ");
    }


    @Test(description = "TC13-F0008 - Mở chi tiết đơn hàng")
    public void TC13_F0008_openOrderDetailFromHistory() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.getOrderCount() > 0, "Không có đơn hàng");
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isPageDisplayed(), "Trang chi tiết chưa hiển thị");
    }

    @Test(description = "TC13-UI0001 - Tiêu đề trang")
    public void TC13_UI0001_pageTitle() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertEquals(orderHistoryPage.getPageTitle(), "LỊCH SỬ ĐƠN HÀNG");
    }

    @Test(description = "TC13-UI0002 - Hình ảnh sản phẩm đại diện")
    public void TC13_UI0002_representativeProductImage() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isRepresentativeImageDisplayed(), "Ảnh sản phẩm đại diện không hiển thị");
    }

    @Test(description = "TC13-UI0003 - Hiển thị trạng thái đơn hàng")
    public void TC13_UI0003_orderStatus() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isOrderStatusDisplayed(), "Trạng thái đơn hàng không hiển thị");
    }

    @Test(description = "TC13-UI0004 - Hiển thị tổng tiền")
    public void TC13_UI0003_totalAmount() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isTotalDisplayed(), "Tổng tiền đơn hàng không hiển thị");
    }

    @Test(description = "TC13-UI0005 - Hình ảnh sản phẩm")
    public void TC13_UI0005_orderItemImageDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isRepresentativeImageDisplayed(), "Ảnh sản phẩm không hiển thị");
    }

    @Test(description = "TC13-UI0006 - Thông tin sản phẩm")
    public void TC13_UI0006_orderItemInfoDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        Assert.assertTrue(orderHistoryPage.isProductInfoDisplayed(),
                "Thông tin chi tiết sản phẩm không hiển thị đầy đủ");
    }

    @Test(description = "TC13-F0007 - Không hiển thị Xem thêm khi đơn có 1 sản phẩm")
    public void TC13_F0007_noViewMoreForSingleProduct() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();

        int productCount = orderDetailPage.getProductCount();
        
        // Cần quay lại trang Lịch sử đơn hàng để kiểm tra nút "Xem thêm"
        orderHistoryPage.openOrderHistoryPage();

        if (productCount == 1) {
            Assert.assertFalse(orderHistoryPage.hasExpandButtonForFirstOrder(),
                    "Nút Xem thêm hiển thị sai khi chỉ có 1 sản phẩm");
        }
    }

    @Test(description = "TC13-F0009 - Mở chi tiết từ lịch sử")
    public void TC13_F0009_openDetailFromHistory() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isPageDisplayed(), "Mở trang chi tiết không thành công");
    }

    @Test(description = "TC13-F0010 - Hiển thị nhiều sản phẩm")
    public void TC13_F0010_displayMultipleProducts() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.getProductCount() > 0, "Không có sản phẩm nào");
    }

    @Test(description = "TC13-F0011 - Không thể sửa thông tin giao hàng")
    public void TC13_F0011_shippingInfoCannotBeEdited() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isShippingInfoDisplayed(), "Thông tin giao hàng không hiển thị");
        Assert.assertTrue(WEBDRIVER.findElements(By.cssSelector(".shipping-info-content input")).isEmpty(),
                "Có trường input trong thông tin giao hàng");
    }

    @Test(description = "TC13-F0012 - Nút liên hệ với người bán")
    public void TC13_F0012_contactSellerAction() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isContactButtonDisplayed(), "Không có nút liên hệ người bán");
    }

    @Test(description = "TC13-F0013 - Phương thức thanh toán COD")
    public void TC13_F0013_codPaymentMethod() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertEquals(orderDetailPage.getPaymentMethod(), "Thanh toán khi giao hàng (COD)");
    }

    @Test(description = "TC13-F0014 - Danh sách sản phẩm đã đặt")
    public void TC13_F0014_verifyProductList() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.areProductDetailsDisplayed(), "Chi tiết sản phẩm không đầy đủ");
    }

    @Test(description = "TC13-F0015 - Tổng tiền hàng và tổng thanh toán")
    public void TC13_F0015_verifyTotalAmountCalculation() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getTotalAmountText().isEmpty(), "Tổng thanh toán trống");
        Assert.assertFalse(orderDetailPage.getSubtotalAmountText().isEmpty(), "Tổng tiền hàng trống");
    }

    @Test(description = "TC13-UI0008 - Hiển thị ghi chú")
    public void TC13_UI0008_displayNote() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.getNote().contains("Ghi chú"), "Ghi chú không hiển thị");
    }

    @Test(description = "TC13-UI0009 - Danh sách sản phẩm đã đặt")
    public void TC13_UI0009_displayProductList() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.getProductCount() > 0, "Danh sách sản phẩm không hiển thị");
    }

    @Test(description = "TC13-UI0010 - Ngày đặt hàng")
    public void TC13_UI0010_displayOrderDate() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getOrderDate().isEmpty(), "Ngày đặt hàng trống");
    }

    @Test(description = "TC13-UI0011 - Thông tin tài khoản")
    public void TC13_UI0011_displayAccountInfo() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getAccountName().isEmpty(), "Thiếu tên tài khoản");
        Assert.assertFalse(orderDetailPage.getAccountEmail().isEmpty(), "Thiếu email tài khoản");
    }

    @Test(description = "TC13-UI0012 - Tên khách hàng")
    public void TC13_UI0012_displayCustomerName() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getAccountName().isEmpty(), "Tên khách hàng trống");
    }

    @Test(description = "TC13-UI0013 - Email khách hàng")
    public void TC13_UI0013_displayCustomerEmail() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getAccountEmail().isEmpty(), "Email khách hàng trống");
    }

    @Test(description = "TC13-UI0014 - Thông tin giao hàng")
    public void TC13_UI0014_displayShippingInfo() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isShippingInfoDisplayed(), "Thông tin giao hàng trống");
    }

    @Test(description = "TC13-UI0015 - Người nhận")
    public void TC13_UI0015_displayRecipient() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getRecipientName().isEmpty(), "Người nhận trống");
    }

    @Test(description = "TC13-UI0016 - Số điện thoại")
    public void TC13_UI0016_displayPhoneNumber() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getPhoneNumber().isEmpty(), "Số điện thoại trống");
    }

    @Test(description = "TC13-UI0017 - Địa chỉ giao hàng")
    public void TC13_UI0017_displayAddress() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getAddress().isEmpty(), "Địa chỉ trống");
    }

    @Test(description = "TC13-UI0018 - Phương thức thanh toán")
    public void TC13_UI0018_displayPaymentMethod() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getPaymentMethod().isEmpty(), "Phương thức thanh toán trống");
    }

    @Test(description = "TC13-UI0019 - Giá sản phẩm")
    public void TC13_UI0019_displayProductPrice() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.areProductDetailsDisplayed(), "Giá sản phẩm không hiển thị");
    }

    @Test(description = "TC13-UI0020 - Số lượng sản phẩm")
    public void TC13_UI0020_displayProductQuantity() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.areProductDetailsDisplayed(), "Số lượng sản phẩm không hiển thị");
    }

    @Test(description = "TC13-UI0021 - Tổng tiền hàng")
    public void TC13_UI0021_displaySubtotal() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getSubtotalAmountText().isEmpty(), "Tổng tiền hàng không hiển thị");
    }

    @Test(description = "TC13-UI0022 - Mã đơn hàng")
    public void TC13_UI0022_displayOrderCode() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.getOrderCode().startsWith("HD"), "Mã đơn hàng không hợp lệ");
    }

    @Test(description = "TC13-UI0023 - Trạng thái đơn hàng")
    public void TC13_UI0023_displayOrderStatus() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertFalse(orderDetailPage.getOrderStatus().isEmpty(), "Trạng thái trống");
    }

    @Test(description = "TC13-UI0024 - Hình ảnh sản phẩm")
    public void TC13_UI0024_displayProductImage() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.areProductDetailsDisplayed(), "Ảnh sản phẩm trống");
    }

    @Test(description = "TC13-UI0025 - Tên sản phẩm")
    public void TC13_UI0025_displayProductName() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.areProductDetailsDisplayed(), "Tên sản phẩm trống");
    }

    @Test(description = "TC13-UI0026 - Nút Hủy đơn hàng")
    public void TC13_UI0026_displayCancelButton() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isCancelButtonDisplayed(), "Nút hủy đơn hàng không hiển thị");
    }

    @Test(description = "TC13-UI0027 - Nút liên hệ người bán")
    public void TC13_UI0027_displayContactSellerButton() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        Assert.assertTrue(orderDetailPage.isContactButtonDisplayed(), "Nút liên hệ người bán không hiển thị");
    }

    @Test(description = "TC13-F0016 - Không cho hủy đơn hàng đã hủy")
    public void TC13_F0016_cannotCancelCancelledOrder() {
        orderHistoryPage.openOrderHistoryPage();

        if (!orderHistoryPage.hasCancelledOrder()) {
            LoggerUtil.info("Không tìm thấy đơn hàng đã hủy, đang chuẩn bị dữ liệu...");
            orderHistoryPage.openFirstOrderDetail();
            if (orderDetailPage.isCancelButtonEnabled()) {
                orderDetailPage.cancelOrder("Hủy để test TC13-F0016");
                orderHistoryPage.openOrderHistoryPage();
            } else {
                Assert.fail("Không có đơn hàng nào có thể hủy để thực hiện test case này");
            }
        }

        orderHistoryPage.openCancelledOrderDetail();
        Assert.assertEquals(orderDetailPage.getOrderStatus(), "Đã hủy");
        Assert.assertFalse(orderDetailPage.isCancelButtonEnabled(),
                "Đơn đã hủy nhưng vẫn cho phép hủy lại");
    }

    @Test(description = "TC13-F0017 - Hủy đơn hàng thành công")
    public void TC13_F0017_cancelOrderSuccess() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.cancelOrder("Đổi ý không mua nữa");
            Assert.assertEquals(orderDetailPage.getOrderStatus(), "Đã hủy", "Hủy đơn hàng thất bại");
        }
    }

    @Test(description = "TC13-F0018 - Hủy đơn khi trống lý do")
    public void TC13_F0018_cancelOrderEmptyReason() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.cancelOrder("");
            Assert.assertEquals(orderDetailPage.getOrderStatus(), "Đã hủy", "Hủy đơn hàng với lý do trống thất bại");
        }
    }

    @Test(description = "TC13-F0019 - Nhấn Hủy trên popup (Không hủy đơn)")
    public void TC13_F0019_dismissCancelModal() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            String initialStatus = orderDetailPage.getOrderStatus();
            orderDetailPage.openCancelModal();
            orderDetailPage.dismissCancelModal();
            Assert.assertEquals(orderDetailPage.getOrderStatus(), initialStatus,
                    "Đơn hàng bị hủy mặc dù đã nhấn Hủy trên popup");
        }
    }

    @Test(description = "TC13-F0020 - Không cho hủy đơn đang giao")
    public void TC13_F0020_cannotCancelDeliveringOrder() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.getOrderStatus().equals("Đang giao")) {
            Assert.assertFalse(orderDetailPage.isCancelButtonEnabled(), "Vẫn có thể hủy đơn đang giao");
        }
    }

    @Test(description = "TC13-F0021 - Không cho hủy đơn đã hoàn thành")
    public void TC13_F0021_cannotCancelCompletedOrder() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.getOrderStatus().equals("Đã giao")) {
            Assert.assertFalse(orderDetailPage.isCancelButtonEnabled(), "Vẫn có thể hủy đơn đã giao");
        }
    }

    @Test(description = "TC13-F0023 - Đóng popup khi nhấn hủy")
    public void TC13_F0023_modalClosedOnDismiss() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            orderDetailPage.dismissCancelModal();
            Assert.assertFalse(orderDetailPage.isCancelModalDisplayed(), "Popup không đóng lại");
        }
    }

    @Test(description = "TC13-UI0028 - Popup xác nhận hủy đơn")
    public void TC13_UI0028_cancelModalDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            Assert.assertTrue(orderDetailPage.isCancelModalDisplayed(), "Popup hủy đơn không hiển thị");
            orderDetailPage.dismissCancelModal();
        }
    }

    @Test(description = "TC13-UI0029 - Ô nhập lý do hủy")
    public void TC13_UI0029_cancelReasonInputDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            Assert.assertTrue(orderDetailPage.isCancelReasonInputDisplayed(), "Ô nhập lý do không hiển thị");
            orderDetailPage.dismissCancelModal();
        }
    }

    @Test(description = "TC13-UI0030 - Nút Hủy trên popup")
    public void TC13_UI0030_cancelModalNoButtonDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            Assert.assertTrue(orderDetailPage.isCancelModalNoButtonDisplayed(), "Nút Hủy (đóng) không hiển thị");
            orderDetailPage.dismissCancelModal();
        }
    }

    @Test(description = "TC13-UI0031 - Nút Xác nhận trên popup")
    public void TC13_UI0031_cancelModalYesButtonDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            Assert.assertTrue(orderDetailPage.isConfirmCancelButtonDisplayed(), "Nút Xác nhận không hiển thị");
            orderDetailPage.dismissCancelModal();
        }
    }

    @Test(description = "TC13-UI0032 - Lớp nền mờ khi mở popup")
    public void TC13_UI0032_cancelModalOverlayDisplayed() {
        orderHistoryPage.openOrderHistoryPage();
        orderHistoryPage.openFirstOrderDetail();
        if (orderDetailPage.isCancelButtonEnabled()) {
            orderDetailPage.openCancelModal();
            Assert.assertTrue(orderDetailPage.isCancelModalDisplayed(), "Popup và background mờ không hiển thị");
            orderDetailPage.dismissCancelModal();
        }
    }
}
