package asciiproject.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Can be used by all, independent
public class FileHandler {
    private static final String FOLDER_NAME = "text files"; // Folder for all .txt files

    // Check if folder exists, if not create it
    public static File getFolder() {
        File folder = new File(FOLDER_NAME);

        // Create folder if it doesn't exist
        if(!folder.exists()) {
            folder.mkdir(); 
            System.out.println("Created folder: " + folder.getAbsolutePath());
        }
        return folder;
    }

    // Get the full file path inside the "text files" directory
    public static File getFilePath(String fileName) {
        return new File(getFolder(), fileName); // Combine folder path with filename
    }

    // Helper method to ensure .txt extension
    public static String ensureTxtExtension(String fileName) {
        if (!fileName.toLowerCase().endsWith(".txt")) {
            return fileName + ".txt";
        }
        return fileName;
    }

    // Create file if not existing
    public static void createFile(File file) {
        try {
            // Create the file
            if (file.createNewFile()) {
                System.out.println("Created new file: " + file.getPath());
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    // Check for existing file or create new one
    public static String checkOrCreateFile(Scanner sc, String[] args) {
        getFolder();
        String fileName = null;
        File file = null;

        // If filename is given via command-line argument
        if (args.length > 0 && !args[0].trim().isEmpty()) {
            fileName = ensureTxtExtension(args[0].trim()); // Ensure .txt extension

            file = getFilePath(fileName); // Get full file path

            // If file doesn't exist then ask user for a valid one or blank for new
            if (!file.exists()) {
                System.out.println("File '" + fileName + "' not found.");

                boolean validInput = false;

                // Loop until valid input is given
                while (!validInput) {
                    System.out.print("Enter a valid filename (or leave blank to create a new file): ");
                    String input = sc.nextLine().trim();

                    if (input.isEmpty()) {
                        // If blank create new file 
                        System.out.print("Enter new filename: ");

                        fileName = ensureTxtExtension(sc.nextLine().trim()); // Ensure .txt extension
                        file = getFilePath(fileName); // Get full file path

                        createFile(file); // Create the new file
                        validInput = true;
                    } else {
                        // Check if the entered file exists
                        input = ensureTxtExtension(input); // Ensure .txt extension
                        file = getFilePath(input); // Get full file path

                        // If file exists, load it
                        if (file.exists()) {
                            fileName = input;
                            System.out.println("Found existing file: " + file.getPath());
                            validInput = true;
                        } else {
                            System.out.println("File not found. Try again.");
                        }
                    }
                }
            } else {
                System.out.println("Found existing file: " + file.getPath());
            }
        } else {
            // No CLI argument then ask for new filename
            System.out.print("Enter new filename: ");
            fileName = ensureTxtExtension(sc.nextLine().trim()); // Ensure .txt extension

            file = getFilePath(fileName); // Get full file path

            // Create the file if it doesn't exist
            if (!file.exists()) {
                createFile(file);
            } else {
                System.out.println("Found existing file: " + file.getPath());
            }
        }

        System.out.println();
        return fileName;
    }

    // Save lines to text file
    public static void saveText(String fileName, List<String> lines) {
        File file = getFilePath(fileName); // Get full file path

        // Write lines to the file
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line); // Write each line to the file
            }
            System.out.println("Saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    // Read lines from text file
    public static List<String> readText(String fileName) {
        File file = getFilePath(fileName); // Get full file path
        List<String> lines = new ArrayList<>(); // List to store lines

        // Check if file exists
        if (!file.exists()) {
            System.out.println("File not found: " + file.getPath());
            return lines;
        }

        // Read lines from the file
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            // Read each line and add to the list
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return lines;
    }
}