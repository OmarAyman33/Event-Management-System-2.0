package org.example.learningjavafx;

public class Wallet {
    private double balance;

    public Wallet() {
        this.balance = 0;
    }

    public Wallet(double initialBalance) {
        this.balance = Math.max(0, initialBalance); // no negative balance allowed
    }

    public double getBalance() {
        return balance;
    }

    public void addFunds(double amount) {
        if (amount > 0) {
            balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
