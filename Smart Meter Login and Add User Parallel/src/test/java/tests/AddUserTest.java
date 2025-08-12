package tests;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import utils.ExcelUtils;
import static utils.ExcelUtils.readTestData;

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
        loginPage.enterEmail("aditya.konda@cwdin.com");
        loginPage.enterPassword("Aditya@2");
        loginPage.clickLogin();
        Thread.sleep(500);
        takeScreenshot("screenshots/Valid Login", "Login_Success_Add_User_Attempt ✅");

        List<Map<String, String>> testData = readTestData();
        int rowCounter = 1;

        for (Map<String, String> row : testData) {
            String srNo = row.get("Sr No");
            String firstName = row.get("FirstName");
            String lastName = row.get("LastName");
            String role = row.get("Role");
            String emailInput = row.get("Email");
            String phoneNumber = row.get("PhoneNumber");
            String password = row.get("Password");

            String timestamp = timestamp();
            String email = (emailInput == null || emailInput.trim().isEmpty())
                    ? "aditya_" + role.toLowerCase() + "_" + timestamp + "@cwd.com"
                    : emailInput;

            String screenshotName = role + "_TC_" + timestamp;
            String testCaseFolder = "screenshots/Add User/TC_" + rowCounter;

            String status = "FAIL";
            String message = "";

            try {
                navigateToAddUser(testCaseFolder);

                fillInputByLabel("First Name*", firstName);
                fillInputByLabel("Last Name*", lastName);
                fillInputByLabel("Email*", email);
                fillPhoneNumber(phoneNumber);

                String finalPassword;
                if (password != null && !password.trim().isEmpty()) {
                    fillInputByLabel("Password*", password);
                    finalPassword = password;
                } else {
                    driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
                    Thread.sleep(500);

                    WebElement pwdField = getInputByLabel("Password*");
                    finalPassword = pwdField.getAttribute("value"); // get auto-generated value
                }

                takeScreenshot(testCaseFolder, screenshotName + "_Form");

                WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")));
                nextBtn.click();
                Thread.sleep(500);

                driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
                Thread.sleep(500);

                try {
                    WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
                    if (error.isDisplayed()) {
                        message = "Username already exists";
                        takeScreenshot(testCaseFolder, screenshotName + "_Error ❌");
                    }
                } catch (NoSuchElementException e) {
                    status = "PASS";
                    takeScreenshot(testCaseFolder, screenshotName + "_Success ✅");
                }
                password = finalPassword;

            } catch (IOException | InterruptedException e) {
                message = "Exception: " + e.getMessage();
                takeScreenshot(testCaseFolder, screenshotName + "_Exception ❌");
            }

            Map<String, String> resultRow = new HashMap<>();
            resultRow.put("Sr No", srNo);
            resultRow.put("FirstName", firstName);
            resultRow.put("LastName", lastName);
            resultRow.put("Role", role);
            resultRow.put("Email", email);
            resultRow.put("PhoneNumber", phoneNumber);
            resultRow.put("Password", password);  // Final password used (input or auto-generated)
            resultRow.put("Status", status);
            resultRow.put("Screenshot", testCaseFolder + "/" + screenshotName + ".png");
            resultRow.put("Timestamp", timestamp);
            resultRow.put("Message", message);

            ExcelUtils.writeResult(rowCounter++, resultRow);
        }
    }

    private void navigateToAddUser(String screenshotFolder) throws InterruptedException, IOException {
        WebElement entityManagement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(text(),'Entity Management')]")));
        wait.until(ExpectedConditions.elementToBeClickable(entityManagement)).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "Nav_EntityManagement 🖱️");

        driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "Nav_Users 🖱️");

        WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
        menuDialpad.click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "DialpadClicked 🖱️");

        driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "AddUserClicked 🖱️");
    }

    private void fillInputByLabel(String labelText, String value) {
        WebElement input = getInputByLabel(labelText);
        input.clear();
        input.sendKeys(value);
    }

    private WebElement getInputByLabel(String labelText) {
        WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
        String forAttr = label.getAttribute("for");
        return driver.findElement(By.id(forAttr));
    }

    private void fillPhoneNumber(String phone) {
        WebElement phoneInput = driver.findElement(By.xpath("//input[@type='tel' and @formcontrolname='phoneNumber']"));
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }

    private void takeScreenshot(String folderPath, String filename) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dir = new File(folderPath);
        if (!dir.exists()) dir.mkdirs();
        File dest = new File(dir, filename + "_" + screenshotCounter++ + ".png");
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
}




















































































































































































































































// package tests;

// import java.io.File;
// import java.io.IOException;
// import java.time.Duration;
// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.apache.commons.io.FileUtils;
// import org.openqa.selenium.By;
// import org.openqa.selenium.NoSuchElementException;
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
// import utils.ExcelUtils;
// import static utils.ExcelUtils.readTestData;

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
//     public void addUsersWithAllRoles() throws InterruptedException, IOException {
//         loginPage.goTo();
//         loginPage.enterEmail("omkar.chavan@cwdin.com");
//         loginPage.enterPassword("Omkar@1234");
//         loginPage.clickLogin();
//         Thread.sleep(3000);

//         // Login screenshot
//         takeScreenshot("screenshots/Valid Login", "Login_Success");

//         List<Map<String, String>> testData = readTestData();
//         int rowCounter = 1;

//         for (Map<String, String> row : testData) {
//             String srNo = row.get("Sr No");
//             String firstName = row.get("FirstName");
//             String lastName = row.get("LastName");
//             String role = row.get("Role");
//             String emailInput = row.get("Email");
//             String phoneNumber = row.get("PhoneNumber");

//             String timestamp = timestamp();
//             String email = (emailInput == null || emailInput.trim().isEmpty())
//                     ? "aditya_" + role.toLowerCase() + "_" + timestamp + "@cwd.com"
//                     : emailInput;

//             String screenshotName = role + "_Attempt_" + timestamp;
//             String testCaseFolder = "screenshots/Add User/TC_" + rowCounter;

//             String status = "FAIL";
//             String message = "";

//             try {
//                 navigateToAddUser(testCaseFolder);

//                 fillInputByLabel("First Name*", firstName);
//                 fillInputByLabel("Last Name*", lastName);
//                 fillInputByLabel("Email*", email);
//                 fillPhoneNumber(phoneNumber);
//                 takeScreenshot(testCaseFolder, screenshotName + "_Form");

//                 driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
//                 Thread.sleep(500);
//                 WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                         By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")));
//                 nextBtn.click();
//                 Thread.sleep(500);
//                 driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
//                 Thread.sleep(500);
//                 driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
//                 Thread.sleep(500);
//                 driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
//                 Thread.sleep(1000);
//                 driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
//                 Thread.sleep(2000);

//                 try {
//                     WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
//                     if (error.isDisplayed()) {
//                         message = "Username already exists";
//                         takeScreenshot(testCaseFolder, screenshotName + "_Error");
//                     }
//                 } catch (NoSuchElementException e) {
//                     status = "PASS";
//                     takeScreenshot(testCaseFolder, screenshotName + "_Success");
//                 }

//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(testCaseFolder, screenshotName + "_Exception");
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("FirstName", firstName);
//             resultRow.put("LastName", lastName);
//             resultRow.put("Role", role);
//             resultRow.put("Email", email);
//             resultRow.put("PhoneNumber", phoneNumber);
//             resultRow.put("Status", status);
//             resultRow.put("Screenshot", testCaseFolder + "/" + screenshotName + ".png");
//             resultRow.put("Timestamp", timestamp);
//             resultRow.put("Message", message);

//             ExcelUtils.writeResult(rowCounter++, resultRow);
//         }
//     }

//     private void navigateToAddUser(String screenshotFolder) throws InterruptedException, IOException {
//         WebElement entityManagement = wait.until(ExpectedConditions.visibilityOfElementLocated(
//                 By.xpath("//a[contains(text(),'Entity Management')]")));
//         wait.until(ExpectedConditions.elementToBeClickable(entityManagement)).click();

//         Thread.sleep(500);
//         takeScreenshot(screenshotFolder, "Nav_EntityManagement");

//         // driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
//         // Thread.sleep(500);
//         // takeScreenshot(screenshotFolder, "Nav_World");

//         driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
//         Thread.sleep(500);
//         takeScreenshot(screenshotFolder, "Nav_Users");

//         WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
//                 By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
//         menuDialpad.click();
//         Thread.sleep(500);
//         takeScreenshot(screenshotFolder, "DialpadClicked");

//         driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
//         Thread.sleep(500);
//         takeScreenshot(screenshotFolder, "AddUserClicked");
//     }

//     private void fillInputByLabel(String labelText, String value) {
//         WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
//         String forAttr = label.getAttribute("for");
//         WebElement input = driver.findElement(By.id(forAttr));
//         input.clear();
//         input.sendKeys(value);
//     }

//     private void fillPhoneNumber(String phone) {
//         WebElement phoneInput = driver.findElement(By.xpath("//input[@type='tel' and @formcontrolname='phoneNumber']"));
//         phoneInput.clear();
//         phoneInput.sendKeys(phone);
//     }

//     private void takeScreenshot(String folderPath, String filename) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dir = new File(folderPath);
//         if (!dir.exists()) dir.mkdirs();
//         File dest = new File(dir, filename + "_" + screenshotCounter++ + ".png");
//         FileUtils.copyFile(src, dest);
//     }

//     private String timestamp() {
//         return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
//     }

//     @AfterClass
//     public void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }
// }