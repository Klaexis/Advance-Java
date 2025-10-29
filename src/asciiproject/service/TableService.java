package asciiproject.service;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import asciiproject.model.Pair;
import asciiproject.model.Row;
import asciiproject.model.Table;
import asciiproject.util.FileHandler;

// TableService to service a table 
public class TableService {
	private Table table;
    private String fileName;
	private static final Random RANDOM = new Random();

    // Initializes a new TableService with an empty table and a given file name.
    public TableService(String fileName) {
        this.table = new Table();
        this.fileName = fileName;
    }

    // Getter Method for table
    public Table getTable() {
        return table;
    }

	// Generate a random 3 character ASCII String
	private static String generateRandomAscii() {
		StringBuilder randomASCII = new StringBuilder(3);
		
		for(int i = 0; i < 3; i++) {
			int ascii = RANDOM.nextInt(94) + 33;
			randomASCII.append((char) ascii);
		}
		
		return randomASCII.toString();
	}

    // Generate a row with random key-value pairs
    private List<Pair> generateRandomKeyPair(int numCells) {
        List<Pair> keyPair = new ArrayList<>();
        for(int i = 0; i < numCells; i++) {
            String key = generateRandomAscii();
            String value = generateRandomAscii();
            keyPair.add(new Pair(key, value));
        }
        return keyPair;
    }

    // Helper method to parse user input in the format rowxcol
    private int[] parseRowColInput(String input) {
        int[] result = {-1, -1};

        if(input.contains("x")) {
            String[] parts = input.split("x");
            if(parts.length == 2) {
                try {
                    result[0] = Integer.parseInt(parts[0]);
                    result[1] = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // Invalid number format — keep default [-1, -1]
                }
            }
        }

        // Returns an int array [row, col]; returns [-1, -1] if invalid
        return result; 
    }

    // Get table dimensions from user
    private int[] getTableDimensions(Scanner sc) {
        int[] dimensions = new int[2];
        boolean validInput = false;

        while(!validInput) {
            System.out.print("Enter the dimension of the table. Please use the format rowxcol (ex. 3x3): ");
            String input = sc.nextLine().trim();

			// Check if input matches pattern like 1x1 or 3x3
			int[] parsed = parseRowColInput(input);

            if (parsed[0] > 0 && parsed[1] > 0) {
                dimensions = parsed;
                validInput = true;
            } else {
                System.out.println("Invalid format. Please use rowxcol (ex. 1x1, 3x3) and ensure both are numbers greater than 0.\n");
            }
        }
        System.out.println();

        return dimensions;
    }

    // Helper method to count substring occurrences
    private int countOccurrences(String text, String search) {
        int count = 0;
        int index = 0;

        while((index = text.indexOf(search, index)) != -1) {
            count++;
            index++; 
        }
        return count;
    }

    // Create a new table with user inputted dimensions
    public void createNewTable(Scanner sc) {
        int[] dims = getTableDimensions(sc);
        int rows = dims[0];
        int cols = dims[1];

        table.clear();
        for (int i = 0; i < rows; i++) {
            table.addRow(new Row(generateRandomKeyPair(cols)));
        }

        saveTable();
    }

    // Saves the current table state to a file
    public void saveTable() {
        List<String> lines = new ArrayList<>();
        for (Row row : table.getRows()) {
            lines.add(row.toString());
        }
        FileHandler.saveText(fileName, lines);
    }

    // Load table data from file
    public boolean loadFromFile() {
        List<String> lines = FileHandler.readText(fileName);
        table.clear();

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

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line); // Create matcher for the line
            List<Pair> cells = new ArrayList<>();
            while (matcher.find()) { // Find all key-value pairs in the line
                cells.add(new Pair(matcher.group(1), matcher.group(2))); // Add Pair to cells list
            }
            if (!cells.isEmpty()) {
                table.addRow(new Row(cells)); // Add new Row to table if cells were found
            }
        }

        if (table.isEmpty()) {
            System.out.println("No valid table data found in file.\n");
            return false;
        }

        System.out.println("File loaded successfully.\n");
        saveTable();
        return true;
    }


    // Check if the key already exists in the table
    private boolean isKeyUnique(String key) {
        for(Row row : table.getRows()) {
            for(Pair pair : row.getCells()) {
                if(pair.getKey().equals(key)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Search for character/s in both key and value of each cell
    public void search(Scanner sc) {
        if(table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        System.out.print("Enter character/s you want to search: ");
        String input = sc.nextLine();

        if(input.isEmpty()) {
            System.out.println("Invalid input. Please enter at least one character.\n");
            return;
        }
        boolean foundAny = false;

         // Iterate through all rows and cells
        for(int i = 0; i < table.size(); i++) {
            Row row = table.getRow(i);
            List<Pair> cells = row.getCells();

            for(int j = 0; j < cells.size(); j++) {
                Pair cell = cells.get(j);
                String key = cell.getKey();
                String value = cell.getValue();

                // Count how many times the search term appears in key and value
                int keyCount = countOccurrences(key, input);
                int valueCount = countOccurrences(value, input);

                // If found in key or value or both
                if(keyCount > 0 || valueCount > 0) {
                    foundAny = true;

                    if(keyCount > 0 && valueCount > 0) {
                        System.out.println(
                            keyCount + " <" + input + "> occurrence/s at key and " 
                            + valueCount + " <" + input + "> occurrence/s at value "
                            + "of [" + i + "," + j + "]"
                        );
                    } else if(keyCount > 0) {
                        System.out.println(
                            keyCount + " <" + input + "> occurrence/s at key "
                            + "of [" + i + "," + j + "]"
                        );
                    } else if(valueCount > 0) {
                        System.out.println(
                            valueCount + " <" + input + "> occurrence/s at value "
                            + "of [" + i + "," + j + "]"
                        );
                    }
                }
            }
        }

        if(!foundAny) {
            System.out.println("No occurrences found for \"" + input + "\". \n"); // No occurrences found for "<input>"
        } else {
            System.out.println();
        }
    }

    // Edit the key/value/both of a cell
    public void edit(Scanner sc) {
        if(table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        int row = 0;
        int col = 0;
        boolean validIndex = false;

        // Get valid cell index from user
        while(!validIndex) {
            System.out.print("Enter cell index to edit (rowxcol, ex. 0x0): ");
            String input = sc.next();

            int[] parsed = parseRowColInput(input);
            row = parsed[0];
            col = parsed[1];

            // Validate input range
            if(row >= 0 && col >= 0 && row < table.size() && col < table.getRow(row).getCells().size()) {
                validIndex = true;
            } else {
                System.out.println("Invalid format or index out of bounds. Use rowxcol (e.g., 0x0).\n");
            }
        }

        Pair cell = table.getRow(row).getCells().get(col);
        String oldKey = cell.getKey();
        String oldValue = cell.getValue();

        sc.nextLine(); // Clear leftover newline

        // Ask what to edit: key, value, or both
        String choice = "";
        while(!choice.equals("key") && !choice.equals("value") && !choice.equals("both")) {
            System.out.print("Do you want to edit the key, value, or both? (key/value/both): ");
            choice = sc.nextLine().trim().toLowerCase();
            if(!choice.equals("key") && !choice.equals("value") && !choice.equals("both")) {
                System.out.println("Invalid choice. Enter 'key', 'value', or 'both'.");
            }
        }

        String newKey = oldKey;
        String newValue = oldValue;

        // Get new key/value based on choice
        if(choice.equals("key") || choice.equals("both")) {
            boolean validKey = false;
            while(!validKey) {
                System.out.print("Enter new key (leave blank to keep '" + oldKey + "'): ");
                String inputKey = sc.nextLine();
                if(!inputKey.isEmpty()) {
                    if(isKeyUnique(inputKey)) { // Check if key is unique
                        newKey = inputKey; 
                        validKey = true;
                    } else {
                        System.out.println("Key already exists. Please enter a unique key.");
                    }
                } else {
                    validKey = true; // Keep old key if input is blank
                }
            }
        }

        // Get new value
        if(choice.equals("value") || choice.equals("both")) {
            System.out.print("Enter new value (leave blank to keep '" + oldValue + "'): ");
            String inputValue = sc.nextLine();
            if(!inputValue.isEmpty()) {
                newValue = inputValue; 
            }
        }

        // Update the cell
        cell.setKey(newKey);
        cell.setValue(newValue);

        System.out.println("\nCell updated:");
        System.out.println("Old value -> (" + oldKey + " , " + oldValue + ")");
        System.out.println("New value -> (" + newKey + " , " + newValue + ")\n");

        saveTable();
    }


    // Add a new row to the table with random key-value pairs
    public void addRow(Scanner sc) {
        if(table.isEmpty()) {
            System.out.println("Table is empty. Please load or generate a table first.\n");
            return;
        }

        int numCells = 0;
        boolean validNumCells = false;

        // Get number of cells for the new row
        while(!validNumCells) {
            System.out.print("Enter the number of cells for the new row: ");
            try {
                numCells = sc.nextInt();
                if(numCells > 0) {
                    validNumCells = true;
                } else {
                    System.out.println("Number of cells must be greater than 0.");
                }
            } catch(Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // clear invalid input
            }
        }

        int insertRow = -1; // Row number after which to insert
        boolean validRow = false;

        // Get the row number after which to insert the new row
        while(!validRow) {
            System.out.print("Insert the new row after which row? (0 to " + table.size() + ", 0 = before first row): ");
            try {
                insertRow = sc.nextInt();
                if(insertRow >= 0 && insertRow <= table.size()) {
                    validRow = true;
                } else {
                    System.out.println("Row number out of range.");
                }
            } catch(Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.next(); // clear invalid input
            }
        }

        // Generate new row
        List<Pair> newCells = generateRandomKeyPair(numCells);

        // Insert new row at specified position
        Row newRow = new Row(newCells);
        table.addRowAt(insertRow, newRow);

        System.out.println("\nNew row added successfully!\n");
        saveTable();
        printTable();
        sc.nextLine(); // Clear newline
    }

    // Sort by unicode value a specific row based on user input
    public void sortRow(Scanner sc) {
        if(table.isEmpty()) {
            System.out.println("No data to sort. Please load or generate a table first.\n");
            return;
        }

        int rowIndex = -1;
        String order = null;

        // Ask for row number
        boolean validRow = false;
        while(!validRow) {
            System.out.print("Enter the row number to sort (1-" + table.size() + "): ");
            String input = sc.nextLine().trim();

            try {
                rowIndex = Integer.parseInt(input) - 1; // user inputs 1-based index
                if(rowIndex >= 0 && rowIndex < table.size()) {
                    validRow = true;
                } else {
                    System.out.println("Row number out of range. Try again.");
                }
            } catch(NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }

        // Ask for sort order
        boolean validOrder = false;
        while(!validOrder) {
            System.out.print("Sort order <asc/desc>: ");
            order = sc.nextLine().trim().toLowerCase();

            if(order.equals("asc") || order.equals("desc")) {
                validOrder = true;
            } else {
                System.out.println("Invalid order. Please enter only 'asc' or 'desc'.");
            }
        }

        Row selectedRow = table.getRow(rowIndex);
        List<Pair> cells = selectedRow.getCells();

        // Make a final copy for use in sort
        final String sortOrder = order;

        // Concatenate key and value and compare based on ASCII Unicode order
        cells.sort((p1, p2) -> {
            String c1 = p1.getKey() + p1.getValue();
            String c2 = p2.getKey() + p2.getValue();
            return sortOrder.equals("asc") ? c1.compareTo(c2) : c2.compareTo(c1);
        });

        System.out.println("\nRow " + (rowIndex + 1) + " sorted in " + order.toUpperCase() + " order.\n");

        // Save changes and print updated table
        saveTable();
        printTable();
    }

    // Reset table with new dimensions and new key-value pairs
    public void resetTable(Scanner sc) {
        if(fileName == null) {
            System.out.println("No file associated with this table.");
            return;
        }

        int[] tableDimensions = getTableDimensions(sc);
        int rows = tableDimensions[0];
        int cols = tableDimensions[1];

        table.clear();
        
        // Generate new table row by row
        for(int i = 0; i < rows; i++) {
            List<Pair> cells = generateRandomKeyPair(cols);
            table.addRow(new Row(cells));
        }

        saveTable();
        printTable();
    }
	
	// Print the table 
    public void printTable() {
        if(table.isEmpty()) {
            System.out.println("Table is empty or not loaded.\n");
            return;
        }

        System.out.println("Table Contents:");
        for(Row row : table.getRows()) {
            System.out.println(row.toString());
        }
        System.out.println();
    }
}