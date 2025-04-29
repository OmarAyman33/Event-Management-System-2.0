package org.example.learningjavafx;

import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Admin extends User {
    private String role;
    private String workingHours;
    private AdminDashboard dashboard = new AdminDashboard(this);

    //Constructors


    public Admin(String username, String password, String dateOfBirth, Gender gender,String role, String workingHours) {
        super(username, password, dateOfBirth, gender);
        this.role = role;
        this.workingHours = workingHours;
    }

    //msh aarf mfrod a intialize l lists wla laa
    public Admin(String username, String password, String dateOfBirth, Gender gender,
                 String role, String workingHours, Wallet wallet,
                 List<Organizer> myOrganizers, List<Room> adminsRooms,
                 List<Category> managedCategories, List<Event> managedEvents) {
        super(username, password, dateOfBirth, gender);  // Initialize the User class with the parent's constructor
        this.role = role;                               // Initialize role
        this.workingHours = workingHours;               // Initialize working hours
    }
    //getters



    public String getWorkingHours() {
        return workingHours;
    }

    public String getRole() {
        return role;
    }

    public String GetWorkingHours() {
        return workingHours;
    }

    //setters

    public void setRole(String Role) {
        this.role = Role;
    }

    public void setWorkingHours(String WorkingHours) {
        this.workingHours = WorkingHours;
    }

    //methods
    public void addRoom(Room room) {
        if (room != null) {
            Database.rooms.add(room);
            System.out.println("Room '" + room.getName() + "' added successfully to the database.");
        } else {
            System.out.println("Cannot add a null room.");
        }
    }
    //overridden methods from object class

    public String toString() {
        return "Admin{" + "username=" + getUsername() + ", role=" + role + ", workingHours=" + workingHours + "Gender is " + "}";
    }


    //methods
    public void addCategory() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Add New Category ---");
        System.out.print("Enter Category Name: ");
        String categoryName = scanner.nextLine().trim();

        // validation to make sure not empty input
        if (categoryName.isEmpty()) {
            System.out.println("Error: Category name cannot be empty.");
            return;
        }

        // Check if category already exists
        for (Category cat : Database.categories) {
            if (cat.getName().equalsIgnoreCase(categoryName)) {
                System.out.println("Error: Category already exists.");
                return;
            }
        }

        // Create and add new category
        Category newCategory = new Category(categoryName);
        Database.categories.add(newCategory);
        System.out.println("Category '" + categoryName + "' added successfully.");
    }

    public void viewAllRooms() { //wde l method lkhlto yshof rooms fe wahda tht events + rooms
        System.out.println("\nAll Rooms in the System ");

        if (Database.rooms == null || Database.rooms.isEmpty()) {
            System.out.println("No rooms are available in the database.");
            return;
        }

        for (Room room : Database.rooms) {
            System.out.println("Room Name: " + room.getName());
            System.out.println("Capacity: " + room.getCapacity());
            System.out.println("Price: " + room.getPrice());

            if (room.getEvents().isEmpty()) {
                System.out.println("Status: Not booked yet.\n");
            } else {
                System.out.println("Status: Booked for these dates:");
                for (Event event : room.getEvents()) {
                    System.out.println("- " + event.getDate() + " (Event: " + event.getName() + ")");
                }
                System.out.println();
            }
        }
    }

    //method to remove current event
    public void removeCurrentEvent(Event event) {//missing to refund attendees and remove from attendeees
        if (event == null) {
            System.out.println("there is no such event to be deleted");
            return;
        }
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

    public void removeRoom(Room room) {
        for (int i = 0; i < room.getEvents().size(); i++) {
            removeCurrentEvent(room.getEvents().get(i));
            i--; // as when an event gets removed getevents.size() decreases and the index of others also shifts back
        }
        Database.rooms.remove(room);
    }

    // l method de 5lt l admin yshof kol l attendees wkol events fadl l rooms
    public void viewAllEvents() {
        System.out.println("\n--- Events You Are Organizing ---");

        if (Database.events == null || Database.events.isEmpty()) {
            System.out.println("You have not created any events yet.");
            return;
        }

        System.out.println("--------------------------------------------------");
        for (int i = 0; i < Database.events.size(); i++) {
            Event event = Database.events.get(i);
            if (event != null) {
                // Event has getName(), getDate(), getPrice() (Change based on naming of events)
                System.out.println(i + 10);

                System.out.print("Attendees: ");
                if (event.getAttendees().isEmpty()) {
                    System.out.println("None");
                } else {
                    for (Attendee attendee : event.getAttendees()) {
                        System.out.print(attendee.getUsername() + " ");
                    }
                }
            }

        }
    }

    public void displayDashboard(Stage stage) {
        dashboard.displayDashboard(stage);
    }


}

