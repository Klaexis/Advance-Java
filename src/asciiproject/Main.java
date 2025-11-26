package asciiproject;

import java.io.File;
import java.util.Scanner;

import asciiproject.util.FileHandler;
import asciiproject.service.TableService;

//This is main
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        String fileName = FileHandler.checkOrCreateFile(sc, args); // Get or create file
        File file = FileHandler.getFilePath(fileName); // Get full file path
        TableService tableService = new TableService(fileName); // Initialize TableService with filename

        if (file.length() == 0) { // Create new table if file is empty
            System.out.println("Creating new table...");
            tableService.createNewTable(sc);
        } else { // Load existing table from file
            tableService.loadTableFromFile(); 
        }
        
        tableService.printTable();

        boolean isRunning = true;
        while(isRunning) {
            System.out.print("[ search ] - Search\n" +
                             "[ edit ] - Edit\n" +
                             "[ add_row ] - Add Row\n" +
                             "[ sort ] - Sort\n" +
                             "[ print ] - Print\n" +
                             "[ reset ] - Reset\n" +
                             "[ x ] - Exit\n" +
                             "Enter the function you want to do: ");

            String choice = sc.nextLine().trim().toLowerCase();
            System.out.println();

            switch(choice) {
                case "search":
                    System.out.println("Searching...");
                    tableService.search(sc);
                    break;

                case "edit":
                    System.out.println("Editing...");
                    tableService.edit(sc);
                    break;

                case "add_row":
                    System.out.println("Adding Row...");
                    tableService.addRow(sc);
                    break;

                case "sort":
                    System.out.println("Sorting...");
                    tableService.sortRow(sc);
                    break;

                case "print":
                    System.out.println("Printing...");
                    tableService.printTable();
                    break;

                case "reset":
                    System.out.println("Resetting...");
                    tableService.resetTable(sc);
                    break;

                case "x":
                    System.out.println("Exiting...");
                    isRunning = false;
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid Input\n");
            }
        }
    }
}