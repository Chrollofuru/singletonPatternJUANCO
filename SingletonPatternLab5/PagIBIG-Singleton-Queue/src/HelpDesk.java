/** Each help desk accesses the same QueueManager Singleton. */
public class HelpDesk {
    private final int deskNumber;
    private final QueueManager queue = QueueManager.getInstance();

    public HelpDesk(int deskNumber) {
        this.deskNumber = deskNumber;
    }

    public void serveNextVisitor() {
        int ticket = queue.callNext();
        if (ticket == -1) {
            System.out.println("Desk " + deskNumber + ": No visitors waiting.");
        } else {
            System.out.println("Desk " + deskNumber + " is now serving ticket #" + ticket);
        }
    }

    public void resetQueue(int number) {
        queue.resetQueue(number);
        System.out.println("Desk " + deskNumber + " reset the queue to " + number
                + ". The next issued ticket will be #" + (number + 1));
    }
}
