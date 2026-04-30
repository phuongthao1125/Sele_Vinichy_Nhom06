package pageobjects;

import common.Constant;
import common.WaitUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartPage extends GeneralPage {

    private final String CART_URL = "http://localhost:8080/gio-hang";

    private final By cartItem = By.cssSelector(".cart-item");
    private final By breadcrumbHome = By.cssSelector(".breadcrumb a[href='/']");
    private final By checkoutBtn = By.cssSelector(".btn-checkout");

    private final By qtyInput = By.cssSelector(".qty-input");
    private final By plusBtn = By.cssSelector(".qty-btn.plus");
    private final By minusBtn = By.cssSelector(".qty-btn.minus");

    private final By itemPrice = By.cssSelector(".item-price");
    private final By itemTotalPrice = By.cssSelector(".item-total-price");
    private final By totalAmount = By.cssSelector(".total-amount");

    private final By deleteBtn = By.cssSelector(".item-delete-btn");
    private final By deleteModal = By.id("deleteConfirmModal");
    private final By confirmDeleteBtn = By.id("btnConfirmDelete");
    private final By cancelDeleteBtn = By.cssSelector(".btn-no");

    private final By emptyCart = By.cssSelector(".empty-cart");
    private final By continueShoppingBtn = By.cssSelector(".btn-continue-shopping");

    private final By variantSelect = By.cssSelector(".variant-select");
    private final By outOfStockTag = By.cssSelector(".out-of-stock-tag");

    private final By relatedCard = By.cssSelector(".related-card");
    private final By productName = By.cssSelector(".item-name");
    private final By toast = By.id("toastNotification");

    // Selector cho login popup — điều chỉnh nếu id/class thực tế khác
    private final By loginPopup = By.cssSelector(".login-modal, #loginModal, .modal-login");

    public void open() {
        Constant.WEBDRIVER.get(CART_URL);
    }

    public boolean hasCartItem() {
        return WaitUtil.isVisible(cartItem, 3);
    }

    public int getItemCount() {
        return Constant.WEBDRIVER.findElements(cartItem).size();
    }

    public boolean isEmptyCartDisplayed() {
        return WaitUtil.isVisible(emptyCart, 3);
    }

    public void clickHomeBreadcrumb() {
        WaitUtil.click(breadcrumbHome);
    }

    public void clickCheckout() {
        WaitUtil.click(checkoutBtn);
    }

    public WebElement firstItem() {
        return WaitUtil.waitForVisible(cartItem);
    }

    public WebElement findCartItemBySku(String sku) {
        return WaitUtil.waitForVisible(cartItemBySku(sku));
    }

    public boolean hasCartItemBySku(String sku) {
        return WaitUtil.isVisible(cartItemBySku(sku), 3);
    }

    public int getQuantityBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return Integer.parseInt(item.findElement(qtyInput).getAttribute("value").trim());
    }

    public void setQuantityBySku(String sku, String value) {
        WebElement item = findCartItemBySku(sku);
        setQuantity(item, value);
        waitAfterCartAction();
    }

    public void clickPlusBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        item.findElement(plusBtn).click();
        waitAfterCartAction();
    }

    public void clickMinusBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        item.findElement(minusBtn).click();
        waitAfterCartAction();
    }

    public void clickPlusUntilReachStockBySku(String sku) {
        int retry = 0;

        while (retry < 3) {
            try {
                WebElement item = findCartItemBySku(sku);
                int stock = getStock(item);

                // set về max stock
                setQuantity(item, String.valueOf(stock));

                waitAfterCartAction();

                // ⚠️ LẤY LẠI element mới sau khi DOM reload
                item = findCartItemBySku(sku);

                item.findElement(plusBtn).click();

                waitAfterCartAction();

                return;

            } catch (StaleElementReferenceException e) {
                retry++;
            }
        }

        throw new RuntimeException("❌ Failed clickPlusUntilReachStockBySku due to stale element");
    }

    public void clickMinusWhenQtyIsOneBySku(String sku) {
        WebElement item = findCartItemBySku(sku);

        setQuantity(item, "1");

        item = findCartItemBySku(sku);
        item.findElement(minusBtn).click();

        sleep(700);
    }

    public void clickDeleteBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        item.findElement(deleteBtn).click();
        sleep(500);
    }

    public void confirmDelete() {
        WaitUtil.click(confirmDeleteBtn);
        waitAfterCartAction();
    }

    public void cancelDelete() {
        WaitUtil.click(cancelDeleteBtn);
        sleep(500);
    }

    public boolean isDeleteModalDisplayed() {
        return WaitUtil.isVisible(deleteModal, 3)
                && Constant.WEBDRIVER.findElement(deleteModal).getAttribute("class").contains("active");
    }

    public long getUnitPriceBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return parseMoney(item.findElement(itemPrice).getText());
    }

    public long getItemTotalBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return parseMoney(item.findElement(itemTotalPrice).getText());
    }

    public long getSummaryTotal() {
        return parseMoney(WaitUtil.waitForVisible(totalAmount).getText());
    }

    public long sumAllItemTotals() {
        long sum = 0;
        for (WebElement item : Constant.WEBDRIVER.findElements(cartItem)) {
            sum += parseMoney(item.findElement(itemTotalPrice).getText());
        }
        return sum;
    }

    public int getVariantOptionCountBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return item.findElement(variantSelect).findElements(By.tagName("option")).size();
    }

    public String getSelectedVariantValueBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return new Select(item.findElement(variantSelect))
                .getFirstSelectedOption()
                .getAttribute("value");
    }

    public void changeVariantBySku(String sku, String skuVariantChanged) {
        WebElement item = findCartItemBySku(sku);
        Select select = new Select(item.findElement(variantSelect));

        String current = select.getFirstSelectedOption().getAttribute("value");

        for (WebElement option : select.getOptions()) {
            String value = option.getAttribute("value");
            if (!value.equals(current)) {
                select.selectByValue(value);
                break;
            }
        }

        waitAfterCartAction();
    }

    public boolean isCheckoutDisabled() {
        WebElement checkout = WaitUtil.waitForVisible(checkoutBtn);
        return "button".equalsIgnoreCase(checkout.getTagName())
                || checkout.getAttribute("disabled") != null
                || !checkout.isEnabled();
    }

    public boolean hasOutOfStockTag() {
        return WaitUtil.isVisible(outOfStockTag, 3);
    }

    public void clickContinueShopping() {
        WaitUtil.click(continueShoppingBtn);
    }

    public void clickProductName() {
        firstItem().findElement(productName).click();
    }

    public boolean hasRelatedProduct() {
        return WaitUtil.isVisible(relatedCard, 3);
    }

    // ✅ FIX TC10_F024: Thêm method click vào sản phẩm liên quan đầu tiên
    public void clickFirstRelatedProduct() {
        WebElement first = WaitUtil.waitForVisible(relatedCard);
        first.click();
    }

    public String getToastText() {
        try {
            WebElement toastEl = new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                    .until(driver -> {
                        WebElement el = driver.findElement(By.id("toastNotification"));
                        return el.isDisplayed() ? el : null;
                    });

            return toastEl.getText().trim();

        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Kiểm tra popup đăng nhập có hiển thị không (dùng cho TC10_F001).
     * Nếu selector chưa đúng, cập nhật By loginPopup theo class/id thực tế của dự
     * án.
     */
    public boolean isLoginPopupDisplayed() {
        return WaitUtil.isVisible(loginPopup, 3);
    }

    public void simulateApiErrorForQuantity() {
        executeJs(
                "window.originalFetch = window.fetch;" +
                        "window.fetch = function(){ return Promise.reject(new Error('mock quantity error')); };");
    }

    public void simulateApiErrorForVariant() {
        executeJs(
                "window.originalFetch = window.fetch;" +
                        "window.fetch = function(){ return Promise.reject(new Error('mock variant error')); };");
    }

    public void restoreFetch() {
        executeJs("if(window.originalFetch){ window.fetch = window.originalFetch; }");
    }

    private void setQuantity(WebElement item, String value) {
        WebElement input = item.findElement(qtyInput);
        input.click();
        input.sendKeys(Keys.CONTROL + "a");
        input.sendKeys(value);
        input.sendKeys(Keys.TAB);
    }

    private By cartItemBySku(String sku) {
        String normalizedSku = sku.replace(" ", "");
        return By.xpath("//div[contains(@class,'cart-item')][.//*[contains(translate(normalize-space(.), ' ', ''), "
                + xpathLiteral(normalizedSku) + ")]]");
    }

    private String xpathLiteral(String value) {
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        return "concat('" + value.replace("'", "',\"'\",'") + "')";
    }

    private void waitAfterCartAction() {
        sleep(1200);
        try {
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                    .until(driver -> "complete".equals(
                            ((JavascriptExecutor) driver).executeScript("return document.readyState")));
        } catch (Exception ignored) {
        }
    }

    private int getStock(WebElement item) {
        String onclick = item.findElement(plusBtn).getAttribute("onclick");
        String stockText = onclick.replaceAll(".*handleUpdateQty\\(this,\\s*1,\\s*(\\d+)\\).*", "$1");
        return Integer.parseInt(stockText);
    }

    private long parseMoney(String text) {
        String number = text.replaceAll("[^0-9]", "");
        if (number.isEmpty())
            return 0;
        return Long.parseLong(number);
    }

    private void executeJs(String script) {
        ((JavascriptExecutor) Constant.WEBDRIVER).executeScript(script);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
        }
    }

    // ================= UI METHODS =================

    public boolean isBreadcrumbDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".breadcrumb"), 3);
    }

    public String getBreadcrumbText() {
        return WaitUtil.waitForVisible(By.cssSelector(".breadcrumb")).getText().trim();
    }

    public String getTitleText() {
        return WaitUtil.waitForVisible(By.cssSelector(".page-title"))
                .getText()
                .replaceAll("\\s+", " ")
                .trim();
    }

    public boolean isProductImageDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".cart-item img"), 3);
    }

    public boolean isProductNameDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".item-name"), 3);
    }

    public boolean isQuantityControlDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".qty-input"), 3)
                && WaitUtil.isVisible(By.cssSelector(".qty-btn.plus"), 3)
                && WaitUtil.isVisible(By.cssSelector(".qty-btn.minus"), 3);
    }

    public boolean isPriceDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".item-price"), 3)
                && WaitUtil.isVisible(By.cssSelector(".item-total-price"), 3);
    }

    public boolean isDeleteIconDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".item-delete-btn"), 3);
    }

    public boolean isSummaryDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".cart-summary"), 3)
                || WaitUtil.isVisible(By.cssSelector(".total-amount"), 3);
    }

    public boolean isCheckoutButtonDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".btn-checkout"), 3);
    }

    public boolean isContinueShoppingDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".btn-continue-shopping"), 3);
    }

    public boolean isVariantDropdownDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".variant-select"), 3);
    }

    public boolean isTitleBold() {
        WebElement title = WaitUtil.waitForVisible(By.cssSelector(".page-title"));
        String fontWeight = title.getCssValue("font-weight");
        return Integer.parseInt(fontWeight) >= 600 || fontWeight.equalsIgnoreCase("bold");
    }

    public boolean isTitleLeftAligned() {
        WebElement title = WaitUtil.waitForVisible(By.cssSelector(".page-title"));
        String textAlign = title.getCssValue("text-align");
        return textAlign.equalsIgnoreCase("left") || textAlign.equalsIgnoreCase("start");
    }

    public boolean isPriceTextContainsCurrency() {
        String price = WaitUtil.waitForVisible(By.cssSelector(".item-price")).getText();
        String total = WaitUtil.waitForVisible(By.cssSelector(".item-total-price")).getText();
        return price.contains("đ") && total.contains("đ");
    }

    public boolean isSupportTextDisplayed() {
        return Constant.WEBDRIVER.getPageSource().contains("Hỗ trợ")
                || Constant.WEBDRIVER.getPageSource().contains("hotline")
                || Constant.WEBDRIVER.getPageSource().contains("Hotline")
                || Constant.WEBDRIVER.getPageSource().matches("(?s).*0\\d{9,10}.*");
    }

    public boolean isTotalAmountDisplayed() {
        return WaitUtil.isVisible(By.cssSelector(".total-amount"), 3);
    }

    public boolean isTotalAmountRed() {
        WebElement total = WaitUtil.waitForVisible(By.cssSelector(".total-amount"));
        String color = total.getCssValue("color");
        return color.contains("208") || color.contains("209") || color.contains("rgb(208")
                || color.contains("rgb(209") || color.contains("rgb(211");
    }

    public boolean isCheckoutButtonBlackWithWhiteText() {
        WebElement btn = WaitUtil.waitForVisible(By.cssSelector(".btn-checkout"));
        String bg = btn.getCssValue("background-color");
        String color = btn.getCssValue("color");

        boolean blackBg = bg.contains("0, 0, 0") || bg.contains("rgb(0");
        boolean whiteText = color.contains("255, 255, 255") || color.contains("rgb(255");

        return blackBg && whiteText;
    }

    public String getEmptyCartText() {
        return WaitUtil.waitForVisible(By.cssSelector(".empty-cart"))
                .getText()
                .replaceAll("\\s+", " ")
                .trim();
    }

    public String getSelectedVariantTextBySku(String sku) {
        WebElement item = findCartItemBySku(sku);
        return new Select(item.findElement(By.cssSelector(".variant-select")))
                .getFirstSelectedOption()
                .getText()
                .trim();
    }

    public boolean isVariantDropdownHasNoDuplicateOptions(String sku) {
        WebElement item = findCartItemBySku(sku);
        Select select = new Select(item.findElement(By.cssSelector(".variant-select")));

        java.util.Set<String> values = new java.util.HashSet<>();

        for (WebElement option : select.getOptions()) {
            String text = option.getText().trim();
            if (!values.add(text)) {
                return false;
            }
        }

        return true;
    }

    public boolean isToastAutoHidden() {
        try {
            new WebDriverWait(Constant.WEBDRIVER, Duration.ofSeconds(5))
                    .until(driver -> {
                        WebElement el = driver.findElement(By.id("toastNotification"));
                        return !el.isDisplayed() || el.getText().trim().isEmpty();
                    });
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}