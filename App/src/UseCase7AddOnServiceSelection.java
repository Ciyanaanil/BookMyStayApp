import java.util.*;

// Add-On Service (Domain Object)
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    public void display() {
        System.out.println(serviceName + " - ₹" + cost);
    }
}

// Reservation (Minimal representation with ID)
class Reservation {
    private String reservationId;
    private String guestName;

    public Reservation(String reservationId, String guestName) {
        this.reservationId = reservationId;
        this.guestName = guestName;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }
}

// Add-On Service Manager
class AddOnServiceManager {

    // Map: Reservation ID → List of Services
    private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {

        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);

        System.out.println("Added service: " + service.getServiceName()
                + " to Reservation: " + reservationId);
    }

    // View services for a reservation
    public void viewServices(String reservationId) {

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services for Reservation: " + reservationId);
            return;
        }

        System.out.println("\nServices for Reservation: " + reservationId);

        for (AddOnService service : services) {
            service.display();
        }
    }

    // Calculate total add-on cost
    public double calculateTotalCost(String reservationId) {

        List<AddOnService> services = serviceMap.get(reservationId);

        if (services == null) return 0;

        double total = 0;

        for (AddOnService service : services) {
            total += service.getCost();
        }

        return total;
    }
}
// Main Class
public class UseCase7AddOnServiceSelection {
    public static void main(String[] args) {

        // Step 1: Create Reservations (already confirmed in UC6)
        Reservation r1 = new Reservation("R101", "Arun");
        Reservation r2 = new Reservation("R102", "Priya");

        // Step 2: Create Add-On Services
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService wifi = new AddOnService("Premium WiFi", 200);
        AddOnService spa = new AddOnService("Spa Access", 1500);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 800);

        // Step 3: Service Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Step 4: Guest selects services
        System.out.println("Guests selecting add-on services...\n");

        manager.addService(r1.getReservationId(), breakfast);
        manager.addService(r1.getReservationId(), wifi);

        manager.addService(r2.getReservationId(), spa);
        manager.addService(r2.getReservationId(), airportPickup);
        manager.addService(r2.getReservationId(), breakfast);

        // Step 5: View services
        manager.viewServices(r1.getReservationId());
        manager.viewServices(r2.getReservationId());

        // Step 6: Calculate total cost
        System.out.println("\nTotal Add-On Cost:");

        System.out.println("Reservation " + r1.getReservationId() +
                ": ₹" + manager.calculateTotalCost(r1.getReservationId()));

        System.out.println("Reservation " + r2.getReservationId() +
                ": ₹" + manager.calculateTotalCost(r2.getReservationId()));
    }
}