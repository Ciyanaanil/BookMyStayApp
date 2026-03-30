import java.util.*;

// Reservation (Booking Request)
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
    private Map<String, Integer> availability;

    public InventoryService() {
        availability = new HashMap<>();
    }

    public void addRoomType(String type, int count) {
        availability.put(type, count);
    }

    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    // Update inventory after allocation
    public void decrement(String type) {
        availability.put(type, getAvailability(type) - 1);
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll(); // FIFO removal
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Booking Service (Core Allocation Logic)
class BookingService {

    private InventoryService inventory;

    // Track allocated room IDs globally (uniqueness)
    private Set<String> allocatedRoomIds = new HashSet<>();

    // Map room type → assigned room IDs
    private Map<String, Set<String>> roomAllocations = new HashMap<>();

    private int roomCounter = 1; // simple ID generator

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void processRequests(BookingRequestQueue queue) {

        while (!queue.isEmpty()) {

            Reservation request = queue.getNextRequest();

            System.out.println("\nProcessing request for: " + request.getGuestName());

            String type = request.getRoomType();

            // Step 1: Check availability
            if (inventory.getAvailability(type) <= 0) {
                System.out.println("No rooms available for type: " + type);
                continue;
            }

            // Step 2: Generate unique Room ID
            String roomId;
            do {
                roomId = type.substring(0, 1).toUpperCase() + roomCounter++;
            } while (allocatedRoomIds.contains(roomId));

            // Step 3: Assign room (atomic logic)
            allocatedRoomIds.add(roomId);

            roomAllocations.putIfAbsent(type, new HashSet<>());
            roomAllocations.get(type).add(roomId);

            // Step 4: Update inventory immediately
            inventory.decrement(type);

            // Step 5: Confirm reservation
            System.out.println("Booking Confirmed!");
            System.out.println("Guest: " + request.getGuestName());
            System.out.println("Room Type: " + type);
            System.out.println("Assigned Room ID: " + roomId);
            System.out.println("Remaining " + type + " Rooms: " + inventory.getAvailability(type));
        }
    }

    // Display all allocations
    public void displayAllocations() {
        System.out.println("\nFinal Room Allocations:");

        for (String type : roomAllocations.keySet()) {
            System.out.println(type + " Rooms: " + roomAllocations.get(type));
        }
    }
}

// Main Class
public class UseCase6RoomAllocationService {
    public static void main(String[] args) {

        // Step 1: Setup Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Single", 2);
        inventory.addRoomType("Double", 1);
        inventory.addRoomType("Suite", 1);

        // Step 2: Setup Booking Queue (FIFO)
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Arun", "Single", 2));
        queue.addRequest(new Reservation("Priya", "Single", 1));
        queue.addRequest(new Reservation("Rahul", "Single", 3)); // should fail (no rooms)
        queue.addRequest(new Reservation("Neha", "Suite", 1));
        queue.addRequest(new Reservation("Kiran", "Double", 2));

        // Step 3: Process Bookings
        BookingService bookingService = new BookingService(inventory);

        System.out.println("Starting Booking Allocation...\n");
        bookingService.processRequests(queue);

        // Step 4: Show final allocations
        bookingService.displayAllocations();
    }
}