import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AsciiTable app = new AsciiTable();
        String fileName = AsciiTable.checkOrCreateFile(sc, args);

        app.setFileName(fileName);
        app.loadFromFile(fileName);
        app.printTable();

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
                    break;

                case "edit":
                    System.out.println("Editing...");
                    app.edit(sc);
                    break;

                case "add_row":
                    System.out.println("Adding Row...");
                    app.addRow(sc);
                    break;

                case "sort":
                    System.out.println("Sorting...");
                    app.sortRow(sc);
                    break;

                case "print":
                    System.out.println("Printing...");
                    app.printTable();
                    break;

                case "reset":
                    System.out.println("Resetting...");
                    app.resetTable(sc);
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