import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        AsciiFileHandler asciiFileHandler = new AsciiFileHandler();
        String fileName = AsciiFileHandler.checkOrCreateFile(sc, args);
        AsciiTable asciiTable = new AsciiTable(asciiFileHandler, fileName);

        asciiFileHandler.setFileName(fileName);
        asciiFileHandler.loadFromFile(fileName, asciiTable.getTable());
        asciiTable.printTable();

        boolean isRunning = true;
        while (isRunning) {
            System.out.print("[ search ] - Search\n" +
                             "[ edit ] - Edit\n" +
                             "[ add_row ] - Add Row\n" +
                             "[ sort ] - Sort\n" +
                             "[ print ] - Print\n" +
                             "[ reset ] - Reset\n" +
                             "[ x ] - Exit\n" +
                             "Enter the function you want to do: ");

            String choice = sc.next().toLowerCase();
            System.out.println();

            switch (choice) {
                case "search":
                    System.out.println("Searching...");
                    asciiTable.search(sc);
                    break;

                case "edit":
                    System.out.println("Editing...");
                    asciiTable.edit(sc);
                    break;

                case "add_row":
                    System.out.println("Adding Row...");
                    asciiTable.addRow(sc);
                    break;

                case "sort":
                    System.out.println("Sorting...");
                    asciiTable.sortRow(sc);
                    break;

                case "print":
                    System.out.println("Printing...");
                    asciiTable.printTable();
                    break;

                case "reset":
                    System.out.println("Resetting...");
                    asciiTable.resetTable(sc);
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