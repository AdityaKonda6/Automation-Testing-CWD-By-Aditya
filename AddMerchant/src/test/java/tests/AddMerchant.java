package tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.LoginPage;

public class AddMerchant {
    WebDriver driver;
    WebDriverWait wait;
    
    LoginPage loginPage;
    AtomicInteger screenshotCounter = new AtomicInteger(1);

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        loginPage = new LoginPage(driver);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void addMerchantFlow() throws IOException, InterruptedException {
        loginPage.goTo();
        loginPage.enterEmail("adityakonda@cwdin.com");
        loginPage.enterPassword("Aditya@123");
        loginPage.clickLogin();
        Thread.sleep(3000);
        takeScreenshot();

        // ✅ Navigate to Entity Management > World > Unlinked Merchant
        waitAndClick("//a[contains(text(),'Entity Management')]");
        waitAndClick("//span[contains(text(),'World')]");
        waitAndClick("//span[contains(text(),' Unlinked Merchant ')]");
        takeScreenshot();

        // ✅ Click Menu Dialpad then Add Merchant
        waitAndClick("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*");
        waitAndClick("//span[contains(text(),'Add Merchant')]");
        takeScreenshot();

        // ✅ Fill Basic Information
        fillInputByLabel(" Business Name* ", "Aditya");
        fillInputByLabel(" Contact Name* ", "Konda Viewer");
        fillInputByLabel(" Mobile* ", "9876543210");
        takeScreenshot();
        clickNext();

        // ✅ Wait for Address Info Page
        waitForHeading("Address Information");

        // ✅ Fill Address Information
        fillInputByLabel(" Address* ", "Aditya Address");
        fillInputByLabel(" City* ", "Mumbai");
        fillInputByLabel(" Pincode* ", "421302");

        // ✅ Select State (Maharashtra)
        Select stateDropdown = new Select(driver.findElement(By.id("Labell1pNamel1p")));
        stateDropdown.selectByVisibleText("MH - Maharashtra");
        takeScreenshot();
        clickNext();

        // ✅ Wait for Payment Details Page
        waitForHeading("Payment Details");
        clickNext();

        // ✅ Wait for Group Assignment Page
        waitForHeading("Group Assignment");
        clickNext();

        // ✅ Wait for Review & Submit Page
        waitForHeading("Review & Submit");
        waitAndClick("//button[normalize-space(text())='Submit']");
        takeScreenshot();
    }

    private void fillInputByLabel(String labelText, String value) {
        WebElement label = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//label[contains(text(),'" + labelText + "')]")));
        String forAttr = label.getAttribute("for");
        WebElement input = driver.findElement(By.id(forAttr));
        input.clear();
        input.sendKeys(value);
    }

    private void waitAndClick(String xpath) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();
    }

    private void clickNext() throws IOException {
        WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space(text())='Next']")));
        nextBtn.click();
        takeScreenshot();
    }

    private void waitForHeading(String headingText) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[normalize-space(text())='" + headingText + "']")));
    }

    private void takeScreenshot() throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File("AddMerchant_Step_" + screenshotCounter.getAndIncrement() + ".png");
        FileUtils.copyFile(src, dest);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
