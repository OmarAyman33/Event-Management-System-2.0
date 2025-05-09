package org.example.learningjavafx;

import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Attendee extends User {
    private ArrayList<Event> registeredEvents;
    private Wallet wallet;
    private ArrayList<Category> interests;
    private AttendeePage attendeePage = new AttendeePage(this);
    // Constructor with default wallet balance
    Attendee(String username, String password, String dob, Gender gender) {
        super(username, password, dob, gender);
        wallet = new Wallet(0);
        interests = new ArrayList<Category>();
        registeredEvents = new ArrayList<Event>();
    }

    // Constructor with custom wallet balance
    Attendee(String username, String password, String dob, Gender gender, double balance) {
        super(username, password, dob, gender);
        wallet = new Wallet(balance);
        interests = new ArrayList<Category>();
        registeredEvents = new ArrayList<Event>();
    }

    // Display all upcoming events from the database
    public void showAllEvents() {
        if (Database.events == null || Database.events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        System.out.println("Available Events:");
        for (int i = 0; i < Database.events.size(); i++) {
            Event event = Database.events.get(i);
            if (event.getDate().isAfter(LocalDate.now()))
                System.out.println((i + 1) + ". " + event.getName() + " - $" + event.getPrice() + ", Date: " + event.getDate());
        }
    }

    // Remove a registered event and refund the attendee
    public void removeEvent(Event event) {
        registeredEvents.remove(event);
        wallet.addFunds(event.getPrice());
    }


    // Purchase a ticket for a selected event
    public boolean buyTicket(Event selectedEvent) {
        ArrayList<Event> availableEvents = new ArrayList<Event>();
        if (wallet.withdraw(selectedEvent.getPrice())) {
            registeredEvents.add(selectedEvent);
            selectedEvent.addAttendee(this);
            System.out.println("Ticket purchased for: " + selectedEvent.getName());
            return true;
        } else {
            System.out.println("Not enough balance.");
            return false;
        }
    }



    public ArrayList<Event> getRegisteredEvents() {
        return registeredEvents;
    }

    public Wallet getWallet(){
        return wallet;
    }

    public ArrayList<Category> getInterests() {
        return interests;
    }

    public void addInterest(Category cat) {
        interests.add(cat);
    }

    public void displayDashboard(Stage stage){
        attendeePage.displayDashboard(stage);
    }

    public String getGender() {
        return gender.toString();
    }
}