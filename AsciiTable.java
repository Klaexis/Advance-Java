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
import java.util.Comparator;
import java.util.Random;

public class AsciiTable {
	private ArrayList<ArrayList<Pair>> table;
    private String fileName;
	
    // Initialize the ArrayList
    public AsciiTable() {
        table = new ArrayList<>();
    }

    // Set the file name
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

    // Get table dimensions from user
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
    public void saveToFile() {
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

    // Helper method to count substring occurrences
    private static int countOccurrences(String text, String search) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf(search, index)) != -1) {
            count++;
            index += search.length();
        }
        return count;
    }

    // Search for character/s in both key and value of each cell
    public void search(Scanner sc) {
        if (table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        System.out.print("Enter character/s you want to search: ");
        String input = sc.next();

        boolean foundAny = false;

        for (int i = 0; i < table.size(); i++) {
            ArrayList<Pair> row = table.get(i);

            for (int j = 0; j < row.size(); j++) {
                Pair cell = row.get(j);
                String key = cell.getKey();
                String value = cell.getValue();

                int keyCount = countOccurrences(key, input);
                int valueCount = countOccurrences(value, input);

                // If found in key or value or both
                if (keyCount > 0 || valueCount > 0) {
                    foundAny = true;

                    if (keyCount > 0 && valueCount > 0) {
                        System.out.println(
                            keyCount + " <" + input + "> occurrence/s at key and " 
                            + valueCount + " <" + input + "> occurrence/s at value "
                            + "of [" + i + "," + j + "]"
                        );
                    } else if (keyCount > 0) {
                        System.out.println(
                            keyCount + " <" + input + "> occurrence/s at key "
                            + "of [" + i + "," + j + "]"
                        );
                    } else if (valueCount > 0) {
                        System.out.println(
                            valueCount + " <" + input + "> occurrence/s at value "
                            + "of [" + i + "," + j + "]"
                        );
                    }
                }
            }
        }

        if (!foundAny) {
            System.out.println("No occurrences found for \"" + input + "\". \n"); // No occurrences found for "<input>"
        } else {
            System.out.println();
        }

    }

    // Edit the key/value/both of a cell
    public void edit(Scanner sc) {
        if (table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        int row = 0, col = 0; 
        boolean validIndex = false;

        // Get valid cell index from user
        while (!validIndex) {
            System.out.print("Enter cell index to edit (rowxcol, ex. 0x0): ");
            String input = sc.next();

            if (input.contains("x")) {
                String[] parts = input.split("x");
                if (parts.length == 2) {
                    try {
                        row = Integer.parseInt(parts[0]);
                        col = Integer.parseInt(parts[1]);

                        if (row >= 0 && row < table.size() && col >= 0 && col < table.get(row).size()) {
                            validIndex = true;
                        } else {
                            System.out.println("Index out of table bounds. Try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Both row and column must be numbers.");
                    }
                } else {
                    System.out.println("Invalid format. Use rowxcol (e.g., 0x0).");
                }
            } else {
                System.out.println("Invalid format. Use rowxcol (e.g., 0x0).");
            }
        }

        Pair cell = table.get(row).get(col);
        String oldKey = cell.getKey();
        String oldValue = cell.getValue();

        sc.nextLine(); // Consume leftover newline

        // Ask what to edit: key, value, or both
        String choice = "";
        while (!choice.equals("key") && !choice.equals("value") && !choice.equals("both")) {
            System.out.print("Do you want to edit the key, value, or both? (key/value/both): ");
            choice = sc.nextLine().trim().toLowerCase();
            if (!choice.equals("key") && !choice.equals("value") && !choice.equals("both")) {
                System.out.println("Invalid choice. Enter 'key', 'value', or 'both'.");
            }
        }

        String newKey = oldKey;
        String newValue = oldValue;

        // Get new key/value based on choice
        if (choice.equals("key") || choice.equals("both")) {
            System.out.print("Enter new key (leave blank to keep '" + oldKey + "'): ");
            String inputKey = sc.nextLine().trim();
            if (!inputKey.isEmpty()) {
                newKey = inputKey;
            }
        }

        if (choice.equals("value") || choice.equals("both")) {
            System.out.print("Enter new value (leave blank to keep '" + oldValue + "'): ");
            String inputValue = sc.nextLine().trim();
            if (!inputValue.isEmpty()) {
                newValue = inputValue;
            }
        }

        // Update the cell
        cell.setKey(newKey);
        cell.setValue(newValue);

        System.out.println("\nCell updated:");
        System.out.println("Old value -> (" + oldKey + " " + oldValue + ")");
        System.out.println("New value -> (" + newKey + " " + newValue + ")\n");

        saveToFile();
    }

    // Add a new row to the table with random key-value pairs
    public void addRow(Scanner sc) {
        if (table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        int numCells = 0;
        boolean validNumCells = false;

        // Get number of cells for the new row
        while (!validNumCells) {
            System.out.print("Enter the number of cells for the new row: ");
            try {
                numCells = sc.nextInt();
                if (numCells > 0) {
                    validNumCells = true;
                } else {
                    System.out.println("Number of cells must be greater than 0.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // clear invalid input
            }
        }

        int insertRow = -1; // Row number after which to insert
        boolean validRow = false;

        // Get the row number after which to insert the new row
        while (!validRow) {
            System.out.print("Insert the new row after which row? (0 to " + table.size() + ", 0 = before first row): ");
            try {
                insertRow = sc.nextInt();
                if (insertRow >= 0 && insertRow <= table.size()) {
                    validRow = true;
                } else {
                    System.out.println("Row number out of range.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // clear invalid input
            }
        }

        // Generate new row
        ArrayList<Pair> newRow = new ArrayList<>();
        for (int i = 0; i < numCells; i++) {
            String key = generateRandomAscii();
            String value = generateRandomAscii();
            newRow.add(new Pair(key, value));
        }

        // Insert new row at specified position
        int insertIndex;
        if (insertRow == 0) {
            insertIndex = 0;
        } else {
            insertIndex = insertRow;
        }
        table.add(insertIndex, newRow);

        System.out.println("\nNew row added successfully!\n");
        saveToFile();
        printTable();
    }

    // Sort by unicode value a specific row based on user input
    public void sortRow(Scanner sc) {
        if (table.isEmpty()) {
            System.out.println("No data to sort. Please load or generate a table first.\n");
            return;
        }

        int rowIndex = -1;
        String order = null;

        // Ask for row number
        boolean validRow = false;
        while (!validRow) {
            System.out.print("Enter the row number to sort (1-" + table.size() + "): ");
            String input = sc.next();

            try {
                rowIndex = Integer.parseInt(input) - 1; // user inputs 1-based index
                if (rowIndex >= 0 && rowIndex < table.size()) {
                    validRow = true;
                } else {
                    System.out.println("Row number out of range. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        // Ask for sort order
        boolean validOrder = false;
        while (!validOrder) {
            System.out.print("Sort order <asc/desc>: ");
            order = sc.next().toLowerCase();

            if (order.equals("asc") || order.equals("desc")) {
                validOrder = true;
            } else {
                System.out.println("Invalid order. Please enter only 'asc' or 'desc'.");
            }
        }

        ArrayList<Pair> selectedRow = table.get(rowIndex);

        // Make a final copy for use in comparator
        final String sortOrder = order;

        // Sort the selected row
        Comparator<Pair> comparator = new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                String concat1 = p1.getKey() + p1.getValue();
                String concat2 = p2.getKey() + p2.getValue();
                if (sortOrder.equals("asc")) {
                    return concat1.compareTo(concat2);
                } else {
                    return concat2.compareTo(concat1);
                }
            }
        };

        selectedRow.sort(comparator);

        System.out.println("\nRow " + (rowIndex + 1) + " sorted in " + order.toUpperCase() + " order.\n");

        // Save changes and print updated table
        saveToFile();
        printTable();
    }

    // Reset table with new dimensions and new key-value pairs
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