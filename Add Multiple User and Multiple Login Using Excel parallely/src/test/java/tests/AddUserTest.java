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
        loginPage.enterEmail("adityakonda@cwdin.com");
        loginPage.enterPassword("Aditya@123");
        loginPage.clickLogin();
        Thread.sleep(3000);

        // Login screenshot
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

            String timestamp = timestamp();
            String email = (emailInput == null || emailInput.trim().isEmpty())
                    ? "aditya_" + role.toLowerCase() + "_" + timestamp + "@cwd.com"
                    : emailInput;

            String screenshotName = role + "_Attempt_" + timestamp;
            String testCaseFolder = "screenshots/Add User/TC_" + rowCounter;

            String status = "FAIL";
            String message = "";

            try {
                navigateToAddUser(testCaseFolder);

                fillInputByLabel("First Name*", firstName);
                fillInputByLabel("Last Name*", lastName);
                fillInputByLabel("Email*", email);
                fillPhoneNumber(phoneNumber);
                takeScreenshot(testCaseFolder, screenshotName + "_Form");

                driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
                Thread.sleep(500);
                WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")));
                nextBtn.click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
                Thread.sleep(500);
                driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
                Thread.sleep(1000);
                driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
                Thread.sleep(2000);

                try {
                    WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
                    if (error.isDisplayed()) {
                        message = "Username already exists";
                        takeScreenshot(testCaseFolder, screenshotName + "_Error");
                    }
                } catch (NoSuchElementException e) {
                    status = "PASS";
                    takeScreenshot(testCaseFolder, screenshotName + "_Success");
                }

            } catch (Exception e) {
                message = "Exception: " + e.getMessage();
                takeScreenshot(testCaseFolder, screenshotName + "_Exception");
            }

            Map<String, String> resultRow = new HashMap<>();
            resultRow.put("Sr No", srNo);
            resultRow.put("FirstName", firstName);
            resultRow.put("LastName", lastName);
            resultRow.put("Role", role);
            resultRow.put("Email", email);
            resultRow.put("PhoneNumber", phoneNumber);
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
        takeScreenshot(screenshotFolder, "Nav_EntityManagement");

        driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "Nav_World");

        driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "Nav_Users");

        WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
        menuDialpad.click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "DialpadClicked");

        driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
        Thread.sleep(500);
        takeScreenshot(screenshotFolder, "AddUserClicked");
    }

    private void fillInputByLabel(String labelText, String value) {
        WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
        String forAttr = label.getAttribute("for");
        WebElement input = driver.findElement(By.id(forAttr));
        input.clear();
        input.sendKeys(value);
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
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(3000);
//         takeScreenshot("Login");

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
//             String status = "FAIL";
//             String message = "";

//             try {
//                 navigateToAddUser();

//                 fillInputByLabel("First Name*", firstName);
//                 fillInputByLabel("Last Name*", lastName);
//                 fillInputByLabel("Email*", email);
//                 fillPhoneNumber(phoneNumber);  // new method
//                 takeScreenshot(screenshotName + "_Form");

//                 driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
//                 Thread.sleep(500);
//                 WebElement nextBtn = wait.until(ExpectedConditions.elementToBeClickable(
//                 By.xpath("//button[normalize-space(text())='Next' and not(@disabled)]")));
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

//                 // Check for error
//                 try {
//                     WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
//                     if (error.isDisplayed()) {
//                         message = "Username already exists";
//                         takeScreenshot(screenshotName + "_Error");
//                     }
//                 } catch (NoSuchElementException e) {
//                     status = "PASS";
//                     takeScreenshot(screenshotName + "_Success");
//                 }

//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(screenshotName + "_Exception");
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("FirstName", firstName);
//             resultRow.put("LastName", lastName);
//             resultRow.put("Role", role);
//             resultRow.put("Email", email);
//             resultRow.put("PhoneNumber", phoneNumber);  // NEW
//             resultRow.put("Status", status);
//             resultRow.put("Screenshot", "screenshots/AddUser_" + screenshotName + ".png");
//             resultRow.put("Timestamp", timestamp);
//             resultRow.put("Message", message);

//             ExcelUtils.writeResult(rowCounter++, resultRow);
//         }
//     }

//     private void navigateToAddUser() throws InterruptedException, IOException {
//         WebElement entityManagement = wait.until(ExpectedConditions.visibilityOfElementLocated(
//     By.xpath("//a[contains(text(),'Entity Management')]")));
// wait.until(ExpectedConditions.elementToBeClickable(entityManagement)).click();

//         Thread.sleep(500);
//         takeScreenshot("Nav_EntityManagement");

//         driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
//         Thread.sleep(500);
//         takeScreenshot("Nav_World");

//         driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
//         Thread.sleep(500);
//         takeScreenshot("Nav_Users");

//         WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
//                 By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
//         menuDialpad.click();
//         Thread.sleep(500);
//         takeScreenshot("DialpadClicked");

//         driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
//         Thread.sleep(500);
//         takeScreenshot("AddUserClicked");
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

//     private void takeScreenshot(String filename) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File("screenshots/AddUser_" + filename + "_" + screenshotCounter++ + ".png");
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
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(3000);
//         takeScreenshot("Login");

//         List<Map<String, String>> testData;
//         testData = readTestData();
// int rowCounter = 1;

// for (Map<String, String> row : testData) {
//     String srNo = row.get("Sr No");
//     String firstName = row.get("FirstName");
//     String lastName = row.get("LastName");
//     String role = row.get("Role");

//     String timestamp = timestamp();
//     String email = "aditya_" + role.toLowerCase() + "_" + timestamp + "@cwd.com";
//     String screenshotName = role + "_Attempt_" + timestamp;
//     String status = "FAIL";
//     String message = "";
//     String emailInput = row.get("Email");
//     String phoneNumber = row.get("PhoneNumber");
    

// // Auto-generate if email not provided



//     try {
//         navigateToAddUser();
//         fillInputByLabel("First Name*", firstName);
//         fillInputByLabel("Last Name*", lastName);
//         fillInputByLabel("Email*", email);
//         takeScreenshot(screenshotName + "_Form");

//         driver.findElement(By.xpath("//span[contains(text(),'Auto-generate secure password')]")).click();
//         Thread.sleep(500);
//         driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
//         Thread.sleep(500);
//         driver.findElement(By.xpath("//div[contains(@class,'cursor-pointer') and .//span[text()='Select Role']]")).click();
//         Thread.sleep(500);
//         driver.findElement(By.xpath("//div[contains(text(),'" + role + "')]")).click();
//         Thread.sleep(500);
//         driver.findElement(By.xpath("//button[normalize-space(text())='Next']")).click();
//         Thread.sleep(1000);
//         driver.findElement(By.xpath("//button[normalize-space(text())='Submit']")).click();
//         Thread.sleep(2000);

//         // Check for error
//         try {
//             WebElement error = driver.findElement(By.xpath("//*[contains(text(),'already exists')]"));
//             if (error.isDisplayed()) {
//                 message = "Username already exists";
//                 takeScreenshot(screenshotName + "_Error");
//             }
//         } catch (NoSuchElementException e) {
//             status = "PASS";
//             takeScreenshot(screenshotName + "_Success");
//         }

//     } catch (Exception e) {
//         message = "Exception: " + e.getMessage();
//         takeScreenshot(screenshotName + "_Exception");
//     }

//     Map<String, String> resultRow = new HashMap<>();
//     resultRow.put("Sr No", srNo);
//     resultRow.put("FirstName", firstName);
//     resultRow.put("LastName", lastName);
//     resultRow.put("Role", role);
//     resultRow.put("Email", email);
//     resultRow.put("Status", status);
//     resultRow.put("Screenshot", "screenshots/AddUser_" + screenshotName + ".png");
//     resultRow.put("Timestamp", timestamp);
//     resultRow.put("Message", message);

//     ExcelUtils.writeResult(rowCounter++, resultRow);
// }




//     }

//     private void navigateToAddUser() throws InterruptedException, IOException {
//     WebElement entityManagement = wait.until(ExpectedConditions.elementToBeClickable(
//         By.xpath("//a[normalize-space(text())='Entity Management']")));
//     entityManagement.click();
//     Thread.sleep(500);
//     takeScreenshot("Nav_EntityManagement");

//         driver.findElement(By.xpath("//span[contains(text(),'World')]")).click();
//         Thread.sleep(500);
//         takeScreenshot("Nav_World");

//         driver.findElement(By.xpath("//span[contains(text(),'Users')]")).click();
//         Thread.sleep(500);
//         takeScreenshot("Nav_Users");

//         WebElement menuDialpad = wait.until(ExpectedConditions.elementToBeClickable(
//             By.xpath("//*[local-name()='svg']/*[local-name()='title' and text()='menu-dialpad']/parent::*")));
//         menuDialpad.click();
//         Thread.sleep(500);
//         takeScreenshot("DialpadClicked");

//         driver.findElement(By.xpath("//button[.//span[text()='Add User']]")).click();
//         Thread.sleep(500);
//         takeScreenshot("AddUserClicked");
//     }

//     private void fillInputByLabel(String labelText, String value) {
//         WebElement label = driver.findElement(By.xpath("//label[contains(text(),'" + labelText + "')]"));
//         String forAttr = label.getAttribute("for");
//         WebElement input = driver.findElement(By.id(forAttr));
//         input.clear();
//         input.sendKeys(value);
//     }

//     private void takeScreenshot(String filename) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File("screenshots/AddUser_" + filename + "_" + screenshotCounter++ + ".png");
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
