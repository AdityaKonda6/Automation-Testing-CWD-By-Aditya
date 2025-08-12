
package pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    WebDriver driver;

    private final By email = By.cssSelector("input[type='text'][placeholder=' ']");    
    private final By password = By.cssSelector("input[type='password'][placeholder=' ']"); 
    private final By loginBtn = By.cssSelector("button[type='submit']"); 
    private final By errorMsg = By.cssSelector(".alert.alert-danger"); 
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }


    public void goTo() {
        driver.get("https://dashboard.sm.test.cwdin.com/login");
    }

    public void enterEmail(String emailText) {
        driver.findElement(email).clear();
        driver.findElement(email).sendKeys(emailText);
    }

    public void enterPassword(String passwordText) {
        driver.findElement(password).clear();
        driver.findElement(password).sendKeys(passwordText);
    }

    public void clickLogin() {
        driver.findElement(loginBtn).click();
    }

    public String getError() {
        return driver.findElement(errorMsg).getText();
    }

    public void login(String your_emailexamplecom, String your_password) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
