import com.hotel.Service;
import com.hotel.RoomType;
import com.hotel.HotelException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   HOTEL RESERVATION SYSTEM - TEST RUN   ");
        System.out.println("=========================================\n");

        Service service = new Service();

        // --- SETUP ---
        System.out.println(">>> 1. Creating Rooms and Users...");
        
        service.setRoom(1, RoomType.STANDARD_SUITE, 1000);
        service.setRoom(2, RoomType.JUNIOR_SUITE, 2000);
        service.setRoom(3, RoomType.MASTER_SUITE, 3000);
        System.out.println("Rooms created.");

        service.setUser(1, 5000);
        service.setUser(2, 10000);
        System.out.println("Users created.\n");

        // --- SCENARIO IMPLEMENTATION ---
        
        // Helper dates
        // Note: Month in Calendar is 0-indexed (June = 5, July = 6)
        Date dateStart_User1 = makeDate(2026, Calendar.JUNE, 30);
        Date dateEnd_User1 = makeDate(2026, Calendar.JULY, 7); // 7 nights

        // TEST 1: User 1 tries to reserve Room 2 (7 nights * 2000 = 14000). User 1 has 5000.
        // Expected: Fail (Insufficient funds)
        System.out.println(">>> Test 1: User 1 books Room 2 (Too expensive)");
        attemptBooking(service, 1, 2, dateStart_User1, dateEnd_User1);

        // TEST 2: User 1 tries access Room 2 with inverted dates
        // Expected: Fail (Invalid dates)
        System.out.println("\n>>> Test 2: User 1 books Room 2 (Inverted Dates)");
        attemptBooking(service, 1, 2, dateEnd_User1, dateStart_User1);

        // TEST 3: User 1 reserves Room 1 (1 night). 
        // 07/07/2026 to 08/07/2026.
        // Cost: 1000. Balance: 5000 -> 4000.
        System.out.println("\n>>> Test 3: User 1 books Room 1 (Valid: 1 night)");
        Date dateStart_Test3 = makeDate(2026, Calendar.JULY, 7);
        Date dateEnd_Test3 = makeDate(2026, Calendar.JULY, 8);
        attemptBooking(service, 1, 1, dateStart_Test3, dateEnd_Test3);

        // TEST 4: User 2 reserves Room 1 (2 nights).
        // 07/07/2026 to 09/07/2026.
        // Overlap with Test 3 (07-08).
        // Expected: Fail (Room taken).
        System.out.println("\n>>> Test 4: User 2 books Room 1 (Overlap)");
        Date dateStart_Test4 = makeDate(2026, Calendar.JULY, 7);
        Date dateEnd_Test4 = makeDate(2026, Calendar.JULY, 9);
        attemptBooking(service, 2, 1, dateStart_Test4, dateEnd_Test4);

        // TEST 5: User 2 reserves Room 3 (1 night).
        // 07/07/2026 to 08/07/2026.
        // Cost: 3000. Balance: 10000 -> 7000.
        System.out.println("\n>>> Test 5: User 2 books Room 3 (Valid)");
        Date dateStart_Test5 = makeDate(2026, Calendar.JULY, 7);
        Date dateEnd_Test5 = makeDate(2026, Calendar.JULY, 8);
        attemptBooking(service, 2, 3, dateStart_Test5, dateEnd_Test5);

        // TEST 6: Modify Room 1
        System.out.println("\n>>> Test 6: Modifying Room 1 (STANDARD -> MASTER, 1000 -> 10000)");
        service.setRoom(1, RoomType.MASTER_SUITE, 10000);
        System.out.println("Room 1 updated.");

        // --- FINAL OUTPUT ---
        service.printAll();
        service.printAllUsers();
    }

    private static void attemptBooking(Service service, int userId, int roomId, Date in, Date out) {
        try {
            service.bookRoom(userId, roomId, in, out);
        } catch (HotelException e) {
            System.err.println("[FAILURE] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Date makeDate(int year, int month, int day) {
        return new GregorianCalendar(year, month, day).getTime();
    }
}
