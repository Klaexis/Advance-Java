package asciiproject.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asciiproject.model.Pair;
import asciiproject.model.Row;
import asciiproject.service.TableService;

public class FileHandler {
    private static String fileName;
    private static final String FOLDER_NAME = "text files"; // Folder for all .txt files

    // Set the file name 
    public static void setFileName(String name) {
        fileName = name;
    }

    // Check if folder exists, if not create it
    private static File getFolder() {
        File folder = new File(FOLDER_NAME);
        if(!folder.exists()) {
            folder.mkdir(); // create folder if it doesn’t exist
            System.out.println("Created folder: " + folder.getAbsolutePath());
        }
        return folder;
    }

    // Get the full file path inside the "text files" directory
    private static File getFilePath(String fileName) {
        return new File(getFolder(), fileName);
    }
    
    // Generates a new random ASCII table and writes it directly to a .txt file.
    public static void generateTableAndSave(String fileName, int rows, int cols) {
        File file = getFilePath(fileName);

        // Write table data to file
        try (PrintWriter writer = new PrintWriter(file)) { // Close PrintWriter after block ends
            for(int i = 0; i < rows; i++) {
                List<Pair> row = TableService.generateRandomKeyPair(cols);
                for(Pair p : row) {
                    writer.print(p.toString() + " "); // Space separate pairs
                }
                writer.println(); // New line after each row
            }
            System.out.println("New table generated and saved to " + file.getPath());
        } catch(IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        System.out.println();
    }

    // Checks if a text file exists (from command-line args or user input), creates one if not.
    public static String checkOrCreateFile(Scanner sc, String[] args) {
        String fileName = null;

        getFolder(); // Ensure folder exists

        // Check command line argument
        if(args.length > 0 && !args[0].trim().isEmpty()) {
            fileName = args[0].trim();
            if(!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = getFilePath(fileName);

            // Ask user if not found
            while(!file.exists()) {
                System.out.print("File not found. Please enter a valid filename (Enter blank to create a new one): ");
                fileName = sc.nextLine().trim();

                if(!fileName.endsWith(".txt") && !fileName.isEmpty()) {
                    fileName += ".txt";
                }

                // Break if user inputs empty to create new file 
                if(fileName.isEmpty()) break;

                file = getFilePath(fileName);
            }
        }

        // If no file found, create a new one
        if(fileName == null || fileName.trim().isEmpty()) {
            System.out.print("Enter a new file name to create: ");
            fileName = sc.nextLine().trim();

            if(!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = getFilePath(fileName);

            if(file.exists()) {
                System.out.println("File already exists. Loading existing file instead.");
            } else {
                // Ask user for table dimensions 
                int[] tableDimensions = TableService.getTableDimensions(sc);
                int row = tableDimensions[0];
                int col = tableDimensions[1];

                // Generate table and save to file
                generateTableAndSave(fileName, row, col);
            }
        }

        System.out.println();
        return fileName;
    }

    // Load table from file 
    public static boolean loadFromFile(String fileName, List<Row> table) {
        File file = getFilePath(fileName);
        if(!file.exists()) {
            System.out.println("File not found: " + file.getPath());
            return false;
        }

        /*  
            Regex pattern to match key-value pairs in the format: (key , value)
            \(             match literal '('
            (.*?)          capture the key — any characters (non-greedy)
            \s,\s          match a comma with one space on each side
            (.*?)          capture the value — any characters (non-greedy)
            \)             match literal ')'
            \s*            match optional whitespace after ')'
            (?=\(|$)       ensure the match is followed by '(' or end of string
        */
       Pattern pattern = Pattern.compile("\\((.*?)\\s,\\s(.*?)\\)\\s*(?=\\(|$)");

        // Wrap FileReader in BufferedReader for faster reading
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) { // Close BufferedReader and FileReader after block ends
            String line;
            table.clear(); // Clear existing table before loading new data

            while((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher matcher = pattern.matcher(line);
                List<Pair> cells = new ArrayList<>();

                // Extract all (key , value) pairs from the line
                while (matcher.find()) {
                    cells.add(new Pair(matcher.group(1), matcher.group(2)));
                }

                // Each line becomes a Row in the table
                table.add(new Row(cells));
            }

            System.out.println("\nFile loaded successfully from " + file.getPath() + "\n");
            return true;
        } catch(IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    // Save table to file
    public static void saveToFile(List<Row> table) {
        if(fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        File file = getFilePath(fileName);

        // Write table data to file
        try (PrintWriter writer = new PrintWriter(file)) { // Close PrintWriter after block ends
            for(Row row : table) {
                writer.println(row.toString());
            }
            System.out.println("Table saved to " + file.getPath() + "\n");
        } catch(IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}