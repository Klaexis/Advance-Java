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
    private static File getFolder() {
        File folder = new File(FOLDER_NAME);
        if(!folder.exists()) {
            folder.mkdir(); // create folder if it doesn’t exist
            System.out.println("Created folder: " + folder.getAbsolutePath());
        }
        return folder;
    }

    // Get the full file path inside the "text files" directory
    public static File getFilePath(String fileName) {
        return new File(getFolder(), fileName);
    }

    // Create file if not existing
    public static void createFile(File file) {
        try {
            if (file.createNewFile()) {
                System.out.println("Created new file: " + file.getPath());
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public static String checkOrCreateFile(Scanner sc, String[] args) {
        getFolder();
        String fileName = null;
        File file = null;

        // If filename is given via command-line argument
        if (args.length > 0 && !args[0].trim().isEmpty()) {
            fileName = args[0].trim();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            file = getFilePath(fileName);

            // If file doesn't exist then ask user for a valid one or blank for new
            if (!file.exists()) {
                System.out.println("File '" + fileName + "' not found.");

                while (true) {
                    System.out.print("Enter a valid filename (or leave blank to create a new file): ");
                    String input = sc.nextLine().trim();

                    if (input.isEmpty()) {
                        // If blank create new file 
                        System.out.print("Enter new filename: ");
                        fileName = sc.nextLine().trim();
                        if (!fileName.endsWith(".txt")) {
                            fileName += ".txt";
                        }
                        file = getFilePath(fileName);
                        createFile(file);
                        break;
                    } else {
                        // Check if the entered file exists
                        if (!input.endsWith(".txt")) {
                            input += ".txt";
                        }
                        file = getFilePath(input);
                        if (file.exists()) {
                            fileName = input;
                            System.out.println("Found existing file: " + file.getPath());
                            break;
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
            fileName = sc.nextLine().trim();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            file = getFilePath(fileName);
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
        File file = getFilePath(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String line : lines) {
                writer.println(line);
            }
            System.out.println("Saved to " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

     // Read lines from text file
    public static List<String> readText(String fileName) {
        File file = getFilePath(fileName);
        List<String> lines = new ArrayList<>();

        if (!file.exists()) {
            System.out.println("File not found: " + file.getPath());
            return lines;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return lines;
    }
}