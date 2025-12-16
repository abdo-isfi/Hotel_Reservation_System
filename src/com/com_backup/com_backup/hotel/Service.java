package com.hotel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class Service {
    private ArrayList<Room> rooms;
    private ArrayList<User> users;
    private ArrayList<Booking> bookings;

    public Service() {
        this.rooms = new ArrayList<>();
        this.users = new ArrayList<>();
        this.bookings = new ArrayList<>();
    }

    public void setRoom(int roomNumber, RoomType roomType, int roomPricePerNight) {
        Room existingRoom = findRoom(roomNumber);
        if (existingRoom != null) {
            existingRoom.setType(roomType);
            existingRoom.setPricePerNight(roomPricePerNight);
        } else {
            rooms.add(new Room(roomNumber, roomType, roomPricePerNight));
        }
    }

    public void setUser(int userId, int balance) {
        User existingUser = findUser(userId);
        if (existingUser != null) {
            existingUser.setBalance(balance);
        } else {
            users.add(new User(userId, balance));
        }
    }

    public void bookRoom(int userId, int roomNumber, Date checkInDate, Date checkOutDate) {
        LocalDate checkIn = convertToLocalDate(checkInDate);
        LocalDate checkOut = convertToLocalDate(checkOutDate);

        // 1. Validate Dates
        if (!checkOut.isAfter(checkIn)) {
            throw new HotelException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
           // Not strictly required by prompt, but good practice. Disabled for now to allow future/test dates freely.
        }

        // 2. Find Entities
        User user = findUser(userId);
        if (user == null) throw new HotelException("User " + userId + " not found.");

        Room room = findRoom(roomNumber);
        if (room == null) throw new HotelException("Room " + roomNumber + " not found.");

        // 3. Check Availability
        if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
            throw new HotelException("Room " + roomNumber + " is not available for the selected dates.");
        }

        // 4. Calculate Price
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        int totalCost = (int) (nights * room.getPricePerNight());

        // 5. Check Balance
        if (user.getBalance() < totalCost) {
            throw new HotelException("Insufficient balance. Cost: " + totalCost + ", Balance: " + user.getBalance());
        }

        // 6. Execute Booking (Transactional feel)
        user.setBalance(user.getBalance() - totalCost);
        Booking newBooking = new Booking(user, room, checkIn, checkOut);
        bookings.add(newBooking);
        
        System.out.println("Booking confirmed for User " + userId + " in Room " + roomNumber + ". Cost: " + totalCost);
    }

    public void printAll() {
        System.out.println("\n=== PRINT ALL DATA ===");
        System.out.println("--- Rooms ---");
        for (Room r : rooms) {
            System.out.println(r);
        }

        System.out.println("\n--- Bookings (Newest to Oldest) ---");
        // Creates a shallow copy to reverse, avoiding modifying the source list if order matters there
        ArrayList<Booking> reversedBookings = new ArrayList<>(bookings);
        Collections.reverse(reversedBookings);
        
        if (reversedBookings.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            for (Booking b : reversedBookings) {
                System.out.println(b);
            }
        }
        System.out.println("======================\n");
    }

    public void printAllUsers() {
        System.out.println("\n=== PRINT ALL USERS (Newest to Oldest) ===");
        ArrayList<User> reversedUsers = new ArrayList<>(users);
        Collections.reverse(reversedUsers);
        
        for (User u : reversedUsers) {
            System.out.println(u);
        }
        System.out.println("==========================================\n");
    }

    // --- Helpers ---

    private Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    private User findUser(int userId) {
        for (User u : users) {
            if (u.getId() == userId) return u;
        }
        return null;
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate start, LocalDate end) {
        for (Booking b : bookings) {
            if (b.getRoomId() == roomNumber) {
                // Check overlap
                // Overlap exists if (StartA < EndB) and (EndA > StartB)
                if (start.isBefore(b.getCheckOut()) && end.isAfter(b.getCheckIn())) {
                    return false;
                }
            }
        }
        return true;
    }

    private LocalDate convertToLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
