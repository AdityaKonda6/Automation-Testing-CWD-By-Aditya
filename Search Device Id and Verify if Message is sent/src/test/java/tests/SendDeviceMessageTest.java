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
        takeScreenshot("Login_Success_For_Send_Message ✅", "screenshots/General");

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

                    WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
                    WebElement amountInput = driver.findElement(By.id(amountLabel.getAttribute("for")));
                    amountInput.click();
                    amountInput.clear();
                    amountInput.sendKeys(amount);
                    Thread.sleep(500);

                    WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
                    String txnModeId = txnModeLabel.getAttribute("for");
                    WebElement txnModeInput = driver.findElement(By.id(txnModeId));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
                    txnModeInput.click();
                    txnModeInput.clear();
                    txnModeInput.sendKeys(txnMode);
                    Thread.sleep(500);

                    WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
                    String txnTypeId = txnTypeLabel.getAttribute("for");
                    WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
                    txnTypeInput.click();
                    txnTypeInput.clear();
                    txnTypeInput.sendKeys(txnType);
                    Thread.sleep(500);

                    takeScreenshot(baseFileName + "_MessageFilled", folderName);

                    int txnModeVal = Integer.parseInt(txnMode);
                    int txnTypeVal = Integer.parseInt(txnType);


                    if (txnModeVal < 0 || txnModeVal > 10 || txnTypeVal < 0 || txnTypeVal > 10) {
                        message = "Transaction Mode or Transaction Type exceeds limit of 0 to 10";
                        takeScreenshot(baseFileName + "_InvalidInputRange ❌", folderName);
                    } else {
                        WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Submit')]"));
                        boolean isEnabled = submitBtn.isEnabled();

                        if (!isEnabled) {
                            message = "Submit button disabled";
                            takeScreenshot(baseFileName + "_SubmitDisabled ❌", folderName);
                        } else {
                            submitBtn.click();
                            Thread.sleep(1500);
                            takeScreenshot(baseFileName + "_MessageSubmitted ✅", folderName);
                            driver.navigate().refresh();
                            Thread.sleep(4000);

                            searchDevice(deviceId);
                            WebElement optionsButton2 = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
                            new Actions(driver).moveToElement(optionsButton2).perform();
                            Thread.sleep(3000);
                            driver.findElement(By.xpath("//img[@alt='device-overview']")).click();
                            Thread.sleep(3000);
                            takeScreenshot(baseFileName + "_DeviceOverviewOpened", folderName);
                            driver.navigate().refresh();

                            WebElement audioPlayedCell = driver.findElement(By.xpath("//th[contains(.,'Audio Played')]/ancestor::table/tbody/tr[1]/td[last()]"));
                            String audioPlayedValue = audioPlayedCell.getText().trim();

                            if (audioPlayedValue.equalsIgnoreCase("SUCCESS")) {
                                status = "PASS";
                                message = "Submitted and played. Audio Played: SUCCESS";
                                takeScreenshot(baseFileName + "_AudioSuccess ✅", folderName);
                            } else if (audioPlayedValue.equalsIgnoreCase("FAILED")) {
                                status = "FAIL";
                                message = "Submitted but device failed to play. Audio Played: FAILED";
                                takeScreenshot(baseFileName + "_AudioFailed ❌", folderName);
                            } else {
                                status = "FAIL";
                                message = "Not sent to device. Audio Played: UNKNOWN";
                                takeScreenshot(baseFileName + "_AudioUnknown ❌", folderName);
                            }
                        }
                    }

                    driver.navigate().refresh();
                    Thread.sleep(3000);
                    attemptCounter++;

                } else {
                    message = "Device not found";
                    takeScreenshot(baseFileName + "_NotFound ❌", folderName);
                }

            } catch (Exception e) {
                message = "Exception: " + e.getMessage();
                takeScreenshot(baseFileName + "_Exception ❌", folderName);
            }

            Map<String, String> resultRow = new HashMap<>();
            resultRow.put("Sr No", srNo);
            resultRow.put("DeviceID", deviceId);
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
//         takeScreenshot("Login", "screenshots/General");

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
//                 // Step 1: Search and Send Message
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

//                     // Validate values
//                     int txnModeVal = Integer.parseInt(txnMode);
//                     int txnTypeVal = Integer.parseInt(txnType);
//                     boolean validRange = txnModeVal >= 0 && txnModeVal <= 10 && txnTypeVal >= 0 && txnTypeVal <= 10;

//                     WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Submit')]"));
//                     boolean isEnabled = submitBtn.isEnabled();

//                     if (!validRange) {
//                         status = "FAIL";
//                         message = "Invalid input: Mode=" + txnMode + ", Type=" + txnType + " (must be 0-10)";
//                         takeScreenshot(baseFileName + "_InvalidInput", folderName);
//                         driver.navigate().refresh();
//                         continue; // Skip rest of loop
//                     } else if (!isEnabled) {
//                         status = "FAIL";
//                         message = "Submit button disabled for Mode=" + txnMode + ", Type=" + txnType;
//                         takeScreenshot(baseFileName + "_SubmitDisabled", folderName);
//                         driver.navigate().refresh();
//                         continue;
//                     }

//                     // Step 2: Refresh and Re-Verify
//                     driver.navigate().refresh();
//                     Thread.sleep(4000);

//                     searchDevice(deviceId);
//                     WebElement optionsButton2 = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     new Actions(driver).moveToElement(optionsButton2).perform();
//                     Thread.sleep(1000);
//                     driver.findElement(By.xpath("//img[@alt='device-overview']")).click();
//                     Thread.sleep(2000);
//                     takeScreenshot(baseFileName + "_DeviceOverviewOpened", folderName);

//                     // String receivedValue = getTextFromOverview("//span[contains(text(),'Received By Device')]/ancestor::table//td[1]");
//                     // String playedValue = getTextFromOverview("//span[contains(text(),'Transaction Played At')]/ancestor::table//td[1]");

//                     // if (receivedValue.equals(" -- ") || playedValue.equals(" -- ")) {
//                     //     message = "Submitted, but NOT played.";
//                     //     status = "FAIL";
//                     //     takeScreenshot(baseFileName + "_NotPlayed", folderName);
//                     // } else if (isEnabled && validRange) {
//                     //     status = "PASS";
//                     //     message = "Submitted and played.";
//                     //     takeScreenshot(baseFileName + "_PlayedSuccess", folderName);
//                     // }

//                     String receivedValue = getTextFromOverview("//span[contains(text(),'Received By Device')]/ancestor::table//td[1]");
//                     String playedValue = getTextFromOverview("//span[contains(text(),'Transaction Played At')]/ancestor::table//td[1]");

// // Final result decision
// if (receivedValue.equals(" -- ") || playedValue.equals(" -- ")) {
//     status = "FAIL";
//     message = "Submitted but NOT played. Received: " + receivedValue + ", Played: " + playedValue;
//     takeScreenshot(baseFileName + "_NotPlayed", folderName);
// } else {
//     status = "PASS";
//     message = "Submitted and played. Received: " + receivedValue + ", Played: " + playedValue;
//     takeScreenshot(baseFileName + "_PlayedSuccess", folderName);
// }
//                     driver.navigate().refresh();
//                     Thread.sleep(3000);
//                     attemptCounter++;

//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(baseFileName + "_NotFound", folderName);
//                 }

//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(baseFileName + "_Exception", folderName);
//             }

//             // Write results to Excel
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

//     private String getTextFromOverview(String xpath) {
//         return driver.findElement(By.xpath(xpath)).getText().trim();
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
// // Same imports as before...

// public class SendDeviceMessageTest {
//     WebDriver driver;
//     LoginPage loginPage;
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
//         takeScreenshot("Login", "screenshots/General");

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
//                 // Step 1: Search and Send Message
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

//                     // Validate values
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
//                         message = "Submitted";
//                     }

//                     // Step 2: Refresh and Re-Verify
//                     driver.navigate().refresh();
//                     Thread.sleep(4000);

//                     searchDevice(deviceId);
//                     WebElement optionsButton2 = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     new Actions(driver).moveToElement(optionsButton2).perform();
//                     Thread.sleep(1000);
//                     driver.findElement(By.xpath("//img[@alt='device-overview']")).click();
//                     Thread.sleep(2000);
//                     takeScreenshot(baseFileName + "_DeviceOverviewOpened", folderName);

//                     String receivedValue = getTextFromOverview("//span[contains(text(),'Received By Device')]/ancestor::table//td[1]");
//                     String playedValue = getTextFromOverview("//span[contains(text(),'Transaction Played At')]/ancestor::table//td[1]");

//                     if (receivedValue.equals("--") || playedValue.equals("--")) {
//                         message = "Submitted, but NOT played.";
//                         status = "FAIL";
//                         takeScreenshot(baseFileName + "_NotPlayed", folderName);
//                     } else if (isEnabled && validRange) {
//                         status = "PASS";
//                         message = "Submitted and played.";
//                         takeScreenshot(baseFileName + "_PlayedSuccess", folderName);
//                     }

//                     driver.navigate().refresh();
//                     Thread.sleep(3000);
//                     attemptCounter++;

//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(baseFileName + "_NotFound", folderName);
//                 }

//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(baseFileName + "_Exception", folderName);
//             }

//             // Write results to Excel
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

//     private String getTextFromOverview(String xpath) {
//         return driver.findElement(By.xpath(xpath)).getText().trim();
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
//         takeScreenshot("Login", "screenshots/General");

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

//                     WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
//                     String amountInputId = amountLabel.getAttribute("for");
//                     WebElement amountInput = driver.findElement(By.id(amountInputId));
//                     amountInput.click();
//                     amountInput.clear();
//                     amountInput.sendKeys(amount);
//                     Thread.sleep(500);

//                     WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
//                     String txnModeId = txnModeLabel.getAttribute("for");
//                     WebElement txnModeInput = driver.findElement(By.id(txnModeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
//                     txnModeInput.click();
//                     txnModeInput.clear();
//                     txnModeInput.sendKeys(txnMode);
//                     Thread.sleep(500);

//                     WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
//                     String txnTypeId = txnTypeLabel.getAttribute("for");
//                     WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
//                     txnTypeInput.click();
//                     txnTypeInput.clear();
//                     txnTypeInput.sendKeys(txnType);
//                     Thread.sleep(500);

//                     takeScreenshot(baseFileName + "_MessageFilled", folderName);

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
//                         message = "Submitted";
//                     }

//                     driver.navigate().refresh();
//                     Thread.sleep(4000);

//                     searchDevice(deviceId);
//                     WebElement optionsButton2 = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                     new Actions(driver).moveToElement(optionsButton2).perform();
//                     Thread.sleep(1000);
//                     driver.findElement(By.xpath("//img[@alt='device-overview']")).click();
//                     Thread.sleep(2000);
//                     takeScreenshot(baseFileName + "_DeviceOverviewOpened", folderName);

//                     String audioPlayed = getTextFromOverview("//th[.='Audio Played']/ancestor::table//tr[2]/td[last()]")
//                             .toUpperCase();

//                     if (audioPlayed.contains("UNKNOWN")) {
//                         message = "Submitted, but NOT sent to device (UNKNOWN)";
//                         status = "FAIL";
//                         takeScreenshot(baseFileName + "_AudioUnknown", folderName);
//                     } else if (audioPlayed.contains("FAILED")) {
//                         message = "Submitted, but FAILED to play on device";
//                         status = "FAIL";
//                         takeScreenshot(baseFileName + "_AudioFailed", folderName);
//                     } else if (audioPlayed.contains("SUCCESS") && validRange && isEnabled) {
//                         status = "PASS";
//                         message = "Submitted and played successfully.";
//                         takeScreenshot(baseFileName + "_AudioSuccess", folderName);
//                     }

//                     driver.navigate().refresh();
//                     Thread.sleep(3000);
//                     attemptCounter++;

//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(baseFileName + "_NotFound", folderName);
//                 }

//             } catch (Exception e) {
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

//     private String getTextFromOverview(String xpath) {
//         return driver.findElement(By.xpath(xpath)).getText().trim();
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
//         takeScreenshot("Login", "screenshots/General");

//         List<Map<String, String>> testData = ExcelUtils.readDeviceMessageData();
//         int rowCounter = 1;

//         for (Map<String, String> row : testData) {
//             String srNo = row.get("Sr No");
//             String deviceId = row.get("DeviceID");
//             String amount = row.get("Amount");
//             String txnMode = row.get("Transaction Mode");
//             String txnType = row.get("Transaction Type");

//             String timestamp = timestamp();
//             String folderName = "screenshots/TC_" + attemptCounter + "/" + deviceId;
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

//                     WebElement amountLabel = driver.findElement(By.xpath("//label[contains(text(),'Amount*')]"));
//                     WebElement amountInput = driver.findElement(By.id(amountLabel.getAttribute("for")));
//                     amountInput.click();
//                     amountInput.clear();
//                     amountInput.sendKeys(amount);
//                     Thread.sleep(500);

//                     WebElement txnModeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Mode*')]"));
//                     String txnModeId = txnModeLabel.getAttribute("for");
//                     WebElement txnModeInput = driver.findElement(By.id(txnModeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnModeInput);
//                     txnModeInput.click();
//                     txnModeInput.clear();
//                     txnModeInput.sendKeys(txnMode);
//                     Thread.sleep(500);

//                     WebElement txnTypeLabel = driver.findElement(By.xpath("//label[contains(text(),'Transaction Type*')]"));
//                     String txnTypeId = txnTypeLabel.getAttribute("for");
//                     WebElement txnTypeInput = driver.findElement(By.id(txnTypeId));
//                     ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", txnTypeInput);
//                     txnTypeInput.click();
//                     txnTypeInput.clear();
//                     txnTypeInput.sendKeys(txnType);
//                     Thread.sleep(500);

//                     takeScreenshot(baseFileName + "_MessageFilled", folderName);

//                     int txnModeVal = Integer.parseInt(txnMode);
//                     int txnTypeVal = Integer.parseInt(txnType);


//                     if (txnModeVal < 0 || txnModeVal > 10 || txnTypeVal < 0 || txnTypeVal > 10) {
//                         message = "Transaction Mode or Transaction Type exceeds limit of 0 to 10";
//                         takeScreenshot(baseFileName + "_InvalidInputRange", folderName);
//                     } else {
//                         WebElement submitBtn = driver.findElement(By.xpath("//button[contains(text(),'Submit')]"));
//                         boolean isEnabled = submitBtn.isEnabled();

//                         if (!isEnabled) {
//                             message = "Submit button disabled";
//                             takeScreenshot(baseFileName + "_SubmitDisabled", folderName);
//                         } else {
//                             submitBtn.click();
//                             Thread.sleep(1500);
//                             takeScreenshot(baseFileName + "_MessageSubmitted", folderName);
//                             driver.navigate().refresh();
//                             Thread.sleep(4000);

//                             searchDevice(deviceId);
//                             WebElement optionsButton2 = driver.findElement(By.xpath("//button[contains(@class, 'hover:text-gray-800')]"));
//                             new Actions(driver).moveToElement(optionsButton2).perform();
//                             Thread.sleep(3000);
//                             driver.findElement(By.xpath("//img[@alt='device-overview']")).click();
//                             Thread.sleep(3000);
//                             takeScreenshot(baseFileName + "_DeviceOverviewOpened", folderName);
//                             driver.navigate().refresh();

//                             WebElement audioPlayedCell = driver.findElement(By.xpath("//th[contains(.,'Audio Played')]/ancestor::table/tbody/tr[1]/td[last()]"));
//                             String audioPlayedValue = audioPlayedCell.getText().trim();

//                             if (audioPlayedValue.equalsIgnoreCase("SUCCESS")) {
//                                 status = "PASS";
//                                 message = "Submitted and played. Audio Played: SUCCESS";
//                                 takeScreenshot(baseFileName + "_AudioSuccess", folderName);
//                             } else if (audioPlayedValue.equalsIgnoreCase("FAILED")) {
//                                 status = "FAIL";
//                                 message = "Submitted but device failed to play. Audio Played: FAILED";
//                                 takeScreenshot(baseFileName + "_AudioFailed", folderName);
//                             } else {
//                                 status = "FAIL";
//                                 message = "Not sent to device. Audio Played: UNKNOWN";
//                                 takeScreenshot(baseFileName + "_AudioUnknown", folderName);
//                             }
//                         }
//                     }

//                     driver.navigate().refresh();
//                     Thread.sleep(3000);
//                     attemptCounter++;

//                 } else {
//                     message = "Device not found";
//                     takeScreenshot(baseFileName + "_NotFound", folderName);
//                 }

//             } catch (Exception e) {
//                 message = "Exception: " + e.getMessage();
//                 takeScreenshot(baseFileName + "_Exception", folderName);
//             }

//             Map<String, String> resultRow = new HashMap<>();
//             resultRow.put("Sr No", srNo);
//             resultRow.put("DeviceID", deviceId);
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
