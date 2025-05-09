package org.example.learningjavafx;

import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Admin extends User {
    private String role;
    private String workingHours;
    private AdminDashboard dashboard = new AdminDashboard(this);

    // Constructor to initialize Admin with essential fields
    public Admin(String username, String password, String dateOfBirth, Gender gender, String role, String workingHours) {
        super(username, password, dateOfBirth, gender);
        this.role = role;
        this.workingHours = workingHours;
    }

    // Extended constructor for future scalability (currently unused lists)
    public Admin(String username, String password, String dateOfBirth, Gender gender,
                 String role, String workingHours, Wallet wallet,
                 List<Organizer> myOrganizers, List<Room> adminsRooms,
                 List<Category> managedCategories, List<Event> managedEvents) {
        super(username, password, dateOfBirth, gender);
        this.role = role;
        this.workingHours = workingHours;
    }

    // Getters
    public String getWorkingHours() {
        return workingHours;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setRole(String Role) {
        this.role = Role;
    }

    public void setWorkingHours(String WorkingHours) {
        this.workingHours = WorkingHours;
    }

    // Adds a new Room to the database if it is not null
    public void addRoom(Room room) {
        if (room != null) {
            Database.rooms.add(room);
            System.out.println("Room '" + room.getName() + "' added successfully to the database.");
        } else {
            System.out.println("Cannot add a null room.");
        }
    }

    // Returns a string representation of the Admin
    public String toString() {
        return "Admin{" + "username=" + getUsername() + ", role=" + role + ", workingHours=" + workingHours + "Gender is " + "}";
    }


    // Removes a given event from the system and handles associated data cleanup
    public void removeCurrentEvent(Event event) {
        if (event == null) {
            System.out.println("There is no such event to be deleted.");
            return;
        }

        Database.events.remove(event);
        Organizer organizer = event.getOrganizer();

        if (organizer == null) {
            System.out.println("There is no organizer associated with this event.");
            return;
        }

        organizer.removeCreatedEvent(event);
        System.out.println("Event '" + event.getName() + "' deleted successfully.");

        Room room = event.getRoom();
        if (room != null) {
            room.removeEvent(event);

            if (organizer.getWallet() != null) {
                organizer.getWallet().addFunds(room.getPrice()); // Refund room price to organizer
            }
        }

        ArrayList<Attendee> attendees = event.getAttendees();
        for (int i = 0; i < attendees.size(); i++) {
            attendees.get(i).removeEvent(event); // Remove event from each attendee
        }
    }

    // Removes a room and all events scheduled in it
    public void removeRoom(Room room) {
        for (int i = 0; i < room.getEvents().size(); i++) {
            removeCurrentEvent(room.getEvents().get(i));
            i--; // Adjust index due to list shrinking after each removal
        }
        Database.rooms.remove(room);
    }

    // Displays all events in the system and their attendees
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

    // Launches the Admin dashboard UI
    public void displayDashboard(Stage stage) {
        dashboard.displayDashboard(stage);
    }
}
