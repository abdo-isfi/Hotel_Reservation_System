package com.hotel;

import java.time.LocalDate;

public class User {
    private int id;
    private int balance;
    private LocalDate creationDate;

    public User(int id, int balance) {
        this.id = id;
        this.balance = balance;
        this.creationDate = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Override
    public String toString() {
        return "User ID: " + id + ", Balance: " + balance + ", Created: " + creationDate;
    }
}
