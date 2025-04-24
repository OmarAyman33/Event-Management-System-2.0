package org.example.learningjavafx;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;


public class Organizer extends User {

    // Attributes
    private Wallet wallet;
    private List<Event> myCreatedEvents; // Stores events created by this organizer

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
                    System.out.printf("%d. %s (Date: %s, Price: $%.2f)%n",
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
    // NO VALIDATION PROGRAM WILL CRASH IF INCORRECT INPUTS

    public void createEvent() {
        System.out.println("\n--- Create New Event ---");
        Scanner scanner = new Scanner(System.in);


        System.out.print("Enter Event Name: ");
        String name = scanner.nextLine();

        // Get Event Date (YYYY-MM-DD )( NEEDS VALIDATION)
        System.out.print("Enter Event Date (YYYY-MM-DD): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        if(date.isBefore(LocalDate.now())){
            System.out.println("Invalid Date, try again");
            createEvent();
            return;
        }

        //  NEEDS VALIDATION
        System.out.print("Enter Ticket Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        // Select Category by Number
        Category selectedCategory = null;
        //Verify Database is not empty. (MAY REMOVE)
        if (Database.categories == null || Database.categories.isEmpty()) {
            System.out.println("Error: No categories available in the Database. Cannot create event.");
            return;
        }
        System.out.println("Available Categories:");
        for (int i = 0; i < Database.categories.size(); i++) {
            // Assumes Category class has a getName() method
            System.out.printf("%d. %s%n", (i + 1), Database.categories.get(i).getName());
        }
        System.out.print("Select Category Number: ");
        int categoryChoice = Integer.parseInt(scanner.nextLine());

        // Basic Check
        if (categoryChoice < 1 || categoryChoice > Database.categories.size()) {
            System.out.println("Invalid category number selected. Returning to menu.");
            return;
        }
        selectedCategory = Database.categories.get(categoryChoice - 1); // Get category by index


        // 5. Select Room by Number
        Room selectedRoom = null;
        // *ASSUMPTION*: Database.rooms exists and is accessible
        if (Database.rooms == null || Database.rooms.isEmpty()) {
            System.out.println("Error: No rooms available in the Database. Cannot create event.");
            return;
        }
        System.out.println("Available Rooms:");
        for (int i = 0; i < Database.rooms.size(); i++) {
            if(Database.rooms.get(i).isAvailableOn(date)) {
                System.out.printf("%d. %s (Capacity: %d)%n",
                        (i + 1),
                        Database.rooms.get(i).getName(),
                        Database.rooms.get(i).getCapacity(),
                        Database.rooms.get(i).getPrice()
                );
            }
        }
        System.out.print("Select Room Number: ");
        int roomChoice = Integer.parseInt(scanner.nextLine()); // Direct parse

        // Basic bounds check for Room choice
        if (roomChoice < 1 || roomChoice > Database.rooms.size()) {
            System.out.println("Invalid room number selected. Returning to menu.");
            return;
        }
        selectedRoom = Database.rooms.get(roomChoice - 1); // Get room by index


        // Check Room Availability (Now that we have the selected room)
        if (!selectedRoom.isAvailableOn(date)) { // Uses Room.isAvailableOn(LocalDate)
            System.out.println("Sorry, Room '" + selectedRoom.getName() + "' is not available on " + date + ".");
            return;
        }

        if(selectedRoom.getPrice()>wallet.getBalance())
        {
            System.out.println("Sorry, the Room you selected is out of your budget, please select another room.");
            return;
        }
        else{
               wallet.withdraw(selectedRoom.getPrice());
        }
        // Create Event object

        Event newEvent = new Event(date, selectedRoom, selectedCategory, name, price, this);

        //  Book the Room (adds event to room's internal list)
        selectedRoom.bookEvent(newEvent);

        //  Add event to main Database list
        if (Database.events != null) {
            Database.events.add(newEvent);
        } else {
            System.err.println("CRITICAL ERROR: Database.events list not initialized!");
            // Should ideally unbook room if DB save fails
            return;
        }

        // 10. Add event to this Organizer's local list
        this.addCreatedEvent(newEvent);

        System.out.println("Event '" + name + "' created successfully!");
    }

    public void DeleteEvent() {//missing to refund attendees and remove from attendeees
        Scanner scanner = new Scanner(System.in);
        viewMyEvents();
        ArrayList<Event> upcomingCreatedEvents = new ArrayList<Event>();
        for (int i = 0; i < myCreatedEvents.size(); i++)
        {
            if(myCreatedEvents.get(i).getDate().isAfter(LocalDate.now()))
                upcomingCreatedEvents.add(myCreatedEvents.get(i));
        }

        System.out.println("Enter the number of the event you want to select.");
        int choice =Integer.parseInt(scanner.nextLine());

        if(choice > upcomingCreatedEvents.size())
        {
            System.out.println("Please keep the index in the range of the given events");
        }
        Event event = upcomingCreatedEvents.get(choice-1);
        Database.events.remove(event);//removing event from general database
        Organizer organizer = event.getOrganizer();//get the organizer of this event
        if (organizer == null) {
            System.out.println("there is no organizer that has that event");
            return;
        }
        organizer.removeCreatedEvent(event);//remove this event from his list
        System.out.println("Event '" + event.getName() + "' deleted successfully.");
        Room room = event.getRoom();
        room.removeEvent(event);
        if (room != null && organizer.getWallet() != null) {
            organizer.getWallet().addFunds(room.getPrice());//refund the room price to the corresponding organizer    }
        }
        ArrayList<Attendee> attendees = event.getAttendees();
        for (int i = 0; i < attendees.size(); i++) {
            attendees.get(i).removeEvent(event);
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

            // 4. Get List from events
            List<Attendee> attendees = selectedEvent.getAttendees();

            // 5. Display the attendees
            System.out.println("\nAttendees for '" + selectedEvent.getName() + "':");
            if (attendees == null || attendees.isEmpty()) {
                System.out.println("  (No attendees registered yet)");
            } else {
                for (int i = 0; i < attendees.size(); i++) {
                    Attendee attendee = attendees.get(i);
                    if (attendee != null) {
                        // Assumes Attendee (or User) has getUsername()
                        System.out.printf("  %d. %s%n", (i + 1), attendee.getUsername());
                    }
                }
            }
        } else {
            System.out.println("Invalid event number entered.");
        }
    }

    //Main Dashboard (Will check again later)
    @Override
    public void displayDashboard() {
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        do {
            System.out.println("\n--- Organizer Dashboard ---");
            System.out.println("Welcome, Organizer " + getUsername() + "!");
            System.out.println("---------------------------------");
            System.out.println("1. Create New Event");
            System.out.println("2. View My Events");
            System.out.println("3. Update an Event");
            System.out.println("4. Delete an Event");
            System.out.println("5. View Attendees for an Event");
            System.out.println("6. View Wallet Balance");
            System.out.println("7. Deposit funds to Wallet");
            System.out.println("0. Logout");
            System.out.println("---------------------------------");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        createEvent();
                        break;
                    case 2:
                        viewMyEvents();
                        break;
                    case 3:
                        updateEvent();
                        break;
                    case 4:
                        DeleteEvent();
                        break;
                    case 5:
                        viewAttendeesForEvent();
                        break;
                    case 6:
                        System.out.println("Your current balance: " +  wallet.getBalance());
                        break;
                    case 7:
                        System.out.println("Enter the amount you want to deposit: ");
                        double amount = Double.parseDouble(scanner.nextLine());
                        if(amount > 0 )
                            wallet.addFunds(amount);
                        else
                            System.out.println("Invalid amount, try again.");
                        displayDashboard();
                    case 0:
                        System.out.println("Logging out...");
                        Database.login.start();
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from the menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = -1;
            } catch (Exception e) {
                System.err.println("An unexpected error occurred: " + e.getMessage());
                choice = -1;
            }

            if (choice != 0) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 0);
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

}