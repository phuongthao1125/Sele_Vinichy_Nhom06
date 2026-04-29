package testcases;

import common.Constant;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.CartPage;
import testdata.CartTestData;

public class CartUITests extends testcases.BaseTest {

    private CartPage cartPage;
    private CartTestData cartTestData;

    private static final String EMAIL = "phanthanhthao892005@gmail.com";
    private static final String PASSWORD = "123456";
    private static final String SKU = "HA634-DEN";

    @BeforeMethod
    public void setup() {
        cartPage = new CartPage();
        cartTestData = new CartTestData();
    }

    private void openCartWithProduct() {
        cartTestData.seedCart(
                EMAIL,
                PASSWORD,
                CartTestData.line(SKU, 1, 10)
        );

        loginByApi();
        cartPage.open();
    }

    private void openEmptyCart() {
        cartTestData.seedCart(EMAIL, PASSWORD);
        loginByApi();
        cartPage.open();
    }

    private void loginByApi() {
        Constant.WEBDRIVER.get("http://localhost:8080");

        ((org.openqa.selenium.JavascriptExecutor) Constant.WEBDRIVER)
                .executeAsyncScript(
                        "const done = arguments[arguments.length - 1];" +
                                "fetch('/api/auth/login', {" +
                                "method: 'POST'," +
                                "headers: {'Content-Type': 'application/json'}," +
                                "body: JSON.stringify({email: arguments[0], matKhau: arguments[1]})" +
                                "}).then(() => done());",
                        EMAIL, PASSWORD
                );
    }

    @Test
    public void TC10_UI001_Breadcrumb() {
        openCartWithProduct();

        Assert.assertTrue(cartPage.isBreadcrumbDisplayed());

        String breadcrumb = cartPage.getBreadcrumbText()
                .replaceAll("\\s+", " ")
                .trim();

        Assert.assertTrue(
                breadcrumb.contains("Trang chủ"),
                "Breadcrumb phải có Trang chủ. Actual: " + breadcrumb
        );

        Assert.assertTrue(
                breadcrumb.contains("Giỏ hàng"),
                "Breadcrumb phải có Giỏ hàng. Actual: " + breadcrumb
        );
    }

    @Test
    public void TC10_UI002_Title() {
        openCartWithProduct();

        String actual = cartPage.getTitleText();

        Assert.assertTrue(
                actual.equalsIgnoreCase("GIỎ HÀNG"),
                "Title sai. Actual: " + actual
        );

        Assert.assertTrue(
                cartPage.isTitleBold(),
                "Tiêu đề GIỎ HÀNG phải in đậm"
        );

        Assert.assertTrue(
                cartPage.isTitleLeftAligned(),
                "Tiêu đề GIỎ HÀNG phải nằm bên trái"
        );
    }

    @Test
    public void TC10_UI003_ProductInfo() {
        openCartWithProduct();

        Assert.assertTrue(cartPage.isProductImageDisplayed(), "Ảnh sản phẩm chưa hiển thị");
        Assert.assertTrue(cartPage.isProductNameDisplayed(), "Tên sản phẩm chưa hiển thị");
        Assert.assertTrue(cartPage.isVariantDropdownDisplayed(), "Dropdown màu chưa hiển thị");
        Assert.assertTrue(cartPage.isQuantityControlDisplayed(), "Bộ tăng giảm số lượng chưa hiển thị");
        Assert.assertTrue(cartPage.isPriceDisplayed(), "Giá hoặc thành tiền chưa hiển thị");
        Assert.assertTrue(cartPage.isPriceTextContainsCurrency(), "Giá/thành tiền phải có ký hiệu đ");
        Assert.assertTrue(cartPage.isDeleteIconDisplayed(), "Icon xóa chưa hiển thị");
    }

    @Test
    public void TC10_UI004_Summary() {
        openCartWithProduct();

        Assert.assertTrue(cartPage.isSummaryDisplayed(), "Khối tổng tiền chưa hiển thị");
        Assert.assertTrue(cartPage.isSupportTextDisplayed(), "Text hỗ trợ/số điện thoại chưa hiển thị");
        Assert.assertTrue(cartPage.isTotalAmountDisplayed(), "Tổng cộng chưa hiển thị");
        Assert.assertTrue(cartPage.isCheckoutButtonDisplayed(), "Nút tiến hành đặt hàng chưa hiển thị");
        Assert.assertTrue(cartPage.isCheckoutButtonBlackWithWhiteText(), "Nút đặt hàng phải nền đen, chữ trắng");
    }

    @Test
    public void TC10_UI006_EmptyCart() {
        openEmptyCart();

        Assert.assertTrue(cartPage.isEmptyCartDisplayed(), "Không hiển thị UI giỏ hàng trống");
        Assert.assertTrue(cartPage.getEmptyCartText().contains("Giỏ hàng của bạn đang trống"), "Sai text giỏ hàng trống");
        Assert.assertTrue(cartPage.isContinueShoppingDisplayed(), "Không hiển thị nút Tiếp tục mua sắm");
        Assert.assertEquals(cartPage.getItemCount(), 0, "Giỏ trống không được hiển thị danh sách sản phẩm");
    }

    @Test
    public void TC10_UI009_DropdownVariant() {
        openCartWithProduct();

        Assert.assertTrue(cartPage.isVariantDropdownDisplayed(), "Dropdown màu chưa hiển thị");
        Assert.assertTrue(cartPage.isVariantDropdownHasNoDuplicateOptions(SKU), "Dropdown màu không được trùng option");
        Assert.assertFalse(cartPage.getSelectedVariantTextBySku(SKU).isBlank(), "Dropdown phải có màu mặc định được chọn");
    }

    @Test
    public void TC10_UI010_Toast() {
        cartTestData.seedCart(
                EMAIL,
                PASSWORD,
                CartTestData.line(SKU, 10, 10)
        );

        loginByApi();
        cartPage.open();

        cartPage.clickPlusBySku(SKU);

        String toast = cartPage.getToastText();

        Assert.assertTrue(
                toast.contains("Số lượng vượt"),
                "Không hiển thị toast khi vượt tồn kho. Actual: " + toast
        );

        Assert.assertTrue(
                cartPage.isToastAutoHidden(),
                "Toast phải tự ẩn sau vài giây"
        );
    }
}