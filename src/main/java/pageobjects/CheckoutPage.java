package pageobjects;

import common.Constant;
import common.WaitUtil;
import org.openqa.selenium.By;

public class CheckoutPage extends GeneralPage {

    private final By checkoutTitle = By.xpath("//*[contains(text(),'ĐẶT HÀNG') or contains(text(),'Đặt hàng')]");
    private final By checkoutContent = By.xpath("//*[contains(text(),'Thông tin giao hàng') or contains(text(),'Thanh toán') or contains(text(),'Đơn hàng')]");

    public boolean isAtCheckoutPage() {
        return Constant.WEBDRIVER.getCurrentUrl().contains("/dat-hang")
                || WaitUtil.isVisible(checkoutTitle, 3)
                || WaitUtil.isVisible(checkoutContent, 3);
    }
}