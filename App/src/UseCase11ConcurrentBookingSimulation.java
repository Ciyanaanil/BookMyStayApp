import java.util.*;

// Reservation (Booking Request)
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Thread-safe Booking Queue
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    // synchronized add
    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
        notifyAll(); // notify waiting threads
    }

    // synchronized retrieval
    public synchronized Reservation getRequest() {
        while (queue.isEmpty()) {
            try {
                wait(); // wait for requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return queue.poll();
    }
}

// Inventory (Shared Resource)
class InventoryService {
    private Map<String, Integer> availability = new HashMap<>();

    public InventoryService() {
        availability.put("Single", 2);
        availability.put("Double", 1);
    }

    // synchronized critical section
    public synchronized boolean allocateRoom(String type) {

        int available = availability.getOrDefault(type, 0);

        if (available > 0) {
            availability.put(type, available - 1);
            return true;
        }
        return false;
    }

    public synchronized int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }
}

// Booking Processor (Runnable Thread)
class BookingProcessor implements Runnable {

    private BookingQueue queue;
    private InventoryService inventory;

    public BookingProcessor(BookingQueue queue, InventoryService inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {

            Reservation request = queue.getRequest();

            // Critical section: allocation
            synchronized (inventory) {

                boolean success = inventory.allocateRoom(request.getRoomType());

                if (success) {
                    System.out.println(Thread.currentThread().getName()
                            + " CONFIRMED booking for "
                            + request.getGuestName()
                            + " (" + request.getRoomType() + ")");
                } else {
                    System.out.println(Thread.currentThread().getName()
                            + " FAILED booking for "
                            + request.getGuestName()
                            + " (" + request.getRoomType() + ")");
                }
            }

            // Exit condition for demo (avoid infinite loop)
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
    }
}

// Main Class
public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {

        // Shared resources
        BookingQueue queue = new BookingQueue();
        InventoryService inventory = new InventoryService();

        // Create worker threads
        Thread t1 = new Thread(new BookingProcessor(queue, inventory), "Thread-1");
        Thread t2 = new Thread(new BookingProcessor(queue, inventory), "Thread-2");

        t1.start();
        t2.start();

        // Simulate multiple guests submitting requests simultaneously
        System.out.println("Submitting concurrent booking requests...\n");

        queue.addRequest(new Reservation("Arun", "Single"));
        queue.addRequest(new Reservation("Priya", "Single"));
        queue.addRequest(new Reservation("Rahul", "Single")); // should fail (only 2 available)
        queue.addRequest(new Reservation("Neha", "Double"));
        queue.addRequest(new Reservation("Kiran", "Double")); // should fail

        // Allow threads to process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop threads (for demo purposes)
        t1.interrupt();
        t2.interrupt();

        System.out.println("\nFinal Availability:");
        System.out.println("Single: " + inventory.getAvailability("Single"));
        System.out.println("Double: " + inventory.getAvailability("Double"));
    }
}