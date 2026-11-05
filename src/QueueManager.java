/**
 * Singleton: one shared queue for all three Pag-IBIG help desks.
 */
public final class QueueManager {
    private static final QueueManager INSTANCE = new QueueManager();

    private int lastIssuedNumber = 0;
    private int currentServingNumber = 0;

    private QueueManager() {
        // Private constructor: no other class can create a QueueManager.
    }

    public static QueueManager getInstance() {
        return INSTANCE;
    }

    /** Gives a new, sequential ticket to a visitor. */
    public synchronized int issueTicket() {
        return ++lastIssuedNumber;
    }

    /** Calls the next waiting visitor. Returns -1 if the queue is empty. */
    public synchronized int callNext() {
        if (currentServingNumber >= lastIssuedNumber) {
            return -1;
        }
        return ++currentServingNumber;
    }

    /**
     * Resets the queue to a chosen number. The next ticket will be number + 1.
     */
    public synchronized void resetQueue(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Queue number cannot be negative.");
        }
        lastIssuedNumber = number;
        currentServingNumber = number;
    }

    /** Status is read atomically so the monitor gets consistent numbers. */
    public synchronized String getStatusJson() {
        return "{\"currentServing\":" + currentServingNumber
                + ",\"lastIssued\":" + lastIssuedNumber
                + ",\"waiting\":" + (lastIssuedNumber - currentServingNumber) + "}";
    }

    public synchronized int getCurrentServingNumber() {
        return currentServingNumber;
    }

    public synchronized int getLastIssuedNumber() {
        return lastIssuedNumber;
    }
}
