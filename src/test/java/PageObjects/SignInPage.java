package PageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class SignInPage extends PageObject{

    @FindBy(id = "username")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(css = "input[type='submit']")
    private WebElement loginButton;

    @FindBy(linkText = "Sign Up")
    private WebElement signUpLink;

    @FindBy(tagName = "h1")
    private WebElement title;

    // Locator for the error message
    @FindBy(xpath = "//div[contains(text(), 'Credenziali non corrette')]")
    private WebElement errorMessage;

    private static final String URL = "http://localhost:8080/";

    public SignInPage(WebDriver driver) {
        super(driver);
    }

    public void openPage() {
        driver.get(URL);
    }

    public String getTitle() {
        return title.getText();
    }


    public EmployeePage enterCredentialsEmployee(String username, String password) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();
        return new EmployeePage(driver);
    }

    public ManagerPage enterCredentialManager(String username, String password) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
        passwordInput.clear();
        passwordInput.sendKeys(password);
        loginButton.click();
        return new ManagerPage(driver);
    }

    public EmployeePage clickLoginButton() {
        loginButton.click();
        return new EmployeePage(driver);
    }

    public void clickSignUp() {
        signUpLink.click();
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
}
