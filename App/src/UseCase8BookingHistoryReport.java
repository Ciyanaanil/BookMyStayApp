import java.util.*;

// Reservation (Confirmed Booking)
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int nights;
    private double pricePerNight;

    public Reservation(String reservationId, String guestName,
                       String roomType, int nights, double pricePerNight) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
    }

    public String getReservationId() {
        return reservationId;
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

    public double getTotalCost() {
        return nights * pricePerNight;
    }

    public void display() {
        System.out.println("Reservation ID: " + reservationId);
        System.out.println("Guest: " + guestName);
        System.out.println("Room Type: " + roomType);
        System.out.println("Nights: " + nights);
        System.out.println("Total Cost: ₹" + getTotalCost());
        System.out.println("-----------------------------");
    }
}

// Booking History (State Holder)
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        history = new ArrayList<>();
    }

    // Store confirmed reservation
    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    // Read-only access
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}

// Booking Report Service (Read-only Reporting)
class BookingReportService {

    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Display all bookings
    public void displayAllBookings() {
        System.out.println("\n--- Booking History ---\n");

        for (Reservation r : history.getAllReservations()) {
            r.display();
        }
    }

    // Generate summary report
    public void generateSummaryReport() {

        List<Reservation> reservations = history.getAllReservations();

        int totalBookings = reservations.size();
        double totalRevenue = 0;

        Map<String, Integer> roomTypeCount = new HashMap<>();

        for (Reservation r : reservations) {

            totalRevenue += r.getTotalCost();

            roomTypeCount.put(
                    r.getRoomType(),
                    roomTypeCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("\n--- Booking Summary Report ---\n");

        System.out.println("Total Bookings: " + totalBookings);
        System.out.println("Total Revenue: ₹" + totalRevenue);

        System.out.println("\nBookings by Room Type:");
        for (String type : roomTypeCount.keySet()) {
            System.out.println(type + ": " + roomTypeCount.get(type));
        }
    }
}

// Main Class
public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {

        // Step 1: Booking History
        BookingHistory history = new BookingHistory();

        // Step 2: Simulate confirmed bookings (from UC6)
        Reservation r1 = new Reservation("R101", "Arun", "Single", 2, 2000);
        Reservation r2 = new Reservation("R102", "Priya", "Suite", 1, 6000);
        Reservation r3 = new Reservation("R103", "Rahul", "Double", 3, 3500);
        Reservation r4 = new Reservation("R104", "Neha", "Single", 1, 2000);

        // Step 3: Add to history (in order)
        history.addReservation(r1);
        history.addReservation(r2);
        history.addReservation(r3);
        history.addReservation(r4);

        // Step 4: Reporting Service
        BookingReportService reportService = new BookingReportService(history);

        // Step 5: Admin views history
        reportService.displayAllBookings();

        // Step 6: Generate report
        reportService.generateSummaryReport();
    }
}