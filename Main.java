// import java.util.Scanner;

// public class Main {
//     public static void main(String[] args) {
// 		AsciiTable app = new AsciiTable();
		
// 		Scanner sc = new Scanner(System.in);
		
// 		app.generateTable(sc);
// 		app.print();
		
// 		boolean isRunning = true;
// 		while(isRunning) {
// 			System.out.print("1 - Search\n"+
// 							 "2 - Edit\n"+
// 							 "3 - Print\n"+
// 							 "4 - Reset\n"+
// 							 "x - Exit\n"+
// 							 "Enter the function you want to do: ");
							 
// 			String choice = sc.next();
// 			choice = choice.toLowerCase();

// 			System.out.println();

// 			switch(choice) {
// 				case "1": //Search
// 					System.out.println("Searching...");
// 					app.search(sc);
// 					break;
// 				case "2": //Edit
// 					System.out.println("Editing...");
// 					app.edit(sc);
// 					break;
// 				case "3": //Print
// 					System.out.println("Printing...");
// 					app.print();
// 					break;
// 				case "4": //Reset
// 					System.out.println("Resetting...");
// 					app.generateTable(sc);
// 					app.print();
// 					break;
// 				case "x": //Exit
// 					System.out.println("Exiting...");
// 					isRunning = false;
// 					sc.close();
// 					System.exit(0); 
// 					break;
// 				default: //Invalid Input
// 					System.out.println("Invalid Input\n");
// 			}
// 		}
// 	}
// }
