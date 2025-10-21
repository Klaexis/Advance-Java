package asciiproject.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import asciiproject.model.Pair;

public class AsciiFileHandler {
    private String fileName;
    private static final String FOLDER_NAME = "text files"; // Folder for all .txt files

    // Set the file name 
    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    // Get the full file path inside the folder
    private static File getFilePath(String fileName) {
        return new File(getFolder(), fileName);
    }
    
    // Generate new table and save to file
    public static void generateTableAndSave(String fileName, int rows, int cols) {
        File file = getFilePath(fileName);

        // Write table data to file
        try (PrintWriter writer = new PrintWriter(file)) { // Close PrintWriter after block ends
            for(int i = 0; i < rows; i++) {
                ArrayList<Pair> row = AsciiTable.generateRandomKeyPair(cols);
                for(Pair p : row) {
                    writer.print(p.toString() + " ");
                }
                writer.println();
            }
            System.out.println("New table generated and saved to " + file.getPath());
        } catch(IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }

        System.out.println();
    }

    // Check for existing file or create a new one
    public String checkOrCreateFile(Scanner sc, String[] args) {
        String fileName = null;
        boolean fileLoaded = false;

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
                System.out.print("File not found. Please enter a valid filename: ");
                fileName = sc.nextLine().trim();

                if(!fileName.endsWith(".txt") && !fileName.isEmpty()) {
                    fileName += ".txt";
                }

                // Break if user inputs empty to create new file 
                if(fileName.isEmpty()) break;

                file = getFilePath(fileName);
            }

            if(file.exists()) {
                fileLoaded = true;
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
                fileLoaded = true;
            } else {
                int[] tableDimensions = AsciiTable.getTableDimensions(sc);
                int row = tableDimensions[0];
                int col = tableDimensions[1];

                generateTableAndSave(fileName, row, col);
                fileLoaded = true;
            }
        }

        System.out.println();
        return fileName;
    }

    // Load table from file 
    public boolean loadFromFile(String fileName, ArrayList<ArrayList<Pair>> table) {
        File file = getFilePath(fileName);
        if(!file.exists()) {
            System.out.println("File not found: " + file.getPath());
            return false;
        }

        // Regex pattern to match key-value pairs in the format: (key , value)
        //  \(             match literal '('
        //  (.*?)          capture the key — any characters (non-greedy)
        //  \s,\s          match a comma with one space on each side
        //  (.*?)          capture the value — any characters (non-greedy)
        //  \)             match literal ')'
        //  \s*            match optional whitespace after ')'
        //  (?=\(|$)       ensure the match is followed by '(' or end of string
       Pattern pattern = Pattern.compile("\\((.*?)\\s,\\s(.*?)\\)\\s*(?=\\(|$)");

        // Wrap FileReader in BufferedReader for faster reading
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) { // Close BufferedReader and FileReader after block ends
            String line;
            table.clear();

            while((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher matcher = pattern.matcher(line);
                ArrayList<Pair> row = new ArrayList<>();
                while(matcher.find()) {
                    row.add(new Pair(matcher.group(1), matcher.group(2)));
                }
                table.add(row);
            }

            System.out.println("\nFile loaded successfully from " + file.getPath() + "\n");
            return true;
        } catch(IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    // Save table to file
    public void saveToFile(ArrayList<ArrayList<Pair>> table) {
        if(fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        File file = getFilePath(fileName);

        // Write table data to file
        try (PrintWriter writer = new PrintWriter(file)) { // Close PrintWriter after block ends
            for(ArrayList<Pair> row : table) {
                for(Pair p : row) {
                    writer.print(p.toString() + " ");
                }
                writer.println();
            }
            System.out.println("Table saved to " + file.getPath() + "\n");
        } catch(IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}