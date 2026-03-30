/**
 * UseCase5BookingRequest
 *
 * Demonstrates centralized room inventory management using HashMap.
 *
 * @author Ciyana
 * @version 5.1
 */
import java.util.*;

// Reservation (Represents booking intent)
class Reservation {
    private String guestName;
    private String roomType;
    private int nights;

    public Reservation(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNights() {
        return nights;
    }

    public void display() {
        System.out.println("Guest: " + guestName);
        System.out.println("Room Type: " + roomType);
        System.out.println("Nights: " + nights);
        System.out.println("-----------------------------");
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Add request to queue
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("Booking request added for " + reservation.getGuestName());
    }

    // View all queued requests (read-only)
    public void viewAllRequests() {
        if (requestQueue.isEmpty()) {
            System.out.println("No booking requests in queue.");
            return;
        }

        System.out.println("\nBooking Requests in Queue (FIFO Order):\n");

        for (Reservation r : requestQueue) {
            r.display();
        }
    }

    // Peek next request (without removing)
    public Reservation peekNextRequest() {
        return requestQueue.peek();
    }

    // Queue size
    public int getQueueSize() {
        return requestQueue.size();
    }
}

// Main Class
public class UseCase5BookingRequestQueue {
    public static void main(String[] args) {

        // Step 1: Initialize Booking Queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();



        // Step 2: Simulate Guest Booking Requests
        Reservation r1 = new Reservation("Arun", "Single", 2);
        Reservation r2 = new Reservation("Priya", "Suite", 1);
        Reservation r3 = new Reservation("Rahul", "Double", 3);

        // Step 3: Add requests to queue (FIFO order)
        System.out.println("Guests are submitting booking requests...\n");

        bookingQueue.addRequest(r1);
        bookingQueue.addRequest(r2);
        bookingQueue.addRequest(r3);

        // Step 4: View all queued requests
        bookingQueue.viewAllRequests();

        // Step 5: Peek next request (without removal)
        System.out.println("\nNext request to be processed (FIFO):\n");

        Reservation next = bookingQueue.peekNextRequest();
        if (next != null) {
            next.display();
        }

        // Step 6: Show queue size
        System.out.println("Total Requests in Queue: " + bookingQueue.getQueueSize());
    }
}