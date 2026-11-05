import java.io.IOException;
import java.util.Scanner;

/** Console controls for ticket issuing and three help desks. */
public class Main {
    public static void main(String[] args) {
        QueueManager queue = QueueManager.getInstance();
        HelpDesk[] desks = {new HelpDesk(1), new HelpDesk(2), new HelpDesk(3)};
        OnlineMonitor monitor = new OnlineMonitor();

        try {
            monitor.start(8080);
            System.out.println("Browser queue monitor: http://localhost:8080");
        } catch (IOException e) {
            System.out.println("Monitor could not start on port 8080: " + e.getMessage());
            System.out.println("The console queue will still work.");
        }

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n=== PAG-IBIG CENTRALIZED QUEUE SYSTEM ===");
                System.out.println("1. Issue visitor ticket");
                System.out.println("2. Help Desk 1: Serve next");
                System.out.println("3. Help Desk 2: Serve next");
                System.out.println("4. Help Desk 3: Serve next");
                System.out.println("5. Help Desk 1: Reset queue");
                System.out.println("6. Help Desk 2: Reset queue");
                System.out.println("7. Help Desk 3: Reset queue");
                System.out.println("8. Display queue status");
                System.out.println("9. Verify Singleton instance");
                System.out.println("0. Exit");
                System.out.print("Choose: ");

                try {
                    int choice = Integer.parseInt(scanner.nextLine().trim());
                    if (choice == 0) {
                        break;
                    } else if (choice == 1) {
                        System.out.println("Visitor received ticket #" + queue.issueTicket());
                    } else if (choice >= 2 && choice <= 4) {
                        desks[choice - 2].serveNextVisitor();
                    } else if (choice >= 5 && choice <= 7) {
                        System.out.print("Reset queue to number: ");
                        int number = Integer.parseInt(scanner.nextLine().trim());
                        desks[choice - 5].resetQueue(number);
                    } else if (choice == 8) {
                        System.out.println("Currently serving: " + queue.getCurrentServingNumber());
                        System.out.println("Last ticket issued: " + queue.getLastIssuedNumber());
                    } else if (choice == 9) {
                        System.out.println("Same Singleton object: "
                                + (queue == QueueManager.getInstance()));
                    } else {
                        System.out.println("Invalid menu option.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid whole number.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            monitor.stop();
        }
        System.out.println("Goodbye!");
    }
}
