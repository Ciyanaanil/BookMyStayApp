import java.util.*;

// Custom Exception for Booking Errors
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation (Input Model)
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
}

// Inventory Service (State Holder)
class InventoryService {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public boolean isValidRoomType(String type) {
        return availability.containsKey(type);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public void decrement(String type) throws InvalidBookingException {
        int current = getAvailability(type);

        if (current <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + type);
        }

        availability.put(type, current - 1);
    }
}

// Validator (Fail-Fast)
class BookingValidator {

    public static void validate(Reservation reservation, InventoryService inventory)
            throws InvalidBookingException {

        // Validate guest name
        if (reservation.getGuestName() == null || reservation.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(reservation.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
        }

        // Validate nights
        if (reservation.getNights() <= 0) {
            throw new InvalidBookingException("Number of nights must be greater than zero.");
        }

        // Validate availability (guard system state)
        if (inventory.getAvailability(reservation.getRoomType()) <= 0) {
            throw new InvalidBookingException(
                    "Room not available for type: " + reservation.getRoomType());
        }
    }
}

// Booking Service (Handles allocation safely)
class BookingService {

    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void confirmBooking(Reservation reservation) {
        try {
            // Step 1: Validate input (Fail-Fast)
            BookingValidator.validate(reservation, inventory);

            // Step 2: Perform allocation safely
            inventory.decrement(reservation.getRoomType());

            // Step 3: Confirm booking
            System.out.println("\nBooking Confirmed!");
            System.out.println("Guest: " + reservation.getGuestName());
            System.out.println("Room Type: " + reservation.getRoomType());
            System.out.println("Nights: " + reservation.getNights());
            System.out.println("Remaining Rooms: "
                    + inventory.getAvailability(reservation.getRoomType()));

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("\nBooking Failed: " + e.getMessage());
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single", 1);
        inventory.addRoomType("Double", 0);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Test Cases (Valid + Invalid)

        Reservation valid = new Reservation("Arun", "Single", 2);
        Reservation invalidRoom = new Reservation("Priya", "Suite", 1);
        Reservation noAvailability = new Reservation("Rahul", "Double", 1);
        Reservation invalidNights = new Reservation("Neha", "Single", 0);
        Reservation emptyName = new Reservation("", "Single", 1);

        // Step 4: Process bookings
        bookingService.confirmBooking(valid);
        bookingService.confirmBooking(invalidRoom);
        bookingService.confirmBooking(noAvailability);
        bookingService.confirmBooking(invalidNights);
        bookingService.confirmBooking(emptyName);
    }
}