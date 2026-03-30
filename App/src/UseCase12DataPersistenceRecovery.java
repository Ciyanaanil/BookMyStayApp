import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void display() {
        System.out.println(reservationId + " | " + guestName + " | " + roomType);
    }
}

// Inventory (Serializable)
class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public Map<String, Integer> getAvailabilityMap() {
        return availability;
    }

    public void display() {
        System.out.println("\nInventory State:");
        for (String type : availability.keySet()) {
            System.out.println(type + ": " + availability.get(type));
        }
    }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> reservations = new ArrayList<>();

    public void addReservation(Reservation r) {
        reservations.add(r);
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : reservations) {
            r.display();
        }
    }
}

// Wrapper for full system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    InventoryService inventory;
    BookingHistory history;

    public SystemState(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // Save state to file
    public static void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
        }
    }

    // Load state from file
    public static SystemState load() {

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("\nSystem state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("\nNo saved state found. Starting fresh...");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\nError loading state. Starting with safe defaults...");
        }

        // Return default safe state
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single", 2);
        inventory.addRoomType("Double", 1);

        BookingHistory history = new BookingHistory();

        return new SystemState(inventory, history);
    }
}

// Main Class
public class UseCase12DataPersistenceRecovery {
    public static void main(String[] args) {

        // Step 1: Load previous state (if exists)
        SystemState state = PersistenceService.load();

        InventoryService inventory = state.inventory;
        BookingHistory history = state.history;

        // Step 2: Simulate system operations
        System.out.println("\n--- System Running ---");

        Reservation r1 = new Reservation("R201", "Arun", "Single");
        Reservation r2 = new Reservation("R202", "Priya", "Double");

        history.addReservation(r1);
        history.addReservation(r2);

        // Step 3: Display current state
        inventory.display();
        history.display();

        // Step 4: Save state before shutdown
        PersistenceService.save(new SystemState(inventory, history));

        System.out.println("\n--- System Shutdown ---");
    }
}