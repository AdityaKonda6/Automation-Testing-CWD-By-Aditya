package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    private static final String INPUT_PATH = "data/DeviceMessageInput.xlsx";
    private static final String OUTPUT_PATH = "data/DeviceMessageResults.xlsx";

    public static List<Map<String, String>> readDeviceMessageData() throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(INPUT_PATH);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        Row headerRow = sheet.getRow(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Map<String, String> rowData = new HashMap<>();
            for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                String key = headerRow.getCell(j).getStringCellValue().trim();
                Cell cell = row.getCell(j);
                String value = "";
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        value = String.format("%.0f", cell.getNumericCellValue());
                    } else {
                        value = cell.toString().trim();
                    }
                }
                rowData.put(key, value);
            }
            data.add(rowData);
        }
        
        workbook.close();
        fis.close();
        return data;
    }

    public static void writeResult(int rowNum, Map<String, String> resultData) throws IOException {
        File file = new File(OUTPUT_PATH);
        Workbook workbook;
        Sheet sheet;

        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
            fis.close();
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Results");

            Row header = sheet.createRow(0);
            String[] headers = {
                "Sr No", "Device ID", "Amount", "Transaction Mode", "Transaction Type",
                "Status", "Screenshot", "Timestamp", "Message"
            };
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
            }
        }

        Row row = sheet.createRow(rowNum);
        int col = 0;
        row.createCell(col++).setCellValue(resultData.getOrDefault("Sr No", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Device ID", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Amount", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Transaction Mode", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Transaction Type", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Status", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Screenshot", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Timestamp", ""));
        row.createCell(col++).setCellValue(resultData.getOrDefault("Message", ""));

        FileOutputStream fos = new FileOutputStream(OUTPUT_PATH);
        workbook.write(fos);
        workbook.close();
        fos.close();
    }
}







































































// package utils;

// import java.io.File;
// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.Row;
// import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.ss.usermodel.Workbook;
// import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// public class ExcelUtils {
//     private static final String INPUT_PATH = "data/DeviceMessageInput.xlsx";
//     private static final String OUTPUT_PATH = "data/DeviceMessageResults.xlsx";

//     public static List<Map<String, String>> readDeviceMessageData() throws IOException {
//     List<Map<String, String>> data = new ArrayList<>();
//     String filePath = "data/DeviceMessageInput.xlsx";
//     FileInputStream fis = new FileInputStream(filePath);
//     Workbook workbook = new XSSFWorkbook(fis);
//     Sheet sheet = workbook.getSheetAt(0);
//     Row headerRow = sheet.getRow(0);

//     for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//         Row row = sheet.getRow(i);
//         if (row == null) continue;
//         Map<String, String> rowData = new HashMap<>();
//         for (int j = 0; j < headerRow.getLastCellNum(); j++) {
//             String key = headerRow.getCell(j).getStringCellValue().trim();
//             Cell cell = row.getCell(j);
//             String value = cell != null ? cell.toString().trim() : "";
//             if (key.equalsIgnoreCase("DeviceId")) {
//                 value = value.replace("E", "").split("\\.")[0]; // Fix exponential numbers
//             }
//             rowData.put(key, value);
//         }
//         data.add(rowData);
//     }

//     workbook.close();
//     fis.close();
//     return data;
// }

// public static void writeDeviceMessageResult(int rowNum, Map<String, String> resultData) throws IOException {
//     String filePath = "data/DeviceMessageResults.xlsx";
//     File file = new File(filePath);
//     Workbook workbook;
//     Sheet sheet;

//     if (file.exists()) {
//         try (FileInputStream fis = new FileInputStream(file)) {
//             workbook = new XSSFWorkbook(fis);
//             sheet = workbook.getSheetAt(0);
//         }
//     } else {
//         workbook = new XSSFWorkbook();
//         sheet = workbook.createSheet("Results");
//         Row header = sheet.createRow(0);
//         String[] headers = {
//             "Sr No", "DeviceId", "Amount", "TransactionMode", "TransactionType",
//             "Status", "Screenshot", "Timestamp", "Message"
//         };
//         for (int i = 0; i < headers.length; i++) {
//             header.createCell(i).setCellValue(headers[i]);
//         }
//     }

//     Row row = sheet.createRow(rowNum);
//     int col = 0;
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Sr No", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("DeviceId", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Amount", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("TransactionMode", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("TransactionType", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Status", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Screenshot", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Timestamp", ""));
//     row.createCell(col++).setCellValue(resultData.getOrDefault("Message", ""));

//         try (FileOutputStream fos = new FileOutputStream(filePath)) {
//             workbook.write(fos);
//             workbook.close();
//         }
// }

//     public static String getOUTPUT_PATH() {
//         return OUTPUT_PATH;
//     }

//     public static String getINPUT_PATH() {
//         return INPUT_PATH;
//     }

// }

