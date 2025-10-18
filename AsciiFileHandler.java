import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsciiFileHandler {
    private String fileName;

    // Set the file name
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Check for existing file or create a new one
    public static String checkOrCreateFile(Scanner sc, String[] args) {
        String fileName = null;
        boolean fileLoaded = false;

        // If the file name was provided as argument
        if (args.length > 0 && !args[0].trim().isEmpty()) {
            fileName = args[0].trim();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            File file = new File(fileName);

            // If the file name was invalid (non-existing) ask user to input valid file name
            while (!file.exists()) {
                System.out.print("File not found. Please enter a valid filename: ");
                fileName = sc.nextLine().trim();

                if (!fileName.endsWith(".txt") && !fileName.isEmpty()) {
                    fileName += ".txt";
                }

                // If user enters blank, break to create a new file instead
                if (fileName.isEmpty()) break;

                file = new File(fileName);
            }

            // If file exists, load its content
            if (file.exists()) {
                fileLoaded = true;
            }
        }

        // If no valid file found or argument missing, create new one
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.print("Enter a new file name to create: ");
            fileName = sc.nextLine().trim();

            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            int[] tableDimensions = AsciiTable.getTableDimensions(sc);
            int row = tableDimensions[0];
            int col = tableDimensions[1];

            generateAndSave(fileName, row, col);
            fileLoaded = true;
        }

        if (!fileLoaded) {
            System.out.println("Failed to load file.");
        }
        System.out.println();

        return fileName;
    }

    // Generate random table data and save to file
    public static void generateAndSave(String fileName, int rows, int cols) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (int i = 0; i < rows; i++) {
                ArrayList<Pair> row = AsciiTable.generateRandomKeyPair(cols);
                for (Pair p : row) {
                    writer.print(p.toString() + " ");
                }
                writer.println();
            }
            System.out.println("New table generated and saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
        System.out.println();
    }
	
    // Read the key-value pairs from file and store in table
    public boolean loadFromFile(String fileName, ArrayList<ArrayList<Pair>> table) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return false;
        }

        // Regex pattern to match key-value pairs in the format: (key value)
        //  \(        match literal '('
        //  ([^\\s]+) capture the key (any non-space characters)
        //  \\s       match a space between key and value
        //  (.*?)     capture the value (non-greedy, allows ')' inside value)
        //  \\)       match literal ')'
        //  (?=\\s|$) ensure ')' is followed by a space or end of line (end of pair)
        Pattern pattern = Pattern.compile("\\(([^\\s]+)\\s(.*?)\\)(?=\\s|$)");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            table.clear();
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                ArrayList<Pair> row = new ArrayList<>();
                while (matcher.find()) {
                    row.add(new Pair(matcher.group(1), matcher.group(2)));
                }
                table.add(row);
            }
            System.out.println("\nFile loaded successfully!\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }

    // Save the current table back to file
    public void saveToFile(ArrayList<ArrayList<Pair>> table) {
        if (fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) { // Overwrite existing file
            for (ArrayList<Pair> row : table) {
                for (Pair p : row) {
                    writer.print(p.toString() + " ");
                }
                writer.println();
            }
            System.out.println("Table saved to " + fileName + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}