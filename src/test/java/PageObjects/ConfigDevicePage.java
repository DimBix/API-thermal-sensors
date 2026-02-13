package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ConfigDevicePage extends PageObject {

    @FindBy(tagName = "h1")
    public WebElement title;

    @FindBy(css = "section ul li span")
    private WebElement deviceIdSpan;

    @FindBy(linkText = "Back to list")
    private WebElement deviceListLink;

    public ConfigDevicePage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return title.getText();
    }

    public String getDeviceId() {
        return deviceIdSpan.getText();
    }

    public EmployeePage clickOnDeviceList() {
        deviceListLink.click();
        return new EmployeePage(driver);
    }
}
