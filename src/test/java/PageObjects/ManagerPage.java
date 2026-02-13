package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class ManagerPage extends PageObject {

    @FindBy(tagName = "h2")
    private WebElement title;

    @FindBy(xpath = "//h2[text()='Devices Inventory']/following-sibling::table[1]/tbody/tr")
    private List<WebElement> deviceRows;

    @FindBy(xpath = "//h2[text()='Sensors Inventory']/following-sibling::table[1]/tbody/tr")
    private List<WebElement> sensorRows;

    public ManagerPage(WebDriver driver) {
        super(driver);
    }

    public String getTitle() {
        return title.getText();
    }


    public String getFirstDeviceId() {
        if (!deviceRows.isEmpty()) {
            return deviceRows.get(0).findElement(org.openqa.selenium.By.xpath("./td[1]")).getText();
        }
        return null;
    }

    public String getFirstSensorId() {
        if (!sensorRows.isEmpty()) {
            return sensorRows.get(0).findElement(org.openqa.selenium.By.xpath("./td[1]")).getText();
        }
        return null;
    }

    public int getDeviceCount() {
        return deviceRows.size();
    }

    public int getSensorCount() {
        return sensorRows.size();
    }
}
