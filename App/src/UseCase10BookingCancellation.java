import java.util.*;

// Reservation (Confirmed Booking)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String reservationId, String guestName,
                       String roomType, String roomId) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancel() {
        isCancelled = true;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId +
                " | Guest: " + guestName +
                " | Room: " + roomId +
                " | Type: " + roomType +
                " | Status: " + (isCancelled ? "Cancelled" : "Active"));
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void increment(String type) {
        availability.put(type, getAvailability(type) + 1);
    }
}

// Booking History
class BookingHistory {
    private Map<String, Reservation> reservations = new HashMap<>();

    public void addReservation(Reservation r) {
        reservations.put(r.getReservationId(), r);
    }

    public Reservation getReservation(String id) {
        return reservations.get(id);
    }

    public void displayAll() {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : reservations.values()) {
            r.display();
        }
    }
}

// Cancellation Service (Core Logic)
class CancellationService {

    private InventoryService inventory;
    private BookingHistory history;

    // Stack for rollback tracking (LIFO)
    private Stack<String> releasedRoomStack = new Stack<>();

    public CancellationService(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelBooking(String reservationId) {

        System.out.println("\nProcessing cancellation for: " + reservationId);

        // Step 1: Validate reservation existence
        Reservation reservation = history.getReservation(reservationId);

        if (reservation == null) {
            System.out.println("Cancellation Failed: Reservation not found.");
            return;
        }

        // Step 2: Prevent duplicate cancellation
        if (reservation.isCancelled()) {
            System.out.println("Cancellation Failed: Already cancelled.");
            return;
        }

        // Step 3: Rollback logic (controlled mutation)
        String roomId = reservation.getRoomId();
        String roomType = reservation.getRoomType();

        // Push to stack (LIFO rollback tracking)
        releasedRoomStack.push(roomId);

        // Restore inventory
        inventory.increment(roomType);

        // Mark reservation cancelled
        reservation.cancel();

        // Step 4: Confirmation
        System.out.println("Cancellation Successful!");
        System.out.println("Released Room ID: " + roomId);
        System.out.println("Updated Availability (" + roomType + "): "
                + inventory.getAvailability(roomType));
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recently Released Rooms): " + releasedRoomStack);
    }
}

// Main Class
public class UseCase10BookingCancellation {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single", 0);
        inventory.addRoomType("Double", 0);

        // Step 2: Booking History (Simulating confirmed bookings)
        BookingHistory history = new BookingHistory();

        Reservation r1 = new Reservation("R101", "Arun", "Single", "S1");
        Reservation r2 = new Reservation("R102", "Priya", "Double", "D1");

        history.addReservation(r1);
        history.addReservation(r2);

        // Step 3: Cancellation Service
        CancellationService cancellationService =
                new CancellationService(inventory, history);

        // Step 4: Perform cancellations
        cancellationService.cancelBooking("R101"); // valid
        cancellationService.cancelBooking("R101"); // duplicate
        cancellationService.cancelBooking("R999"); // invalid

        // Step 5: View system state
        history.displayAll();
        cancellationService.showRollbackStack();
    }
}