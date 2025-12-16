# Hotel Reservation System

## Overview

This project is a standalone Hotel Reservation System implemented in pure Java. It was developed as a solution to a technical assessment, designed to simulate the core logic of a booking engine.

The primary goal of this implementation is to demonstrate a strong grasp of Object-Oriented Programming (OOP) principles, clean architecture, and robust business logic handling. While my primary expertise lies in frontend development (React), this project serves as a demonstration of my ability to work effectively across the full stack and implement improved backend designs.

## Technical Stack

*   **Language**: Java (JDK 8+)
*   **Dependencies**: Standard Java Libraries only (`java.util`, `java.time` equivalent logic)
*   **Architecture**: Layered service-oriented design (simulated in-memory)

## System Design

The system is built around three core entities, each with a specific responsibility:

### Entities

1.  **User**: Represents a customer with a balance. The system tracks user balances to ensure they have sufficient funds before confirming a booking.
2.  **Room**: Represents a hotel room with a type (Standard, Junior Suite, Master Suite) and a price per night.
3.  **Booking**: Acts as an immutable record of a reservation.
    *   **Design Note (Snapshot Pattern)**: The `Booking` entity stores a *snapshot* of the room's price and type at the time of creation. This ensures that if a hotel manager updates a room's price later, existing bookings remain unaffected and historically accurate.

### Business Rules & Constraints

*   **Balance Validation**: A booking is strictly rejected if the user's balance is lower than the total cost (Price * Nights).
*   **Date Validation**: Check-out dates must strictly follow check-in dates.
*   **Availability Logic**: The system performs a rigorous overlap check `(StartA < EndB) && (EndA > StartB)` to ensure no room is double-booked.
*   **Data Integrity**: Modifying a room's attributes via `setRoom` does not alter the data of previously confirmed bookings.

## Service Responsibilities

The logic is encapsulated within a centralized `Service` class, which handles:

*   **`setRoom`**: Creates or updates a room in the inventory.
*   **`setUser`**: Registers a new user with an initial balance.
*   **`bookRoom`**: The core transactional method. It checks availability, calculates costs, validates balances, deducts funds, and commits the booking.
*   **`printAll`**: Outputs the current state of rooms and bookings for verification.

## Test Scenario

The project includes a `Main.java` file that executes a predefined test scenario to validate all constraints:

1.  **Insufficient Funds**: Attempts to book a luxury room with a low balance. (Expected: Failure)
2.  **Invalid Dates**: Attempts to book with a check-out date before the check-in date. (Expected: Failure)
3.  **Successful Booking**: Completes a valid transaction, updating the user's balance. (Expected: Success)
4.  **Overlapping Dates**: Attempts to book a room that is already occupied during the requested window. (Expected: Failure)
5.  **Price Stability**: Modifies a room's price after a booking is made to verify that the original booking cost remains unchanged. (Expected: Success)

## Design Decisions

*   **In-Memory Storage**: I chose `ArrayList` and `HashMap` for data storage to keep the solution lightweight and focused on logic rather than infrastructure setup.
*   **No Repository Layer**: For the scope of this test, a full repository pattern would be over-engineering. The `Service` class directly manages the collections, which is appropriate for a prototype.
*   **Exception Handling**: Verified exceptions (`HotelException`) are used to handle business logic failures gracefully, separating "expected" errors (like fully booked rooms) from unexpected runtime system errors.

## Bonus – Design Questions

### Q1: Suppose we put all the functions inside the same service. Is this recommended?

**Answer**: No, placing all functionality into a single `Service` class is not recommended for production systems as it violates the Single Responsibility Principle (SRP). A "God Class" like this becomes difficult to maintain, test, and extend.

In a real-world scenario, I would refactor this into:
*   `UserService`: Handling user registration and balance management.
*   `RoomService`: Managing room inventory and pricing.
*   `BookingService`: Orchestrating the booking transaction and dependency on the other two services.

### Q2: We have an issue with the `setRoom` method. If we change the price of a room, it changes the price for all bookings. How can we fix this?

**Answer**: This is solved by decoupling the booking data from the current room state. There are a few approaches:

1.  **Snapshot (Implemented)**: Copy the price and room type into the `Booking` object at the moment of creation. This is the approach I used as it is simple and effective for this scale.
2.  **Pricing History Table**: Create a separate entity that tracks price changes over time (`price`, `valid_from`, `valid_to`). The system would then query this table based on the booking date.
3.  **Versioning**: Create a new version of the `Room` entity (e.g., `RoomID_v2`) whenever a change occurs, leaving the old reference intact for past bookings.

## How to Run

Navigate to the source directory and compile the project using the standard Java compiler.

```bash
cd HotelReservationSystem/src
javac Main.java com/hotel/*.java
java Main
```

## Conclusion

This project fulfills all requirements of the technical test while adding necessary robustness in date handling and error management. It reflects my approach to coding: prioritize clarity, adhere exactly to requirements, and ensure the system behaves predictably under edge cases. I am confident this demonstrates my ability to adapt quickly and deliver high-quality backend code alongside my frontend expertise.
