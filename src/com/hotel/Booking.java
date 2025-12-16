package com.hotel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private User user; // Reference to user (balance might change, but identity doesn't)
    
    // Snapshot of room details at time of booking
    private int roomId;
    private RoomType snapshotRoomType;
    private int snapshotPricePerNight;
    
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDate bookingDate;
    private int totalPrice;

    public Booking(User user, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.user = user;
        this.roomId = room.getRoomNumber();
        // Capture snapshot of room data
        this.snapshotRoomType = room.getType();
        this.snapshotPricePerNight = room.getPricePerNight();
        
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.bookingDate = LocalDate.now();
        
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalPrice = (int) (nights * snapshotPricePerNight);
    }

    public int getRoomId() {
        return roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    @Override
    public String toString() {
        return String.format("Booking [User: %d | Room: %d (%s) | %s to %s | Total: %d | Booked On: %s]",
                user.getId(), roomId, snapshotRoomType, checkIn, checkOut, totalPrice, bookingDate);
    }
}
