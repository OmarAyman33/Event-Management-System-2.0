package org.example.learningjavafx;

import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class Organizer extends User {

    // Attributes
    private Wallet wallet;
    private List<Event> myCreatedEvents; // Stores events created by this specifc  organizer
    //Local array
    // Constructor
    public Organizer(String username, String password, String dateOfBirth, Gender gender) {
        super(username, password, dateOfBirth, gender); // Initialize User parts
        this.wallet = new Wallet(100);                     // Initialize Organizer's wallet
        this.myCreatedEvents = new ArrayList<>();       // Initialize the list of created events
    }

    public Organizer(String username, String password, String dateOfBirth, Gender gender, double balance) {
        super(username, password, dateOfBirth, gender); // Initialize User parts
        this.wallet = new Wallet(balance);                     // Initialize Organizer's wallet
        this.myCreatedEvents = new ArrayList<>();       // Initialize the list of created events
    }

    // Getter for Wallet
    public Wallet getWallet() {
        return wallet;
    }


    //Methods to add/Remove events from local array list (Not just database)

    protected void addCreatedEvent(Event event) {
        if (event != null && !this.myCreatedEvents.contains(event)) {
            this.myCreatedEvents.add(event);
        }
    }

    protected boolean removeCreatedEvent(Event event) {
        return this.myCreatedEvents.remove(event);
    }



    // --- CRUD Methods for Events ---

    public boolean createEvent(String name, LocalDate date, double price, Category category, Room room) {

        //Validation
        // Basic null/empty checks
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Create Event Error: Event name cannot be empty.");
            return false;
        }
        if (date == null) {
            System.err.println("Create Event Error: Event date cannot be null.");
            return false;
        }
        if (category == null) {
            System.err.println("Create Event Error: Event category cannot be null.");
            return false;
        }
        if (room == null) {
            System.err.println("Create Event Error: Event room cannot be null.");
            return false;
        }

        // Date validation (
        if (date.isBefore(LocalDate.now())) {
            System.err.println("Create Event Error: Event date must be in the future.");
            return false;
        }

        // Price validation
        if (price < 0) {
            System.err.println("Create Event Error: Event price cannot be negative.");
            return false;
        }

        // Check Room Availability for the given date
        if (!room.isAvailableOn(date)) {
            System.err.println("Create Event Error: Room '" + room.getName() + "' is not available on " + date + ".");
            return false; // room not available
        }

        // Check Organizer's Wallet Balance against Room Price
        if (room.getPrice() > this.wallet.getBalance()) {
            System.err.println("Create Event Error: Room cost ($" + room.getPrice() + ") exceeds wallet balance ($" + this.wallet.getBalance() + ").");
            return false; //  insufficient funds
        }


        try {

            Event newEvent = new Event(date, room, category, name, price, this);

            //Adds to main list
            Database.events.add(newEvent);

            // Add event to this Organizer's local list
            this.addCreatedEvent(newEvent);

            // Withdraw the room cost from wallet
            this.wallet.withdraw(room.getPrice());

            System.out.println("Event '" + name + "' created successfully.");
            return true;

        } catch (Exception e) {

            System.err.println("Unexpected error during event creation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(Event eventToDelete) { // Renamed for simplicity
        // Basic Validation
        if (eventToDelete == null) {
            System.err.println("Delete Event Error: Event to delete cannot be null.");
            return false;
        }
        if (!this.myCreatedEvents.contains(eventToDelete)) {
            System.err.println("Delete Event Error: Event '" + eventToDelete.getName() + "' not found in this organizer's list.");
            return false;
        }

        try {
            // 1. Remove from global Database list
            boolean removedFromDB = Database.events.remove(eventToDelete);
            if (!removedFromDB) {
                System.err.println("Warning: Event '" + eventToDelete.getName() + "' not found in global Database.events list during deletion.");
            }

            // 2. Remove from this organizer's local list
            this.removeCreatedEvent(eventToDelete); // Uses existing helper

            // 3. Unbook from Room and Refund Organizer
            Room room = eventToDelete.getRoom();
            if (room != null) {
                room.removeEvent(eventToDelete);
                if (this.wallet != null) {
                    this.wallet.addFunds(room.getPrice());
                    System.out.println("Refunded room cost $" + room.getPrice() + " to organizer " + this.getUsername());
                } else {
                    System.err.println("Warning: Organizer wallet is null. Cannot refund room cost.");
                }
            } else {
                System.err.println("Warning: Room is null for event " + eventToDelete.getName() + ".");
            }

            // 4. Notify Attendees
            ArrayList<Attendee> attendees = eventToDelete.getAttendees();
            if (attendees != null) {
                List<Attendee> attendeesCopy = new ArrayList<>(attendees);
                for (Attendee attendee : attendeesCopy) {
                    if (attendee != null) {
                        attendee.removeEvent(eventToDelete);
                    }
                }
            }

            System.out.println("Event '" + eventToDelete.getName() + "' deleted successfully.");
            return true; // Indicate success

        } catch (Exception e) {
            System.err.println("Unexpected error during event deletion: " + e.getMessage());
            e.printStackTrace();
            return false; // Indicate failure
        }
    }



    //Display attendees Method


    //Main Dashboard
    private OrganizerDashboard organizerDashboard = new OrganizerDashboard(this);
    @Override
    public void displayDashboard(Stage stage) {
        // Call the display method on the instance variable
        organizerDashboard.displayDashboard(stage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        User that = (User) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "username='" + getUsername() + '\'' +
                ", balance=" + (wallet != null ? wallet.getBalance() : "N/A") +
                '}';
    }


    //Getters for GUI
    // To use local array list
    public List<Event> getMyCreatedEvents() {
        return this.myCreatedEvents;
    }

    public boolean updateEvent(Event eventToUpdate, String newName, LocalDate newDate,
                               double newPrice, Category newCategory, Room newRoom) {

        // Validation
        if (eventToUpdate == null || newName == null || newName.trim().isEmpty() ||
                newDate == null || newPrice < 0 || newCategory == null || newRoom == null) {
            System.err.println("Backend Update(Replace) Error: Invalid null data provided for replacement.");
            return false;
        }
        if (newDate.isBefore(LocalDate.now())) {
            System.err.println("Backend Update(Replace) Error: Replacement event date must be in the future.");
            return false;
        }

        // --- 3. Room Booking Logic & Availability Check (for the NEW room/date) ---
        Room oldRoom = eventToUpdate.getRoom();
        LocalDate oldDate = eventToUpdate.getDate();
        boolean dateChanged = !newDate.equals(oldDate);
        boolean roomChanged = !newRoom.equals(oldRoom); // Relies on Room.equals()

        if (dateChanged || roomChanged) {
            // making sure that the new room is available on the new date
            if (!newRoom.isAvailableOn(newDate)) {
                System.err.println("Backend Update(Replace) Failed: Target Room '" + newRoom.getName() +
                        "' is not available on target date " + newDate + " for replacement event.");
                return false;
            }
        }
        // checks done, now onto the replacement process


        try {
            // if room has changed, unbook the old room
            if ((!oldRoom.equals(newRoom) || !eventToUpdate.getDate().equals(newDate))) {
                oldRoom.removeEvent(eventToUpdate);
                System.out.println("Backend Info: Removed event from old room: " + oldRoom.getName());
            }

            // updating the event details
            eventToUpdate.setName(newName.trim());
            eventToUpdate.setDate(newDate);
            eventToUpdate.setPrice(newPrice);
            eventToUpdate.setCategory(newCategory);
            eventToUpdate.setRoom(newRoom);

            // booking new room
            if (!newRoom.equals(oldRoom) || !newDate.equals(eventToUpdate.getDate())) {
                newRoom.bookEvent(eventToUpdate);
                System.out.println("Backend Info: Booked event in new room: " + newRoom.getName());
            }

            System.out.println("Backend: Event '" + eventToUpdate.getName() + "' updated in-place by Organizer '" + this.getUsername() + "'.");
            return true;
        } catch (Exception ex) {
            System.err.println("Backend Error: Failed to update event: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }

    }

}