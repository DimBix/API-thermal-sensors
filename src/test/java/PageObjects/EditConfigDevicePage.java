package PageObjects;

import it.univr.track.repository.DeviceService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class EditConfigDevicePage extends PageObject {

    @FindBy(css = "h1 span")
    private WebElement deviceIdSpan;

    @FindBy(css = "table tbody tr")
    private List<WebElement> sensorRows;

    @FindBy(id = "sendButton")
    private WebElement sendConfigButton;

    @FindBy(linkText = "Back to list")
    private WebElement backToLink;

    private DeviceService deviceService;

    public EditConfigDevicePage(WebDriver driver) {
        super(driver);
    }

    public String getDeviceId() {
        return deviceIdSpan.getText();
    }

    public EditConfigDevicePage clickSendConfiguration() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("sendButton")));
        button.click();

        wait.until(ExpectedConditions.alertIsPresent()).accept();

        return new EditConfigDevicePage(driver);
    }


    public EditConfigDevicePage clickConfirmRemoveDevice(Long sensorId) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table tbody tr")));

        for (WebElement row : rows) {
            if (row.getText().contains(String.valueOf(sensorId))) {
                WebElement btn = row.findElement(By.tagName("button"));
                btn.click();

                wait.until(ExpectedConditions.alertIsPresent()).accept();

                return new EditConfigDevicePage(driver);
            }
        }
        throw new RuntimeException("Sensore non trovato: " + sensorId);
    }

    public int getSensorsCount() {

        if (sensorRows.size() == 1 && sensorRows.get(0).getText().contains("No sensors connected to this device.")) {
            return 0;
        }

        return sensorRows.size();
    }

    public EmployeePage clickBackToList() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        WebElement freshBackLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Back to list")));

        freshBackLink.click();

        return new EmployeePage(driver);
    }
}
