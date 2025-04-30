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


    //  Viewing Own Events for organizer , Can be updated in phase 2 (GUI)
    public void viewMyEvents() {
        System.out.println("\n--- Events You Are Organizing ---");

        if (this.myCreatedEvents == null || this.myCreatedEvents.isEmpty()) {
            System.out.println("You have not created any events yet.");
            return;
        }

        System.out.println("--------------------------------------------------");
        for (int i = 0; i < this.myCreatedEvents.size(); i++) {
            Event event = this.myCreatedEvents.get(i);
            if(event.getDate().isAfter(LocalDate.now())) {
                if (event != null) {
                    // Event has getName(), getDate(), getPrice() (Change based on naming of events)
                    System.out.printf(" (Date: %s, Price: $%.2f)%n",
                            (i + 1),
                            event.getName(),
                            event.getDate(),
                            event.getPrice()
                    );
                }
            }
        }
        System.out.println("--------------------------------------------------");
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

    public void updateEvent() {
        System.out.println("\n--- Update Event ---");
        Scanner scanner = new Scanner(System.in);

        viewMyEvents();
        if (this.myCreatedEvents.isEmpty()) return;

        // Events are displayed in numbers
        //We Choose the event to update
        System.out.print("Enter the number of the event you want to update: ");
        int eventNumber = Integer.parseInt(scanner.nextLine());

        if (eventNumber >= 1 && eventNumber <= this.myCreatedEvents.size()) {
            Event eventToUpdate = this.myCreatedEvents.get(eventNumber - 1);

            System.out.println("What do you want to update for '" + eventToUpdate.getName() + "'?");
            System.out.println("1. Event Name");
            System.out.println("2. Ticket Price");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter new Event Name: ");
                    String newName = scanner.nextLine();
                    eventToUpdate.setName(newName); // From Events
                    System.out.println("Event name updated!");
                    break;
                case 2:
                    System.out.print("Enter new Ticket Price: ");
                    double newPrice = Double.parseDouble(scanner.nextLine());
                    eventToUpdate.setPrice(newPrice); // From Events
                    System.out.println("Event price updated!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } else {
            System.out.println("Invalid event number entered.");
        }
    }


    //Display attendees Method

    public void viewAttendeesForEvent() {
        System.out.println("\n--- View Event Attendees ---");
        Scanner scanner = new Scanner(System.in);

        // All organizer events
        viewMyEvents();
        if (this.myCreatedEvents.isEmpty()) {
            return;
        }

        // 3. Choose event
        System.out.print("Enter the number of the event to view attendees for: ");
        int eventNumber = Integer.parseInt(scanner.nextLine());

        //Check
        if (eventNumber >= 1 && eventNumber <= this.myCreatedEvents.size()) {
            Event selectedEvent = this.myCreatedEvents.get(eventNumber - 1);

            //Get List from events
            List<Attendee> attendees = selectedEvent.getAttendees();

            // Display the attendees
            System.out.println("\nAttendees for '" + selectedEvent.getName() + "':");
            if (attendees == null || attendees.isEmpty()) {
                System.out.println("  (No attendees registered yet)");
            } else {
                for (int i = 0; i < attendees.size(); i++) {
                    Attendee attendee = attendees.get(i);
                    if (attendee != null) {

                        System.out.printf("", (i + 1), attendee.getUsername());
                    }
                }
            }
        } else {
            System.out.println("Invalid event number entered.");
        }
    }

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

    //View Available rooms (baher)

    //Getters for GUI
    // To use local array list
    public List<Event> getMyCreatedEvents() {
        return this.myCreatedEvents;
    }

}