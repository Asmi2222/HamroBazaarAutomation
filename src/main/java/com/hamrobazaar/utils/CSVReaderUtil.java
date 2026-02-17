package com.hamrobazaar.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

/**
 * CSVReader - Utility class to read test data from CSV files
 * Reads CSV and converts to Map for easy data access
 */
public class CSVReaderUtil {
    
    private static final Logger log = LogManager.getLogger(CSVReaderUtil.class);
    
    /**
     * Read CSV file and return list of maps
     * Each map represents one row with column headers as keys
     * 
     * @param filePath Path to CSV file
     * @return List of maps containing CSV data
     */
    public static List<Map<String, String>> readCSV(String filePath) {
        List<Map<String, String>> data = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            
            List<String[]> allRows = reader.readAll();
            
            if (allRows.isEmpty()) {
                log.warn("CSV file is empty: {}", filePath);
                return data;
            }
            
            // First row contains headers
            String[] headers = allRows.get(0);
            log.info("CSV Headers: {}", String.join(", ", headers));
            
            // Process each data row (skip header row)
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);
                Map<String, String> rowData = new HashMap<>();
                
                // Map each column to its header
                for (int j = 0; j < headers.length && j < row.length; j++) {
                    rowData.put(headers[j].trim(), row[j].trim());
                }
                
                data.add(rowData);
                log.debug("Read row {}: {}", i, rowData);
            }
            
            log.info("Successfully read {} rows from CSV: {}", data.size(), filePath);
            
        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", filePath, e);
        } catch (CsvException e) {
            log.error("Failed to parse CSV file: {}", filePath, e);
        }
        
        return data;
    }
    
    /**
     * Get specific test data by row index
     * 
     * @param filePath Path to CSV file
     * @param rowIndex Index of row to retrieve (0-based, excluding header)
     * @return Map containing row data
     */
    public static Map<String, String> getTestData(String filePath, int rowIndex) {
        List<Map<String, String>> allData = readCSV(filePath);
        
        if (rowIndex >= 0 && rowIndex < allData.size()) {
            return allData.get(rowIndex);
        } else {
            log.error("Invalid row index: {}. Available rows: {}", rowIndex, allData.size());
            return new HashMap<>();
        }
    }
    
    /**
     * Get first row of test data (most common use case)
     * 
     * @param filePath Path to CSV file
     * @return Map containing first row data
     */
    public static Map<String, String> getTestData(String filePath) {
        return getTestData(filePath, 0);
    }
    
    /**
     * Print CSV data in readable format (for debugging)
     * 
     * @param filePath Path to CSV file
     */
    public static void printCSVData(String filePath) {
        List<Map<String, String>> data = readCSV(filePath);
        
        log.info("========== CSV Data ==========");
        for (int i = 0; i < data.size(); i++) {
            log.info("Row {}: {}", i, data.get(i));
        }
        log.info("==============================");
    }
}