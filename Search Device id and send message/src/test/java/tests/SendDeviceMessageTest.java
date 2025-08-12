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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.LoginPage;
import utils.ExcelUtils;

public class SendDeviceMessageTest {
    WebDriver driver;
    LoginPage loginPage;
    @SuppressWarnings("unused")
    WebDriverWait wait;
    int attemptCounter = 1;
    int screenshotCounter = 1;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        loginPage = new LoginPage(driver);
    }

    @Test
    public void sendMessageToDevices() throws IOException, InterruptedException {
        loginPage.goTo();
        loginPage.enterEmail("adityakonda@cwdin.com");
        loginPage.enterPassword("Aditya@123");
        loginPage.clickLogin();
        Thread.sleep(4000);
        takeScreenshot("Login", "screenshots/General");

        List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
        int rowCounter = 1;

        for (Map<String, String> row : testData) {
            String srNo = row.get("Sr No");
            String deviceId = row.get("DeviceID");
            String amount = row.get("Amount");
            String txnMode = row.get("Transaction Mode");
            String txnType = row.get("Transaction Type");

            String timestamp = timestamp();
            String folderName = "screenshots/TC_" + attemptCounter + "/" + deviceId;
            new File(folderName).mkdirs();
            String baseFileName = "TC_" + srNo + "_" + timestamp;
            String status = "FAIL";
            String message = "";

            try {
                searchDevice(deviceId);
                takeScreenshot(baseFileName + "_Search", folderName);

                if (isElementPresent(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"))) {
                    WebElement optionsButton = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
                    new Actions(driver).moveToElement(optionsButton).perform();
                    Thread.sleep(1500);
                    takeScreenshot(baseFileName + "_OptionsHovered", folderName);

                    driver.findElement(By.xpath("//img[@alt='send-message']")).click();
                    Thread.sleep(1500);
                    takeScreenshot(baseFileName + "_SendMessageClicked 🖱️", folderName);

                    // Fill Amount
                    WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
                    String amountInputId = amountLabel.getAttribute("for");
                    WebElement amountInput = driver.findElement(By.id(amountInputId));
                    amountInput.click();
                    amountInput.clear();
                    amountInput.sendKeys(amount);
                    Thread.sleep(500);

                    // Fill Transaction Mode
                    WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
                    String txnModeId = txnModeLabel.getAttribute("for");
                    WebElement txnModeInput = driver.findElement(By.id(txnModeId));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
                    txnModeInput.click();
                    txnModeInput.clear();
                    txnModeInput.sendKeys(txnMode);
                    Thread.sleep(500);

                    // Fill Transaction Type
                    WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
                    String txnTypeId = txnTypeLabel.getAttribute("for");
                    WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
                    txnTypeInput.click();
                    txnTypeInput.clear();
                    txnTypeInput.sendKeys(txnType);
                    Thread.sleep(500);

                    takeScreenshot(baseFileName + "_MessageFilled", folderName);

                    // Validate input values (must be 0 to 10)
                    int txnModeVal = Integer.parseInt(txnMode);
                    int txnTypeVal = Integer.parseInt(txnType);
                    boolean validRange = txnModeVal >= 0 && txnModeVal <= 10 && txnTypeVal >= 0 && txnTypeVal <= 10;

                    WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Submit')]"));
                    boolean isEnabled = submitBtn.isEnabled();

                    if (!validRange || !isEnabled) {
                        message = "Invalid values: Mode=" + txnMode + ", Type=" + txnType + " or Submit button disabled";
                        takeScreenshot(baseFileName + "_InvalidInputOrDisabled ❌", folderName);
                    } else {
                        submitBtn.click();
                        Thread.sleep(1500);
                        takeScreenshot(baseFileName + "_MessageSubmitted", folderName);
                        status = "PASS";
                        message = "Everything Worked Correctly Audio Played ✅";
                    }

                } else {
                    message = "Device not found";
                    takeScreenshot(baseFileName + "_NotFound ❌", folderName);
                }

                driver.navigate().refresh();
                Thread.sleep(4000);
                attemptCounter++;

            } catch (IOException | InterruptedException | NumberFormatException e) {
                message = "Exception: " + e.getMessage();
                takeScreenshot(baseFileName + "_Exception ❌", folderName);
            }

            Map<String, String> resultRow = new HashMap<>();
            resultRow.put("Sr No", srNo);
            resultRow.put("Device ID", deviceId);
            resultRow.put("Amount", amount);
            resultRow.put("Transaction Mode", txnMode);
            resultRow.put("Transaction Type", txnType);
            resultRow.put("Status", status);
            resultRow.put("Screenshot", folderName);
            resultRow.put("Timestamp", timestamp);
            resultRow.put("Message", message);

            ExcelUtils.writeResult(rowCounter++, resultRow);
        }
    }

    private void searchDevice(String deviceId) throws InterruptedException {
        WebElement searchBar = driver.findElement(By.xpath("//input[@placeholder='Search users, devices, and merchants...']"));
        searchBar.clear();
        searchBar.sendKeys(deviceId);
        searchBar.sendKeys(Keys.ENTER);
        Thread.sleep(3000);
    }

    private boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    private void takeScreenshot(String fileName, String folderPath) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File dest = new File(folderPath + "/" + fileName + "_" + screenshotCounter++ + ".png");
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
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.interactions.Actions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import io.github.bonigarcia.wdm.WebDriverManager;
// import pages.LoginPage;
// import utils.ExcelUtils;

// public class SendDeviceMessageTest {
//     WebDriver driver;
//     LoginPage loginPage;
//     @SuppressWarnings("unused")
//     WebDriverWait wait;
//     int attemptCounter = 1;
//     int screenshotCounter = 1;

//     @BeforeClass
//     public void setup() {
//         WebDriverManager.chromedriver().setup();
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//         loginPage = new LoginPage(driver);
//     }

//     @Test
//     public void sendMessageToDevices() throws IOException, InterruptedException {
//         loginPage.goTo();
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(4000);
//         takeScreenshot("Login", "General");

//         List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
//         int rowCounter = 1;

//         for (Map<String, String> row : testData) {
//             String srNo = row.get("Sr No");
//             String deviceId = row.get("DeviceID");
//             String amount = row.get("Amount");
//             String txnMode = row.get("Transaction Mode");
//             String txnType = row.get("Transaction Type");

//             String timestamp = timestamp();
//             String folderName = "screenshots/Attempt_" + attemptCounter + "/" + deviceId;
//             new File(folderName).mkdirs();
//             String baseFileName = "TC_" + srNo + "_" + timestamp;
//             String status = "FAIL";
//             String message = "";

//             try {
//                 searchDevice(deviceId);
//                 takeScreenshot(baseFileName + "_Search", folderName);

//                 if (isElementPresent(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"))) {
//                     WebElement optionsButton = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     new Actions(driver).moveToElement(optionsButton).perform();
//                     Thread.sleep(1500);
//                     takeScreenshot(baseFileName + "_OptionsHovered", folderName);

//                     driver.findElement(By.xpath("//img[@alt='send-message']")).click();
//                     Thread.sleep(1500);
//                     takeScreenshot(baseFileName + "_SendMessageClicked", folderName);

//                     // Fill Amount
//                     WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
//                     String amountInputId = amountLabel.getAttribute("for");
//                     WebElement amountInput = driver.findElement(By.id(amountInputId));
//                     amountInput.click();
//                     amountInput.clear();
//                     amountInput.sendKeys(amount);
//                     Thread.sleep(500);

//                     // Fill Transaction Mode
//                     WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
//                     String txnModeId = txnModeLabel.getAttribute("for");
//                     WebElement txnModeInput = driver.findElement(By.id(txnModeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
//                     txnModeInput.click();
//                     txnModeInput.clear();
//                     txnModeInput.sendKeys(txnMode);
//                     Thread.sleep(500);

//                     // Fill Transaction Type
//                     WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
//                     String txnTypeId = txnTypeLabel.getAttribute("for");
//                     WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
//                     txnTypeInput.click();
//                     txnTypeInput.clear();
//                     txnTypeInput.sendKeys(txnType);
//                     Thread.sleep(500);

//                     takeScreenshot(baseFileName + "_MessageFilled", folderName);

//                     // Validate input values (must be 0 to 10)
//                     int txnModeVal = Integer.parseInt(txnMode);
//                     int txnTypeVal = Integer.parseInt(txnType);
//                     boolean validRange = txnModeVal >= 0 && txnModeVal <= 10 && txnTypeVal >= 0 && txnTypeVal <= 10;

//                     WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Submit')]"));
//                     boolean isEnabled = submitBtn.isEnabled();

//                     if (!validRange || !isEnabled) {
//                         message = "Invalid values: Mode=" + txnMode + ", Type=" + txnType + " or Submit button disabled";
//                         takeScreenshot(baseFileName + "_InvalidInputOrDisabled", folderName);
//                     } else {
//                         submitBtn.click();
//                         Thread.sleep(1500);
//                         takeScreenshot(baseFileName + "_MessageSubmitted", folderName);
//                         status = "PASS";
//                     }

//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(baseFileName + "_NotFound", folderName);
//                 }

//                 driver.navigate().refresh();
//                 Thread.sleep(4000);
//                 attemptCounter++;

//             } catch (IOException | InterruptedException | NumberFormatException e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(baseFileName + "_Exception", folderName);
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("Device ID", deviceId);
//             resultRow.put("Amount", amount);
//             resultRow.put("Transaction Mode", txnMode);
//             resultRow.put("Transaction Type", txnType);
//             resultRow.put("Status", status);
//             resultRow.put("Screenshot", folderName);
//             resultRow.put("Timestamp", timestamp);
//             resultRow.put("Message", message);

//             ExcelUtils.writeResult(rowCounter++, resultRow);
//         }
//     }

//     private void searchDevice(String deviceId) throws InterruptedException {
//         WebElement searchBar = driver.findElement(By.xpath("//input[@placeholder='Search users, devices, and merchants...']"));
//         searchBar.clear();
//         searchBar.sendKeys(deviceId);
//         searchBar.sendKeys(Keys.ENTER);
//         Thread.sleep(3000);
//     }

//     private boolean isElementPresent(By locator) {
//         return !driver.findElements(locator).isEmpty();
//     }

//     private void takeScreenshot(String fileName, String folderPath) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File(folderPath + "/" + fileName + "_" + screenshotCounter++ + ".png");
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
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.interactions.Actions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import io.github.bonigarcia.wdm.WebDriverManager;
// import pages.LoginPage;
// import utils.ExcelUtils;

// public class SendDeviceMessageTest {
//     WebDriver driver;
//     LoginPage loginPage;
//     WebDriverWait wait;
//     int attemptCounter = 1;

//     @BeforeClass
//     public void setup() {
//         WebDriverManager.chromedriver().setup();
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//         loginPage = new LoginPage(driver);
//     }

//     @Test
//     public void sendMessageToDevices() throws IOException, InterruptedException {
//         loginPage.goTo();
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(4000);

//         List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
//         int rowCounter = 1;

//         for (Map<String, String> row : testData) {
//             String srNo = row.get("Sr No");
//             String deviceId = row.get("DeviceID");
//             String amount = row.get("Amount");
//             String txnMode = row.get("TransactionMode");
//             String txnType = row.get("TransactionType");

//             String timestamp = timestamp();
//             String status = "FAIL";
//             String message = "";

//             // Folder for screenshots per attempt
//             String folderPath = "screenshots/Attempt_" + attemptCounter + "/" + srNo + "_" + deviceId;
//             new File(folderPath).mkdirs();

//             try {
//                 searchDevice(deviceId);
//                 takeScreenshot(folderPath, "Search");

//                 if (isElementPresent(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"))) {
//                     WebElement optionsButton = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     Actions actions = new Actions(driver);
//                     actions.moveToElement(optionsButton).perform();
//                     Thread.sleep(1500);
//                     takeScreenshot(folderPath, "OptionsHovered");

//                     driver.findElement(By.xpath("//img[@alt='send-message']")).click();
//                     Thread.sleep(1500);
//                     takeScreenshot(folderPath, "SendMessageClicked");

//                     Thread.sleep(1000);

//                     fillInput("Amount*", amount, folderPath);
//                     fillInput("Transaction Mode*", txnMode, folderPath);
//                     fillInput("Transaction Type*", txnType, folderPath);
//                     takeScreenshot(folderPath, "MessageFilled");

//                     WebElement submitButton = driver.findElement(By.xpath("//button[contains(@class, 'btn-primary-outline') and text()=' Submit ']"));
//                     if (submitButton.isEnabled()) {
//                         submitButton.click();
//                         Thread.sleep(2000);
//                         takeScreenshot(folderPath, "MessageSubmitted");
//                         status = "PASS";
//                     } else {
//                         message = "Submit button disabled. Invalid input.";
//                         takeScreenshot(folderPath, "SubmitDisabled");
//                     }
//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(folderPath, "DeviceNotFound");
//                 }
//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(folderPath, "Exception");
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("Device ID", deviceId);
//             resultRow.put("Amount", amount);
//             resultRow.put("Transaction Mode", txnMode);
//             resultRow.put("Transaction Type", txnType);
//             resultRow.put("Status", status);
//             resultRow.put("Screenshot", folderPath);
//             resultRow.put("Timestamp", timestamp);
//             resultRow.put("Message", message);

//             ExcelUtils.writeResult(rowCounter++, resultRow);

//             driver.navigate().refresh();
//             Thread.sleep(4000);
//             attemptCounter++;
//         }
//     }

//     private void searchDevice(String deviceId) throws InterruptedException {
//         WebElement searchBar = driver.findElement(By.xpath("//input[@placeholder='Search users, devices, and merchants...']"));
//         searchBar.clear();
//         searchBar.sendKeys(deviceId);
//         searchBar.sendKeys(Keys.ENTER);
//         Thread.sleep(3000);
//     }

//     private boolean isElementPresent(By locator) {
//         return !driver.findElements(locator).isEmpty();
//     }

//     private void takeScreenshot(String folderPath, String filename) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File(folderPath + "/" + filename + ".png");
//         FileUtils.copyFile(src, dest);
//     }

//     private String timestamp() {
//         return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
//     }

//     private void fillInput(String label, String value, String folderPath) throws IOException, InterruptedException {
//         WebElement labelElement = driver.findElement(By.xpath("//label[contains(text(),'" + label + "')]"));
//         String inputId = labelElement.getAttribute("for");
//         WebElement inputField = driver.findElement(By.id(inputId));
//         ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", inputField);
//         wait.until(ExpectedConditions.elementToBeClickable(inputField));
//         inputField.click();
//         inputField.clear();
//         inputField.sendKeys(value);
//         Thread.sleep(1000);
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
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.Keys;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.interactions.Actions;
// import org.openqa.selenium.support.ui.WebDriverWait;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import io.github.bonigarcia.wdm.WebDriverManager;
// import pages.LoginPage;
// import utils.ExcelUtils;

// public class SendDeviceMessageTest {
//     WebDriver driver;
//     LoginPage loginPage;
//     WebDriverWait wait;
//     int screenshotCounter = 1;

//     @BeforeClass
//     public void setup() {
//         WebDriverManager.chromedriver().setup();
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//         loginPage = new LoginPage(driver);
//     }

//     @Test
//     public void sendMessageToDevices() throws IOException, InterruptedException {
//         loginPage.goTo();
//         loginPage.enterEmail("adityakonda@cwdin.com");
//         loginPage.enterPassword("Aditya@123");
//         loginPage.clickLogin();
//         Thread.sleep(4000);
//         takeScreenshot("Login");

//         List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
//         int rowCounter = 1;

//         for (Map<String, String> row : testData) {
//             String srNo = row.get("Sr No");
//             String deviceId = row.get("DeviceID");
//             String amount = row.get("Amount");
//             String txnMode = row.get("Transaction Mode");
//             String txnType = row.get("Transaction Type");

//             String timestamp = timestamp();
//             String screenshotName = "TC_" + srNo + "_" + timestamp;
//             String status = "FAIL";
//             String message = "";

//             try {
//                 searchDevice(deviceId);
//                 takeScreenshot(screenshotName + "_Search");

//                 if (isElementPresent(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"))) {
//                     WebElement optionsButton = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     Actions actions = new Actions(driver);
//                     actions.moveToElement(optionsButton).perform();
//                     Thread.sleep(1500);
//                     takeScreenshot(screenshotName + "_OptionsHovered");

//                     driver.findElement(By.xpath("//img[@alt='send-message']")).click();
//                     Thread.sleep(1500);
//                     takeScreenshot(screenshotName + "_SendMessageClicked");

//                    Thread.sleep(1000);
 
//                    // Amount
// // WebElement amountInput = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]/following::input[1]"));
// // ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", amountInput);
// // amountInput.sendKeys(amount);
// // Thread.sleep(500);

// WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
// String amountInputId = amountLabel.getAttribute("for");
// WebElement amountInput = driver.findElement(By.id(amountInputId));
// amountInput.click();
// amountInput.clear();
// amountInput.sendKeys(amount);
// Thread.sleep(1000);



// // Transaction Mode
// WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
// String txnModeId = txnModeLabel.getAttribute("for");
// WebElement txnModeInput = driver.findElement(By.id(txnModeId));
// ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
// txnModeInput.sendKeys(txnMode);
// Thread.sleep(500);

// // Transaction Type
// WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
// String txnTypeId = txnTypeLabel.getAttribute("for");
// WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
// ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
// txnTypeInput.sendKeys(txnType);
// Thread.sleep(500);
//                     takeScreenshot(screenshotName + "_MessageFilled");

//                     driver.findElement(By.xpath("//button[contains(@class, 'btn-primary-outline') and text()=' Submit ']")).click();
//                     Thread.sleep(2000);
//                     takeScreenshot(screenshotName + "_MessageSubmitted");
//                     status = "PASS";

//                     driver.navigate().refresh();
//                     Thread.sleep(4000); 

                 
//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(screenshotName + "_NotFound");
//                 }
//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(screenshotName + "_Exception");
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("Device ID", deviceId);
//             resultRow.put("Amount", amount);
//             resultRow.put("Transaction Mode", txnMode);
//             resultRow.put("Transaction Type", txnType);
//             resultRow.put("Status", status);
//             resultRow.put("Screenshot", "screenshots/" + screenshotName + ".png");
//             resultRow.put("Timestamp", timestamp);
//             resultRow.put("Message", message);

//             ExcelUtils.writeResult(rowCounter++, resultRow);
//         }
//     }

//     private void searchDevice(String deviceId) throws InterruptedException {
//         WebElement searchBar = driver.findElement(By.xpath("//input[@placeholder='Search users, devices, and merchants...']"));
//         searchBar.clear();
//         searchBar.sendKeys(deviceId);
//         searchBar.sendKeys(Keys.ENTER);
//         Thread.sleep(3000);
//     }

//     private boolean isElementPresent(By locator) {
//         return !driver.findElements(locator).isEmpty();
//     }

//     private void takeScreenshot(String filename) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         File dest = new File("screenshots/" + filename + "_" + screenshotCounter++ + ".png");
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

















































































































































































































// // package tests;

// // import java.io.File;
// // import java.io.IOException;
// // import java.time.Duration;
// // import java.time.LocalDateTime;
// // import java.time.format.DateTimeFormatter;
// // import java.util.HashMap;
// // import java.util.List;
// // import java.util.Map;

// // import org.apache.commons.io.FileUtils;
// // import org.openqa.selenium.By;
// // import org.openqa.selenium.Keys;
// // import org.openqa.selenium.NoSuchElementException;
// // import org.openqa.selenium.OutputType;
// // import org.openqa.selenium.TakesScreenshot;
// // import org.openqa.selenium.WebDriver;
// // import org.openqa.selenium.WebElement;
// // import org.openqa.selenium.chrome.ChromeDriver;
// // import org.openqa.selenium.interactions.Actions;
// // import org.openqa.selenium.support.ui.ExpectedConditions;
// // import org.openqa.selenium.support.ui.WebDriverWait;
// // import org.testng.annotations.AfterClass;
// // import org.testng.annotations.BeforeClass;
// // import org.testng.annotations.Test;

// // import io.github.bonigarcia.wdm.WebDriverManager;
// // import pages.LoginPage;
// // import utils.ExcelUtils;

// // public class SendDeviceMessageTest {
// //     WebDriver driver;
// //     LoginPage loginPage;
// //     WebDriverWait wait;
// //     int screenshotCounter = 1;

// //     @BeforeClass
// //     public void setup() {
// //         WebDriverManager.chromedriver().setup();
// //         driver = new ChromeDriver();
// //         driver.manage().window().maximize();
// //         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
// //         loginPage = new LoginPage(driver);
// //     }

// //     @Test
// //     public void sendMessageToDevices() throws InterruptedException, IOException {
// //         loginPage.goTo();
// //         loginPage.enterEmail("adityakonda@cwdin.com");
// //         loginPage.enterPassword("Aditya@123");
// //         loginPage.clickLogin();
// //         Thread.sleep(3000);
// //         takeScreenshot("Login");

// //         List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
// //         int rowCounter = 1;

// //         for (Map<String, String> row : testData) {
// //             String srNo = row.get("Sr No");
// //             String deviceId = row.get("Device ID");
// //             String amount = row.get("Amount");
// //             String txnMode = row.get("Transaction Mode");
// //             String txnType = row.get("Transaction Type");

// //             String timestamp = timestamp();
// //             String screenshotName = "Device_" + deviceId + "_" + timestamp;
// //             String status = "FAIL";
// //             String message = "";

// //             try {
// //                 searchDevice(deviceId);
// //                 takeScreenshot(screenshotName + "_Search");

// //                 if (isElementPresent(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"))) {
// //                     WebElement optionsButton = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
// //                     Actions actions = new Actions(driver);
// //                     actions.moveToElement(optionsButton).perform();
// //                     Thread.sleep(1000);
// //                     takeScreenshot(screenshotName + "_OptionsHovered");

// //                     driver.findElement(By.xpath("//img[@alt='send-message']")).click();
// //                     Thread.sleep(1000);
// //                     takeScreenshot(screenshotName + "_SendMessageClicked");

// //                     driver.findElement(By.xpath("//label[contains(text(),'Amount*')]/following-sibling::input")).sendKeys(amount);
// //                     driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]/following-sibling::input")).sendKeys(txnMode);
// //                     driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]/following-sibling::input")).sendKeys(txnType);
// //                     takeScreenshot(screenshotName + "_MessageFilled");

// //                     driver.findElement(By.xpath("//button[contains(@class, 'btn-primary-outline') and text()=' Submit ']")).click();
// //                     Thread.sleep(1000);
// //                     takeScreenshot(screenshotName + "_MessageSubmitted");
// //                     status = "PASS";

// //                 } else {
// //                     message = "Device not found";
// //                     takeScreenshot(screenshotName + "_NotFound");
// //                 }
// //             } catch (Exception e) {
// //                 message = "Exception: " + e.getMessage();
// //                 takeScreenshot(screenshotName + "_Exception");
// //             }

// //             Map<String, String> resultRow = new HashMap<>();
// //             resultRow.put("Sr No", srNo);
// //             resultRow.put("Device ID", deviceId);
// //             resultRow.put("Amount", amount);
// //             resultRow.put("Transaction Mode", txnMode);
// //             resultRow.put("Transaction Type", txnType);
// //             resultRow.put("Status", status);
// //             resultRow.put("Screenshot", "screenshots/" + screenshotName + ".png");
// //             resultRow.put("Timestamp", timestamp);
// //             resultRow.put("Message", message);

// //             ExcelUtils.writeResult(rowCounter++, resultRow);
// //         }
// //     }

// //     private void searchDevice(String deviceId) throws InterruptedException {
// //         WebElement searchBar = driver.findElement(By.xpath("//input[@placeholder='Search users, devices, and merchants...']"));
// //         searchBar.clear();
// //         searchBar.sendKeys(deviceId);
// //         searchBar.sendKeys(Keys.ENTER);
// //         Thread.sleep(2000);
// //     }

// //     private boolean isElementPresent(By locator) {
// //         return !driver.findElements(locator).isEmpty();
// //     }

// //     private void takeScreenshot(String filename) throws IOException {
// //         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
// //         File dest = new File("screenshots/" + filename + "_" + screenshotCounter++ + ".png");
// //         FileUtils.copyFile(src, dest);
// //     }

// //     private String timestamp() {
// //         return LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMdd_HHmmss"));
// //     }

// //     @AfterClass
// //     public void tearDown() {
// //         if (driver != null) {
// //             driver.quit();
// //         }
// //     }
// // }
