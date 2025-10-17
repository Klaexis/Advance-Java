import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Random;

public class AsciiTable {
	private LinkedHashMap<String, String> table;
	
    // Initialize the LinkedHashMap
    public AsciiTable() {
        table = new LinkedHashMap<>();
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

    private static String checkOrCreateFile(AsciiTable app, Scanner sc, String[] args) {
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
                fileLoaded = app.loadFromFile(fileName);
            }
        }

        // If no valid file found or argument missing, create new one
        if (fileName == null || fileName.trim().isEmpty()) {
            System.out.print("Enter a new file name to create: ");
            fileName = sc.nextLine().trim();

            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            System.out.print("Enter number of rows: ");
            int rows = sc.nextInt();

            System.out.print("Enter number of columns: ");
            int cols = sc.nextInt();

            generateAndSave(fileName, rows, cols);
            fileLoaded = app.loadFromFile(fileName);
        }

        if (!fileLoaded) {
            System.out.println("Failed to load file.");
        }

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
            System.out.println("New table generated and saved to " + fileName + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
	
    // Read the key-value pairs from file and store in table
    public boolean loadFromFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found: " + fileName);
            return false;
        }

        table.clear(); 

        Pattern pattern = Pattern.compile("\\(([^\\s]+)\\s([^\\s]+)\\)");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    table.put(matcher.group(1), matcher.group(2));
                }
            }
            System.out.println("\nFile loaded successfully!\n");
            return true;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }
    }
	
	// Print the table 
    public void printTable(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found.\n");
            return;
        }

        System.out.println("Generated ASCII Table:");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
        AsciiTable app = new AsciiTable();
        String fileName = checkOrCreateFile(app, sc, args);

        app.printTable(fileName);
		
		boolean isRunning = true;
		while(isRunning) {
			System.out.print("[ search ] - Search\n"+
							 "[ edit ] - Edit\n"+
							 "[ add_row ] - Add Row\n"+
							 "[ print ] - Print\n"+
                             "[ reset ] - Reset\n"+
							 "[ x ] - Exit\n"+
							 "Enter the function you want to do: ");
							 
			String choice = sc.next().toLowerCase();

			System.out.println();

			switch(choice) {
				case "search": //Search
					System.out.println("Searching...");
					break;
				case "edit": //Edit
					System.out.println("Editing...");
					break;
				case "add_row": //Add Row
                    System.out.println("Adding Row...");
					break;
				case "print": //Print
					System.out.println("Printing...");
                    app.printTable(fileName);
					break;
                case "reset": //Reset
					System.out.println("Resetting...");
					break;
				case "x": //Exit
					System.out.println("Exiting...");
					isRunning = false;
					sc.close();
					System.exit(0); 
					break;
				default: //Invalid Input
					System.out.println("Invalid Input\n");
			}
		}
	}
}