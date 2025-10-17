import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class AsciiTable {
	private ArrayList<ArrayList<Pair>> table;
    private String fileName;
	
    // Initialize the ArrayList
    public AsciiTable() {
        table = new ArrayList<>();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
	//Generate a random 3 character ASCII String
	private static String generateRandomAscii() {
		Random random = new Random();
		StringBuilder randomASCII = new StringBuilder(3);
		
		for(int i = 0; i < 3; i++) {
			int ascii = random.nextInt(94) + 33;
			randomASCII.append((char) ascii);
		}
		
		return randomASCII.toString();
	}

    private static int[] getTableDimensions(Scanner sc) {
        int[] dimensions = new int[2];
        boolean validInput = false;

        while (!validInput) {
            System.out.print("Enter the dimension of the table. Please use the format rowxcol (ex. 3x3): ");
            String input = sc.next();

			// Check if input matches pattern like 1x1 or 3x3
			if (input.contains("x")) {
				String[] parts = input.split("x");

				if (parts.length == 2) {
					try {
						int row = Integer.parseInt(parts[0]);
						int col = Integer.parseInt(parts[1]);

						if (row > 0 && col > 0) {
                            dimensions[0] = row;
                            dimensions[1] = col;
                            validInput = true;
						} else {
							System.out.println("Rows and columns must be greater than 0.");
						}
					} catch (NumberFormatException e) {
						System.out.println("Both row and column must be valid numbers.");
					}
				} else {
					System.out.println("Invalid format. Please use the format rowxcol (ex. 1x1, 3x3) with lowercase 'x'.");
				}
			} else {
				System.out.println("Invalid format. Please use the format rowxcol (ex. 1x1, 3x3) with lowercase 'x'.");
			}
        }
        System.out.println();

        return dimensions;
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

            int[] tableDimensions = getTableDimensions(sc);
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
                for (int j = 0; j < cols; j++) {
                    String key = generateRandomAscii();
                    String value = generateRandomAscii();
                    writer.printf("(%s %s) ", key, value);
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
    public boolean loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return false;
        }

        Pattern pattern = Pattern.compile("\\(([^\\s]+)\\s([^)]*)\\)");

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
    public void saveToFile() {
        if (fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
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

    public void resetTable(Scanner sc) {
        if (fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        int[] tableDimensions = getTableDimensions(sc);
        int rows = tableDimensions[0];
        int cols = tableDimensions[1];

        table.clear();

        for (int i = 0; i < rows; i++) {
            ArrayList<Pair> rowList = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                String key = generateRandomAscii();
                String value = generateRandomAscii();
                rowList.add(new Pair(key, value));
            }
            table.add(rowList);
        }

        saveToFile();
        printTable();
    }
	
	// Print the table 
    public void printTable() {
        if (table.isEmpty()) {
            System.out.println("Table is empty or not loaded.\n");
            return;
        }

        System.out.println("Table Contents:");
        for (ArrayList<Pair> row : table) {
            for (Pair p : row) {
                System.out.print(p.toString() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}