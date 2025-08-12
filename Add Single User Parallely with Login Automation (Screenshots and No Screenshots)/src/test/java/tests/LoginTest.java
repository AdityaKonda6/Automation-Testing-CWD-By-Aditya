// LoginTest.java
package tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.LoginPage;

public class LoginTest {
    WebDriver driver;
    LoginPage loginPage;
       

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        // options.setBinary("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"); 
        options.setBinary("/usr/bin/google-chrome"); 
        driver = new ChromeDriver(options);
        
        driver.manage().window().maximize();
        loginPage = new LoginPage(driver);
    }
    @Test(priority = 1)
    public void runAllLogins() throws IOException, InterruptedException {
        String[][] validCreds = {
            {"adityakonda@cwdin.com", "Aditya@123"},
            {"adityakonda@cwdin.com", "Aditya@123"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"Omkar.chavan@cwdin.com", "Omkar@4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"},
            {"sushmita.singh@cwdin.com", "@Sushmita4321"}
        };


        for (int i = 0; i < validCreds.length; i++) {
            loginPage.goTo();
            Thread.sleep(1000);

            loginPage.enterEmail(validCreds[i][0]);
            loginPage.enterPassword(validCreds[i][1]);
            loginPage.clickLogin();

            Thread.sleep(2000);
            takeScreenshot("validLogin_" + (i + 1));

          
            driver.navigate().back();
            Thread.sleep(1000);
        }

        for (int i = 1; i <= 15; i++) {
            loginPage.goTo();
            Thread.sleep(1000);

            loginPage.enterEmail("invalid" + i + "@test.com");
            loginPage.enterPassword("wrongpass");
            loginPage.clickLogin();

            Thread.sleep(2000);
            takeScreenshot("invalidLogin_" + i);
        }
    }

    public void takeScreenshot(String fileName) throws IOException {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String dirPath = "screenshots";
        Files.createDirectories(Paths.get(dirPath));
        FileUtils.copyFile(src, new File(dirPath + "/" + fileName + ".png"));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}





















































// // LoginTest.java
// // This file contains the test cases for the login functionality.
// package tests;

// import java.io.File;
// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;

// import org.apache.commons.io.FileUtils;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import config.Config;
// import pages.LoginPage;

// public class LoginTest {
//     WebDriver driver;
//     LoginPage loginPage;

//     @BeforeClass
//     public void setup() {
//         WebDriverManager.chromedriver().setup();
//         WebDriver driver = new ChromeDriver();


//         System.setProperty("webdriver.chromium.chromedriver", Config.DRIVER_PATH);
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         loginPage = new LoginPage(driver);
//     }
//     /**
//      * This test performs 15 valid login attempts with predefined credentials,
//      * followed by 15 invalid login attempts with incorrect credentials.
//      * It takes screenshots after each attempt for verification.
//      */
// @Test(priority = 1)
// public void runAllLogins() throws IOException, InterruptedException {
//     // ✅ Define 15 valid credentials
//     String[][] validCreds = {
//         {"user1@cwdin.com", "Pass1@123"},
//         {"user2@cwdin.com", "Pass2@123"},
//          {"amal.a@cwdin.com", "Pass@123"},
//         {"user4@cwdin.com", "Pass4@123"},
//         {"user5@cwdin.com", "Pass5@123"},
//         {"user6@cwdin.com", "Pass6@123"},
//         {"user7@cwdin.com", "Pass7@123"},
//         {"user8@cwdin.com", "Pass8@123"},
//         {"user9@cwdin.com", "Pass9@123"},
//         {"user10@cwdin.com", "Pass10@123"},
//         {"user11@cwdin.com", "Pass11@123"},
//         {"user12@cwdin.com", "Pass12@123"},
//         {"user13@cwdin.com", "Pass13@123"},
//         {"user14@cwdin.com", "Pass14@123"},
//         {"user15@cwdin.com", "Pass15@123"}
//     };


//     for (int i = 0; i < validCreds.length; i++) {
//         loginPage.goTo();
//         Thread.sleep(1000);

//         loginPage.enterEmail(validCreds[i][0]);
//         loginPage.enterPassword(validCreds[i][1]);
//         loginPage.clickLogin();

//         Thread.sleep(2000);
//         takeScreenshot("validLogin_" + (i + 1));

//         // Navigate back if dashboard redirects you
//         driver.navigate().back();
//         Thread.sleep(1000);
//     }

//     // 🔁 Try 15 invalid logins (same as before)
//     for (int i = 1; i <= 15; i++) {
//         loginPage.goTo();
//         Thread.sleep(1000);

//         loginPage.enterEmail("invalid" + i + "@test.com");
//         loginPage.enterPassword("wrongpass");
//         loginPage.clickLogin();

//         Thread.sleep(2000);
//         takeScreenshot("invalidLogin_" + i);
//     }
// }

//     public void takeScreenshot(String fileName) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         String dirPath = "screenshots";
//         Files.createDirectories(Paths.get(dirPath));
//         FileUtils.copyFile(src, new File(dirPath + "/" + fileName + ".png"));
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
// import java.nio.file.Files;
// import java.nio.file.Paths;

// import org.apache.commons.io.FileUtils;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.TakesScreenshot;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.testng.annotations.AfterClass;
// import org.testng.annotations.BeforeClass;
// import org.testng.annotations.Test;

// import config.Config;
// import pages.LoginPage;

// public class LoginTest {
//     WebDriver driver;
//     LoginPage loginPage;

//     @BeforeClass
//     public void setup() {
//         System.setProperty("webdriver.chrome.driver", Config.DRIVER_PATH);
//         driver = new ChromeDriver();
//         driver.manage().window().maximize();
//         loginPage = new LoginPage(driver);
//     }

//     @Test(priority = 1)
//     public void runAllLogins() throws IOException, InterruptedException {
//         // 🔁 15 Valid Login Attempts
//         for (int i = 1; i <= 15; i++) {
//             loginPage.goTo();
//             Thread.sleep(1000); // Give time for page to load

//             loginPage.enterEmail("amal.a@cwdin.com"); // ✅ Replace with valid
//             loginPage.enterPassword("Pass@123");      // ✅ Replace with valid
//             loginPage.clickLogin();

//             Thread.sleep(2000); // Wait for navigation

//             takeScreenshot("validLogin_" + i);
//             driver.navigate().back(); // Go back to login page
//             Thread.sleep(1000);
//         }

//         // 🔁 15 Invalid Login Attempts
//         for (int i = 1; i <= 15; i++) {
//             loginPage.goTo();
//             Thread.sleep(1000);

//             loginPage.enterEmail("invalid" + i + "@test.com");
//             loginPage.enterPassword("wrongpass");
//             loginPage.clickLogin();

//             Thread.sleep(2000); // Wait for error message to load
//             takeScreenshot("invalidLogin_" + i);
//         }
//     }

//     public void takeScreenshot(String fileName) throws IOException {
//         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//         String dirPath = "screenshots";
//         Files.createDirectories(Paths.get(dirPath));
//         FileUtils.copyFile(src, new File(dirPath + "/" + fileName + ".png"));
//     }

//     @AfterClass
//     public void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }
// }































// // package tests;

// // import org.openqa.selenium.WebDriver;
// // import org.openqa.selenium.chrome.ChromeDriver;
// // import org.testng.annotations.*;
// // import org.openqa.selenium.OutputType;
// // import org.openqa.selenium.TakesScreenshot;

// // import config.Config;
// // import pages.LoginPage;

// // import java.io.File;
// // import java.io.IOException;
// // import java.nio.file.Files;
// // import java.nio.file.Paths;

// // import static org.testng.Assert.assertTrue;
// // import org.apache.commons.io.FileUtils;

// // public class LoginTest {
// //     WebDriver driver;
// //     LoginPage loginPage;

// //     @BeforeClass
// //     public void setup() {
// //         System.setProperty("webdriver.chrome.driver", Config.DRIVER_PATH);
// //         driver = new ChromeDriver();
// //         driver.manage().window().maximize();
// //         loginPage = new LoginPage(driver);
// //     }

// //     @Test(priority = 1)
// //     public void runValidLoginTest15Times() throws IOException {
// //         for (int i = 1; i <= 15; i++) {
// //             loginPage.goTo();
// //             loginPage.enterEmail("amal.a@cwdin.com");  // Replace with valid test user
// //             loginPage.enterPassword("Pass@123");       // Replace with correct password
// //             loginPage.clickLogin();

// //             takeScreenshot("validLogin_" + i);
// //             assertTrue(driver.getCurrentUrl().contains("/dashboard"),
// //                 "Valid login failed on attempt " + i);
// //         }
// //     }

// //     @Test(priority = 2)
// //     public void runInvalidLoginTest15Times() throws IOException {
// //         for (int i = 1; i <= 15; i++) {
// //             loginPage.goTo();
// //             loginPage.enterEmail("invalid_" + i + "@example.com");
// //             loginPage.enterPassword("wrongpwd");
// //             loginPage.clickLogin();

// //             takeScreenshot("invalidLogin_" + i);
// //             String err = loginPage.getError();
// //             assertTrue(err.toLowerCase().contains("invalid") || err.length() > 0,
// //                 "Expected error message on invalid login attempt " + i);
// //         }
// //     }

// //     public void takeScreenshot(String fileName) throws IOException {
// //         File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
// //         String dirPath = "screenshots";
// //         Files.createDirectories(Paths.get(dirPath));
// //         FileUtils.copyFile(src, new File(dirPath + "/" + fileName + ".png"));
// //     }

// //     @AfterClass
// //     public void tearDown() {
// //         if (driver != null) {
// //             driver.quit();
// //         }
// //     }
// // }






























// // // package tests;

// // // import org.openqa.selenium.WebDriver;
// // // import org.openqa.selenium.chrome.ChromeDriver;
// // // import org.testng.Assert;
// // // import org.testng.annotations.AfterClass;
// // // import org.testng.annotations.BeforeClass;
// // // import org.testng.annotations.Test;

// // // import io.github.bonigarcia.wdm.WebDriverManager;  // ✅ Added for auto driver management

// // // import pages.LoginPage;

// // // public class LoginTest {
// // //     WebDriver driver;
// // //     LoginPage loginPage;

// // //     @BeforeClass
// // //     public void setup() {
// // //         // ✅ Automatically sets up the correct ChromeDriver
// // //         WebDriverManager.chromedriver().setup();
// // //         driver = new ChromeDriver();
// // //         driver.manage().window().maximize();
// // //         loginPage = new LoginPage(driver);
// // //     }

// // //     @Test(priority = 1)
// // //     public void validLoginTest() {
// // //         loginPage.goTo();
// // //         loginPage.enterEmail("amal.a@cwdin.com");
// // //         loginPage.enterPassword("Pass@123");
// // //         loginPage.clickLogin();

// // //         Assert.assertTrue(driver.getCurrentUrl().contains("/dashboard"), 
// // //             "Expected to land on dashboard, but was: " + driver.getCurrentUrl());
// // //     }

// // //     @Test(priority = 2)
// // //     public void invalidLoginTest() {
// // //         loginPage.goTo();
// // //         loginPage.enterEmail("invalid@example.com");
// // //         loginPage.enterPassword("wrongpwd");
// // //         loginPage.clickLogin();

// // //         String err = loginPage.getError();
// // //         Assert.assertTrue(err.contains("Invalid"), "Expected error message, got: " + err);
// // //     }

// // //     @AfterClass
// // //     public void tearDown() {
// // //         if (driver != null) driver.quit();
// // //     }
// // // }
