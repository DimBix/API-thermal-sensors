package PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class EmployeePage extends PageObject {

    @FindBy(tagName = "h1")
    private WebElement header;

    @FindBy(linkText = "delete")
    private WebElement deleteLink;

    @FindBy(linkText = "show")
    private WebElement showLink;

    @FindBy(linkText = "edit")
    private WebElement editLink;

    @FindBy(linkText = "Calibrate")
    private WebElement calibrateLink;

    @FindBy(css = "table tbody tr")
    private List<WebElement> deviceRows;


    public EmployeePage(WebDriver driver) {
        super(driver);
    }

    public String getHeaderText() {
        return header.getText();
    }

    public EditConfigDevicePage clickEditConfigDevice() {
        editLink.click();
        return new EditConfigDevicePage(driver);
    }

    public ConfigDevicePage clickConfigDevice() {
        showLink.clear();
        return new ConfigDevicePage(driver);
    }

    public EmployeePage clickCalibrateLink() {
        calibrateLink.click();
        return new EmployeePage(driver);
    }

    public EmployeePage clickDeleteLink() {
        deleteLink.click();
        return new EmployeePage(driver);
    }

    public boolean isCalibrateLinkPresentInFirstRow() {
        if (deviceRows.isEmpty()) {
            return false;
        }

        WebElement firstRow = deviceRows.get(0);
        try {
            WebElement calibrateLink = firstRow.findElement(By.xpath("./td[4]/a[text()='Calibrate']"));
            return calibrateLink.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }

    public ConfigDevicePage clickShowConfigFirstRow() {
        if (deviceRows.isEmpty()) {
            throw new RuntimeException("Nessun dispositivo presente nella tabella!");
        }

        WebElement firstRow = deviceRows.get(0);

        WebElement showLink = firstRow.findElement(By.linkText("show"));

        showLink.click();
        return new ConfigDevicePage(driver);
    }

    public EditConfigDevicePage clickEditConfigFirstRow() {
        if (deviceRows.isEmpty()) {
            throw new RuntimeException("Impossibile modificare: la tabella è vuota!");
        }

        WebElement firstRow = deviceRows.get(0);

        WebElement editLink = firstRow.findElement(By.linkText("edit"));

        editLink.click();

        return new EditConfigDevicePage(driver);
    }

    public int getTotalDeviceRows() {
        return deviceRows.size();
    }
}

