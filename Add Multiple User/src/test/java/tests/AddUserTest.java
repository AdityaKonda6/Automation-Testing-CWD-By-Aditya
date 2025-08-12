package tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.LoginPage;

public class AddUserTest {
    WebDriver driver;
    LoginPage loginPage;
    WebDriverWait wait;
    int screenshotCounter = 1;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        loginPage = new LoginPage(driver);
    }

    @Test
    public void addUsersWithAllRoles() throws InterruptedException, IOException {
        loginPage.goTo();
        loginPage.enterEmail("adityakonda@cwdin.com");
        loginPage.enterPassword("Aditya@123");
        loginPage.clickLogin();
        Thread.sleep(3000);
        takeScreenshot("Login");

        String[] roles = {
            "SuperAdmin", "Admin", "Viewer", "Production", "Tester", "MerchantViewer"
        };

        for (String role : roles) {
            navigateToAddUser();

            String firstName = "Aditya";
            String lastName = "Konda " + role;
            String uniqueEmail = "adi_" + role.toLowerCase() + "_" + timestamp() + "@konda.com";

            fillInputByLabel("First Name*", firstName);
            fillInputByLabel("Last Name*", lastName);
            fillInputByLabel("Email*", uniqueEmail);
            takeScreenshot(role + "_FormFilled");

            driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
            Thread.sleep(500);
            takeScreenshot(role + "_PasswordGenerated");

            WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")));
            nextBtn.click();
            Thread.sleep(500);
            takeScreenshot(role + "_AfterBasicInfo");

            // Select role dynamically
            driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
            Thread.sleep(500);
            driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
            Thread.sleep(500);
            takeScreenshot(role + "_RoleSelected");

            driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
            Thread.sleep(1000);
            takeScreenshot(role + "_AfterRole");

            driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
            Thread.sleep(2000);
            takeScreenshot(role + "_Submitted");

            // Check if "username already exists" error shows up
            boolean retry = false;
            try {
                WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
                if (error.isDisplayed()) {
                    takeScreenshot(role + "_ErrorExists");
                    retry = true;
                }
            } catch (NoSuchElementException e) {
                // Do nothing
            }

            // Retry if duplicate
            if (retry) {
                navigateToAddUser();

                String retryEmail = "retry_" + role.toLowerCase() + "_" + timestamp() + "@example.com";
                fillInputByLabel("First Name*", firstName);
                fillInputByLabel("Last Name*", lastName + "_retry");
                fillInputByLabel("Email*", retryEmail);
                takeScreenshot(role + "_RetryFormFilled");

                driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
                Thread.sleep(500);
                takeScreenshot(role + "_RetryPassword");

                driver.findElement(By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")).click();
                Thread.sleep(500);

                driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
                Thread.sleep(500);
                takeScreenshot(role + "_RetryRoleSelected");

                driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
                Thread.sleep(1000);
                takeScreenshot(role + "_RetryAfterRole");

                driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
                Thread.sleep(2000);
                takeScreenshot(role + "_RetrySubmitted");
            }
        }
    }

    private void navigateToAddUser() throws InterruptedException, IOException {
        WebElement entityManagement = wait.until(ExpectedConditions.elementToBeClickable(
        By.xpath("//a[normalize-space(text())='Entity Management']")));
        entityManagement.click();
        Thread.sleep(500);
        takeScreenshot("Nav_EntityManagement");

        driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
        Thread.sleep(500);
        takeScreenshot("Nav_World");

        driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
        Thread.sleep(500);
        takeScreenshot("Nav_Users");

        WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
        menuDialpad.click();
        Thread.sleep(500);
        takeScreenshot("DialpadClicked");

        driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
        Thread.sleep(500);
        takeScreenshot("AddUserClicked");
    }

    private void fillInputByLabel(String labelText, String value) {
        WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
        String forAttr = label.getAttribute("for");
        WebElement input = driver.findElement(By.id(forAttr));
        input.clear();
        input.sendKeys(value);
    }

    private void takeScreenshot(String filename) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File("screenshots/AddUser_" + filename + "_" + screenshotCounter++ + ".png");
        FileUtils.copyFile(src, dest);
    }

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private By path(String anormalizespacetextEntity_Management) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}







































































// package tests;

// import java.io.File;
// import java.io.IOException;
// import java.time.Duration;

// import org.apache.commons.io.FileUtils;
// import org.openqa.selenium.By;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import io.github.bonigarcia.wdm.WebDriverManager;
// import pages.LoginPage;

// public class AddUserTest {
//     WebDriver driver;
//     LoginPage loginPage;
//     WebDriverWait wait;
//     int screenshotCounter = 1;

//     @BeforeClass
//     public void setup() {
//         WebDriverManager.chromedriver().setup();
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//         loginPage = new LoginPage(driver);
//     }

//     @Test
//     public void addNewUserFlow() throws InterruptedException, IOException {
//         // ✅ Step 1: Login
//         loginPage.goTo();
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(3000);
//         takeScreenshot();

//         // ✅ Step 2: Navigate to Entity Management > World > Users
//         driver.findElement(By.linkText("Entity Management")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 3: Click Menu Dialpad (SVG)
//         WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
//             By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")
//         ));
//         menuDialpad.click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 4: Click Add User
//         driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 5: Fill First Name, Last Name, Email (Based on label 'for' attribute)
//         fillInputByLabel("First Name*", "John");
//         fillInputByLabel("Last Name*", "Doe");
//         fillInputByLabel("Email*", "adityakonda1@example.com");
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 6: Click Auto-Generate Password
//         driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 7: Click Next
//         WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
//             By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")
//         ));
//         nextBtn.click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 8: Select Role (Viewer)
//         driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
//         Thread.sleep(500);
//         driver.findElement(By.xpath("//div[contains(text(),'Viewer')]")).click();
//         Thread.sleep(500);
//         takeScreenshot();

//         // ✅ Step 9: Click Next on Role
//         driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
//         Thread.sleep(1000);
//         takeScreenshot();

//         // ✅ Step 10: Submit on Review Page
//         driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
//         Thread.sleep(2000);
//         takeScreenshot();
//     }

//     /**
//      * Utility method to fill input fields by their label text
//      */
//     private void fillInputByLabel(String labelText, String value) {
//         WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
//         String forAttr = label.getAttribute("for");
//         WebElement input = driver.findElement(By.id(forAttr));
//         input.clear();
//         input.sendKeys(value);
//     }

//     /**
//      * Screenshot utility
//      */
//     private void takeScreenshot() throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File("screenshots/AddUserStep" + screenshotCounter++ + ".png");
//         FileUtils.copyFile(src, dest);
//     }

//     @AfterClass
//     public void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }
// }































































































































// // package tests;

// // import java.time.Duration;

// // import org.openqa.selenium.By;
// // import org.openqa.selenium.WebDriver;
// // import org.openqa.selenium.WebElement;
// // import org.openqa.selenium.chrome.ChromeDriver;
// // import org.openqa.selenium.support.ui.ExpectedConditions;
// // import org.openqa.selenium.support.ui.WebDriverWait;
// // import org.testng.annotations.AfterClass;
// // import org.testng.annotations.BeforeClass;
// // import org.testng.annotations.Test;

// // import io.github.bonigarcia.wdm.WebDriverManager;
// // import pages.LoginPage;

// // public class AddUserTest {
// //     WebDriver driver;
// //     LoginPage loginPage;
// //     WebDriverWait wait;

// //     @BeforeClass
// //     public void setup() {
// //         WebDriverManager.chromedriver().setup();
// //         driver = new ChromeDriver();
// //         driver.manage().window().maximize();
// //         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
// //         loginPage = new LoginPage(driver);
// //     }

// //     @Test
// //     public void addNewUserFlow() throws InterruptedException {
// //         // ✅ Step 1: Login
// //         loginPage.goTo();
// //         loginPage.enterEmail("amal.a@cwdin.com");
// //         loginPage.enterPassword("Pass@123");
// //         loginPage.clickLogin();
// //         Thread.sleep(3000); // Wait for login

// //         // ✅ Step 2: Navigate to Entity Management > World > Users
// //         driver.findElement(By.linkText("Entity Management")).click();
// //         Thread.sleep(1000);
// //         driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
// //         Thread.sleep(1000);
// //         driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
// //         Thread.sleep(1000);

// //         // ✅ Step 3: Click Menu Dialpad (SVG)
// //         WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
// //             By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")
// //         ));
// //         menuDialpad.click();
// //         Thread.sleep(1000);

// //         // ✅ Step 4: Click Add User
// //         driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
// //         Thread.sleep(1000);

// //         // ✅ Step 5: Fill First Name, Last Name, Email (Based on label 'for' attribute)
// //         fillInputByLabel("First Name*", "John");
// //         fillInputByLabel("Last Name*", "Doe");
// //         fillInputByLabel("Email*", "john.doe@example.com");
// //         Thread.sleep(1000);

// //         // ✅ Step 6: Click Auto-Generate Password
// //         driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
// //         Thread.sleep(1000);

// //         // ✅ Step 7: Click Next
// //         WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
// //             By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")
// //         ));
// //         nextBtn.click();
// //         Thread.sleep(1000);

// //         // ✅ Step 8: Select Role (Viewer)
// //         driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
// //         Thread.sleep(500);
// //         driver.findElement(By.xpath("//div[contains(text(),'Viewer')]")).click();
// //         Thread.sleep(500);

// //         // ✅ Step 9: Click Next on Role
// //         driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
// //         Thread.sleep(1000);

// //         // ✅ Step 10: Submit on Review Page
// //         driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
// //         Thread.sleep(2000);
// //     }

// //     /**
// //      * Utility method to fill input fields by their label text
// //      */
// //     private void fillInputByLabel(String labelText, String value) {
// //         WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
// //         String forAttr = label.getAttribute("for");
// //         WebElement input = driver.findElement(By.id(forAttr));
// //         input.clear();
// //         input.sendKeys(value);
// //     }

// //     @AfterClass
// //     public void tearDown() {
// //         if (driver != null) {
// //             driver.quit();
// //         }
// //     }
// // }
