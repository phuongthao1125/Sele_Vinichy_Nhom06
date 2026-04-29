package testcases;

import common.Constant;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pageobjects.CartPage;
import pageobjects.CheckoutPage;
import testdata.CartTestData;
import testdata.CartTestData.CartLine;

import java.util.Map;

public class CartTests extends testcases.BaseTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CART_URL = BASE_URL + "/gio-hang";

    private static final String EMAIL = "phanthanhthao892005@gmail.com";
    private static final String PASSWORD = "123456";

    private static final String SKU_NORMAL = "HA634-DEN";
    private static final String SKU_STOCK_1 = "HA761-NAU";
    private static final String SKU_VARIANT = "HA623-HONG";
    private static final String SKU_VARIANT_CHANGED = "HA623-DEN";
    private static final String SKU_OUT_OF_STOCK = "HA494-HONG";
    private static final String SKU_DELETE_A = "TOTE-BAN-NGUYET-TRANG";
    private static final String SKU_DELETE_B = "TOTE-CO-DIEN-DEN";

    private static final int NORMAL_STOCK = 10;
    private static final int STOCK_ONE = 1;
    private static final int DEFAULT_STOCK = 100;
    private static final int OUT_OF_STOCK = 0;

    private final CartTestData cartTestData = new CartTestData();

    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    @BeforeMethod
    public void initPage() {
        cartPage = new CartPage();
        checkoutPage = new CheckoutPage();
    }

    private void openCartWith(CartLine... lines) {
        cartTestData.seedCart(EMAIL, PASSWORD, lines);
        loginByApi();
        cartPage.open();

        if (lines.length == 0) {
            Assert.assertTrue(cartPage.isEmptyCartDisplayed(), "Expected an empty cart");
            return;
        }

        for (CartLine line : lines) {
            Assert.assertTrue(
                    cartPage.hasCartItemBySku(line.sku()),
                    "Missing seeded SKU in cart: " + line.sku()
            );
        }
    }

    private void loginByApi() {
        Constant.WEBDRIVER.manage().deleteAllCookies();
        Constant.WEBDRIVER.get(BASE_URL);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) ((JavascriptExecutor) Constant.WEBDRIVER)
                .executeAsyncScript(
                        "const done = arguments[arguments.length - 1];" +
                                "fetch('/api/auth/login', {" +
                                "  method: 'POST'," +
                                "  headers: {'Content-Type': 'application/json'}," +
                                "  body: JSON.stringify({email: arguments[0], matKhau: arguments[1]})" +
                                "}).then(async response => done({status: response.status, body: await response.text()}))" +
                                ".catch(error => done({status: 0, body: String(error)}));",
                        EMAIL,
                        PASSWORD
                );

        int status = ((Number) result.get("status")).intValue();
        Assert.assertEquals(status, 200, "Login API failed: " + result.get("body"));
    }

    private CartLine normal(int quantity) {
        return CartTestData.line(SKU_NORMAL, quantity, NORMAL_STOCK);
    }

    private CartLine stockOne() {
        return CartTestData.line(SKU_STOCK_1, 1, STOCK_ONE);
    }

    private CartLine variant() {
        return CartTestData.line(SKU_VARIANT, 1, DEFAULT_STOCK);
    }

    private CartLine variantChanged() {
        return CartTestData.line(SKU_VARIANT_CHANGED, 1, DEFAULT_STOCK);
    }

    private CartLine outOfStock() {
        return CartTestData.line(SKU_OUT_OF_STOCK, 1, OUT_OF_STOCK);
    }

    private CartLine deleteA() {
        return CartTestData.line(SKU_DELETE_A, 1, DEFAULT_STOCK);
    }

    private CartLine deleteB() {
        return CartTestData.line(SKU_DELETE_B, 1, DEFAULT_STOCK);
    }

    private void assertToastContains(String expectedMessage) {
        String actualMessage = cartPage.getToastText();

        Assert.assertFalse(actualMessage.isBlank(), "Expected a toast message");
        Assert.assertTrue(
                actualMessage.contains(expectedMessage),
                "Sai nội dung thông báo. Expected contains: " + expectedMessage + ", Actual: " + actualMessage
        );
    }

    private boolean waitForLoginPopupDisplayed() {
        long endTime = System.currentTimeMillis() + 7000;

        while (System.currentTimeMillis() < endTime) {
            Boolean visible = (Boolean) ((JavascriptExecutor) Constant.WEBDRIVER)
                    .executeScript(
                            "const el = document.querySelector('#loginPopup');" +
                                    "return !!el && el.classList.contains('active') && " +
                                    "window.getComputedStyle(el).display !== 'none';"
                    );

            if (Boolean.TRUE.equals(visible)) {
                return true;
            }

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    @Test(priority = 1)
    public void TC10_F001() {
        Constant.WEBDRIVER.manage().deleteAllCookies();
        Constant.WEBDRIVER.get(BASE_URL);

        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript(
                "window.isLoggedIn = false;" +
                        "document.body.setAttribute('data-logged-in', 'false');"
        );

        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript(
                "handleCartClick(new MouseEvent('click', {bubbles:true, cancelable:true}));"
        );

        Assert.assertTrue(
                waitForLoginPopupDisplayed(),
                "Khi chưa đăng nhập và click icon giỏ hàng, hệ thống phải hiển thị popup đăng nhập"
        );

        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().equals(BASE_URL + "/")
                        || Constant.WEBDRIVER.getCurrentUrl().equals(BASE_URL + "/#"),
                "Khi chưa đăng nhập, hệ thống không được chuyển sang trang giỏ hàng"
        );
    }

    @Test(priority = 2)
    public void TC10_F002() {
        openCartWith(normal(1));

        cartPage.clickHomeBreadcrumb();

        Assert.assertEquals(
                Constant.WEBDRIVER.getCurrentUrl(),
                BASE_URL + "/",
                "Không điều hướng đúng về trang chủ"
        );
    }

    @Test(priority = 3)
    public void TC10_F003() {
        openCartWith(normal(1));

        cartPage.clickCheckout();

        Assert.assertTrue(checkoutPage.isAtCheckoutPage(), "Không điều hướng đến trang đặt hàng");
    }

    @Test(priority = 4)
    public void TC10_F004() {
        openCartWith(normal(1));

        int beforeQty = cartPage.getQuantityBySku(SKU_NORMAL);

        cartPage.clickPlusBySku(SKU_NORMAL);

        Assert.assertEquals(cartPage.getQuantityBySku(SKU_NORMAL), beforeQty + 1);
        Assert.assertEquals(cartPage.getSummaryTotal(), cartPage.sumAllItemTotals());
    }

    @Test(priority = 5)
    public void TC10_F005() {
        openCartWith(normal(2));

        int beforeQty = cartPage.getQuantityBySku(SKU_NORMAL);

        cartPage.clickMinusBySku(SKU_NORMAL);

        Assert.assertEquals(cartPage.getQuantityBySku(SKU_NORMAL), beforeQty - 1);
        Assert.assertEquals(cartPage.getSummaryTotal(), cartPage.sumAllItemTotals());
    }

    @Test(priority = 6)
    public void TC10_F006() {
        openCartWith(stockOne());

        cartPage.clickPlusUntilReachStockBySku(SKU_STOCK_1);

        Assert.assertEquals(cartPage.getQuantityBySku(SKU_STOCK_1), STOCK_ONE);
        assertToastContains("Số lượng vượt ngưỡng tồn kho");
    }

    @Test(priority = 7)
    public void TC10_F007() {
        openCartWith(normal(1));

        cartPage.setQuantityBySku(SKU_NORMAL, "2");

        Assert.assertEquals(cartPage.getQuantityBySku(SKU_NORMAL), 2);
        Assert.assertEquals(
                cartPage.getItemTotalBySku(SKU_NORMAL),
                cartPage.getUnitPriceBySku(SKU_NORMAL) * 2,
                "Thành tiền từng sản phẩm chưa cập nhật đúng"
        );
        Assert.assertEquals(
                cartPage.getSummaryTotal(),
                cartPage.sumAllItemTotals(),
                "Tổng tiền chưa cập nhật đúng sau khi nhập số lượng hợp lệ"
        );
    }

    @Test(priority = 8)
    public void TC10_F008() {
        openCartWith(normal(2));

        cartPage.setQuantityBySku(SKU_NORMAL, "0");

        Assert.assertTrue(cartPage.getQuantityBySku(SKU_NORMAL) >= 1);
    }

    @Test(priority = 9)
    public void TC10_F009() {
        openCartWith(normal(1));

        cartPage.setQuantityBySku(SKU_NORMAL, "999");

        Assert.assertTrue(cartPage.getQuantityBySku(SKU_NORMAL) <= NORMAL_STOCK);
    }

    @Test(priority = 10)
    public void TC10_F010() {
        openCartWith(normal(3));

        int beforeQty = cartPage.getQuantityBySku(SKU_NORMAL);

        cartPage.setQuantityBySku(SKU_NORMAL, "abc");

        Assert.assertEquals(cartPage.getQuantityBySku(SKU_NORMAL), beforeQty);
    }

    @Test(priority = 11)
    public void TC10_F011() {
        openCartWith(normal(2), deleteA(), deleteB());

        Assert.assertEquals(cartPage.getSummaryTotal(), cartPage.sumAllItemTotals());
    }

    @Test(priority = 12)
    public void TC10_F012() {
        openCartWith(normal(1));

        cartPage.setQuantityBySku(SKU_NORMAL, "2");

        Assert.assertEquals(
                cartPage.getItemTotalBySku(SKU_NORMAL),
                cartPage.getUnitPriceBySku(SKU_NORMAL) * 2
        );
    }

    @Test(priority = 13)
    public void TC10_F013() {
        openCartWith(stockOne());

        cartPage.clickMinusWhenQtyIsOneBySku(SKU_STOCK_1);

        Assert.assertTrue(cartPage.isDeleteModalDisplayed());
        Assert.assertTrue(cartPage.hasCartItemBySku(SKU_STOCK_1));
    }

    @Test(priority = 14)
    public void TC10_F014() {
        openCartWith(normal(1), deleteA(), deleteB());

        int beforeItemCount = cartPage.getItemCount();

        cartPage.clickDeleteBySku(SKU_DELETE_A);
        cartPage.confirmDelete();

        Assert.assertEquals(cartPage.getItemCount(), beforeItemCount - 1);
        Assert.assertFalse(cartPage.hasCartItemBySku(SKU_DELETE_A));
        Assert.assertEquals(cartPage.getSummaryTotal(), cartPage.sumAllItemTotals());
    }

    @Test(priority = 15)
    public void TC10_F015() {
        openCartWith(normal(1));

        cartPage.clickDeleteBySku(SKU_NORMAL);
        cartPage.confirmDelete();

        Assert.assertTrue(cartPage.isEmptyCartDisplayed());
    }

    @Test(priority = 16)
    public void TC10_F016() {
        openCartWith(normal(1), deleteB());

        cartPage.clickDeleteBySku(SKU_DELETE_B);
        cartPage.cancelDelete();

        Assert.assertTrue(cartPage.hasCartItemBySku(SKU_DELETE_B));
    }

    @Test(priority = 17)
    public void TC10_F017() {
        openCartWith(variant());

        cartPage.changeVariantBySku(SKU_VARIANT, SKU_VARIANT_CHANGED);

        Assert.assertTrue(
                cartPage.hasCartItemBySku(SKU_VARIANT_CHANGED),
                "Variant mới phải xuất hiện trong giỏ sau khi đổi"
        );
        Assert.assertFalse(
                cartPage.hasCartItemBySku(SKU_VARIANT),
                "Variant cũ phải được thay thế"
        );
    }

    @Test(priority = 18)
    public void TC10_F018() {
        openCartWith(variant(), variantChanged());

        int beforeItemCount = cartPage.getItemCount();

        cartPage.changeVariantBySku(SKU_VARIANT, SKU_VARIANT_CHANGED);

        Assert.assertEquals(
                cartPage.getItemCount(),
                beforeItemCount - 1,
                "Đổi sang phân loại đã tồn tại thì phải gộp dòng"
        );
        Assert.assertFalse(
                cartPage.hasCartItemBySku(SKU_VARIANT),
                "SKU cũ phải biến mất sau khi gộp"
        );
        Assert.assertTrue(
                cartPage.hasCartItemBySku(SKU_VARIANT_CHANGED),
                "SKU variant đã tồn tại phải còn lại sau khi gộp"
        );
        Assert.assertEquals(
                cartPage.getQuantityBySku(SKU_VARIANT_CHANGED),
                2,
                "Số lượng sau khi gộp phải được cộng dồn"
        );
        Assert.assertEquals(
                cartPage.getSummaryTotal(),
                cartPage.sumAllItemTotals(),
                "Tổng tiền sau khi gộp variant chưa đúng"
        );
    }

    @Test(priority = 19)
    public void TC10_F019() {
        openCartWith(outOfStock());

        Assert.assertTrue(cartPage.hasOutOfStockTag());
        Assert.assertTrue(cartPage.isCheckoutDisabled());
    }

    @Test(priority = 20)
    public void TC10_F020() {
        openCartWith(outOfStock());

        Assert.assertTrue(
                cartPage.isCheckoutDisabled(),
                "Khi giỏ chỉ có sản phẩm hết hàng thì nút Tiến hành đặt hàng phải bị vô hiệu hóa"
        );
        Assert.assertTrue(
                Constant.WEBDRIVER.getCurrentUrl().contains("/gio-hang"),
                "Vẫn phải ở lại trang giỏ hàng"
        );
    }

    @Test(priority = 21)
    public void TC10_F021() {
        openCartWith(outOfStock(), normal(1));

        cartPage.clickCheckout();

        Assert.assertTrue(
                checkoutPage.isAtCheckoutPage(),
                "Không điều hướng sang trang đặt hàng khi giỏ có ít nhất 1 sản phẩm còn hàng"
        );

        Assert.assertFalse(
                Constant.WEBDRIVER.getCurrentUrl().contains("/gio-hang"),
                "Khi giỏ có sản phẩm còn hàng thì không được ở lại trang giỏ hàng"
        );
    }

    @Test(priority = 22)
    public void TC10_F022() {
        openCartWith();

        cartPage.clickContinueShopping();

        Assert.assertTrue(Constant.WEBDRIVER.getCurrentUrl().contains("/san-pham"));
    }

    @Test(priority = 23)
    public void TC10_F023() {
        openCartWith(normal(1));

        cartPage.clickProductName();

        Assert.assertTrue(Constant.WEBDRIVER.getCurrentUrl().contains("/san-pham/"));
    }

    @Test(priority = 24)
    public void TC10_F024() {
        openCartWith(normal(1));

        Assert.assertTrue(cartPage.hasRelatedProduct());

        cartPage.clickFirstRelatedProduct();

        Assert.assertTrue(Constant.WEBDRIVER.getCurrentUrl().contains("/san-pham/"));
    }

    @Test(priority = 25)
    public void TC10_F025() {
        openCartWith(normal(1));

        int beforeQty = cartPage.getQuantityBySku(SKU_NORMAL);

        cartPage.simulateApiErrorForQuantity();
        cartPage.clickPlusBySku(SKU_NORMAL);

        assertToastContains("Lỗi kết nối. Vui lòng thử lại!");
        Assert.assertEquals(
                cartPage.getQuantityBySku(SKU_NORMAL),
                beforeQty,
                "Số lượng không được thay đổi khi API cập nhật số lượng lỗi"
        );
    }

    @Test(priority = 26)
    public void TC10_F026() {
        openCartWith(variant());

        int beforeItemCount = cartPage.getItemCount();

        cartPage.simulateApiErrorForVariant();
        cartPage.changeVariantBySku(SKU_VARIANT, SKU_VARIANT_CHANGED);

        assertToastContains("Lỗi kết nối khi đổi màu sắc!");
        Assert.assertEquals(
                cartPage.getItemCount(),
                beforeItemCount,
                "Giỏ hàng không được thay đổi khi API đổi phân loại lỗi"
        );
    }
}