/**
 * UseCase4RoomSearch
 *
 * Demonstrates centralized room inventory management using HashMap.
 *
 * @author YourName
 * @version 4.1
 */
import java.util.*;

class Room {
    private String type;
    private double price;
    private List<String> amenities;

    public Room(String type, double price, List<String> amenities) {
        this.type = type;
        this.price = price;
        this.amenities = amenities;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Price: ₹" + price);
        System.out.println("Amenities: " + String.join(", ", amenities));
        System.out.println("-----------------------------");
    }
}

class Inventory {
    private Map<String, Integer> roomAvailability;

    public Inventory() {
        roomAvailability = new HashMap<>();
    }

    public void addRoom(String type, int count) {
        roomAvailability.put(type, count);
    }

    public int getAvailability(String type) {
        return roomAvailability.getOrDefault(type, 0);
    }

    public Set<String> getAllRoomTypes() {
        return roomAvailability.keySet();
    }
}

class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomCatalog;

    public SearchService(Inventory inventory, Map<String, Room> roomCatalog) {
        this.inventory = inventory;
        this.roomCatalog = roomCatalog;
    }

    public void searchAvailableRooms() {
        System.out.println("Available Rooms:\n");

        for (String type : inventory.getAllRoomTypes()) {

            int availableCount = inventory.getAvailability(type);

            if (availableCount > 0 && roomCatalog.containsKey(type)) {
                Room room = roomCatalog.get(type);

                room.displayDetails();
                System.out.println("Available Count: " + availableCount);
                System.out.println("=============================");
            }
        }
    }
}

public class UseCase4RoomSearch {
    public static void main(String[] args) {

        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 5);
        inventory.addRoom("Double", 0);  // Should be filtered
        inventory.addRoom("Suite", 2);

        Map<String, Room> roomCatalog = new HashMap<>();

        roomCatalog.put("Single", new Room(
                "Single",
                2000,
                Arrays.asList("WiFi", "TV", "AC")
        ));

        roomCatalog.put("Double", new Room(
                "Double",
                3500,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar")
        ));

        roomCatalog.put("Suite", new Room(
                "Suite",
                6000,
                Arrays.asList("WiFi", "TV", "AC", "Mini Bar", "Jacuzzi")
        ));


        SearchService searchService = new SearchService(inventory, roomCatalog);


        System.out.println("Guest is searching for available rooms...\n");
        searchService.searchAvailableRooms();
    }
}