package org.example.learningjavafx;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class AdminDashboard {

    private Admin admin; // The logged-in admin
    public AdminDashboard(Admin admin) {
        this.admin = admin;
    }

    // Display the main dashboard
    public void displayDashboard(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));
        stage.setTitle("Admin Dashboard");

        // Menu buttons
        Button addCategoryBtn = new Button("Add Category");
        Button addRoomBtn = new Button("Add Room");
        Button viewRoomsBtn = new Button("View All Rooms");
        Button viewEventsBtn = new Button("View All Events");
        Button logOutBtn = new Button("Log Out");
        Button viewAttendeesBtn = new Button("View All Attendees");

        // Apply styling
        String style = Database.menuButtonStyle;
        addCategoryBtn.setStyle(style);
        addRoomBtn.setStyle(style);
        viewRoomsBtn.setStyle(style);
        viewEventsBtn.setStyle(style);
        logOutBtn.setStyle(style);
        viewAttendeesBtn.setStyle(style);

        addCategoryBtn.setMaxWidth(Double.MAX_VALUE);
        addRoomBtn.setMaxWidth(Double.MAX_VALUE);
        viewRoomsBtn.setMaxWidth(Double.MAX_VALUE);
        viewEventsBtn.setMaxWidth(Double.MAX_VALUE);
        logOutBtn.setMaxWidth(Double.MAX_VALUE);
        viewAttendeesBtn.setMaxWidth(Double.MAX_VALUE);

        // Set button actions
        addCategoryBtn.setOnAction(e -> stage.setScene(addCategory(stage)));
        addRoomBtn.setOnAction(e -> stage.setScene(addRoom(stage)));
        viewRoomsBtn.setOnAction(e -> stage.setScene(viewRooms(stage)));
        viewEventsBtn.setOnAction(e -> stage.setScene(viewEvents(stage)));
        logOutBtn.setOnAction(e -> new LoginPage().start(stage));
        viewAttendeesBtn.setOnAction(e -> stage.setScene(viewAttendees(stage)));

        // Add buttons to vbox
        vbox.getChildren().addAll(addCategoryBtn, addRoomBtn, viewRoomsBtn, viewEventsBtn,viewAttendeesBtn, logOutBtn);

        Scene scene = new Scene(vbox, 300, 400);
        stage.setScene(scene);
        stage.show();
    }



    // Screen to add a new category
    public Scene addCategory(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        Label label = new Label("Add New Category:");
        TextField nameField = new TextField();
        nameField.setPromptText("Category Name");

        Label feedback = new Label();

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle(Database.confirmButtonStyle);
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        submitBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                feedback.setText("Category name cannot be empty.");
                return;
            }
            for (Category cat : Database.categories) {
                if (cat.getName().equalsIgnoreCase(name)) {
                    feedback.setText("Category already exists.");
                    return;
                }
            }
            Database.categories.add(new Category(name));
            feedback.setText("Category added successfully!");
            nameField.clear();
        });

        backBtn.setOnAction(e -> displayDashboard(stage));

        vbox.getChildren().addAll(label, nameField, submitBtn, feedback, backBtn);
        return new Scene(vbox, 300, 300);
    }

    // Screen to add a new room
    public Scene addRoom(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        Label label = new Label("Add New Room:");
        TextField nameField = new TextField();
        nameField.setPromptText("Room Name");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        TextField priceField = new TextField();
        priceField.setPromptText("Price ($)");

        Label feedback = new Label();

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle(Database.confirmButtonStyle);
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        submitBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());

                if (name.isEmpty() || capacity <= 0 || price <= 0) {
                    feedback.setText("Invalid values.");
                    return;
                }
                Room room = new Room(name, capacity, price);
                admin.addRoom(room);
                feedback.setText("Room added successfully!");
                nameField.clear();
                capacityField.clear();
                priceField.clear();
            } catch (NumberFormatException ex) {
                feedback.setText("Please enter valid numbers.");
            }
        });

        backBtn.setOnAction(e -> displayDashboard(stage));

        vbox.getChildren().addAll(label, nameField, capacityField, priceField, submitBtn, feedback, backBtn);
        return new Scene(vbox, 300, 350);
    }

    // Screen to view and remove rooms
    public Scene viewRooms(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        Label label = new Label("All Rooms:");

        ListView<String> roomList = new ListView<>();
        ArrayList<Room> rooms = new ArrayList<>(Database.rooms);

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            roomList.getItems().add(room.getName() + " | Capacity: " + room.getCapacity() + " | Price: $" + room.getPrice());
        }

        Button removeBtn = new Button("Remove Selected Room");
        removeBtn.setStyle(Database.confirmButtonStyle);
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        Label feedback = new Label();

        removeBtn.setOnAction(e -> {
            int index = roomList.getSelectionModel().getSelectedIndex();
            if (index != -1) { // if nothing is selected the index is automatically sett to -1
                Room room = rooms.get(index);
                admin.removeRoom(room);
                feedback.setText("Room removed.");
                roomList.getItems().remove(index);
                rooms.remove(index);
            } else {
                feedback.setText("Please select a room to remove.");
            }
        });

        backBtn.setOnAction(e -> displayDashboard(stage));

        vbox.getChildren().addAll(label, roomList, removeBtn, feedback, backBtn);
        return new Scene(vbox, 400, 400);
    }

    // Screen to view and remove events
    public Scene viewEvents(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        Label label = new Label("All Events:");

        ListView<String> eventList = new ListView<>();
        ArrayList<Event> events = new ArrayList<>(Database.events);

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event.getDate().isAfter(LocalDate.now()))
                eventList.getItems().add(event.getName() + " | " + event.getDate() + " | $" + event.getPrice());
        }

        Button removeBtn = new Button("Remove Selected Event");
        removeBtn.setStyle(Database.confirmButtonStyle);
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        Label feedback = new Label();

        removeBtn.setOnAction(e -> {
            int index = eventList.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Event event = events.get(index);
                admin.removeCurrentEvent(event);
                feedback.setText("Event removed successfully.");
                eventList.getItems().remove(index);
                events.remove(index);
            } else {
                feedback.setText("Please select an event to remove.");
            }
        });

        backBtn.setOnAction(e -> displayDashboard(stage));

        vbox.getChildren().addAll(label, eventList, removeBtn, feedback, backBtn);
        return new Scene(vbox, 400, 400);
    }
    // Screen to view all attendees
    public Scene viewAttendees(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        Label label = new Label("All Attendees:");

        ListView<String> attendeeList = new ListView<>();

        for (int i = 0; i < Database.users.size(); i++) {
            if(Database.users.get(i) instanceof Attendee) {
                Attendee attendee = (Attendee) Database.users.get(i);
                attendeeList.getItems().add(attendee.getUsername() + " | " + attendee.getGender());
            }
        }

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        backBtn.setOnAction(e -> displayDashboard(stage));

        Button removeBtn = new Button("Remove Selected Attendee");
        removeBtn.setStyle(Database.menuButtonStyle);
        removeBtn.setOnAction(e -> {
            ArrayList<Attendee> attendees = new ArrayList<>();
            for (int i = 0 ; i < Database.users.size(); i++) {
                User user = Database.users.get(i);
                if (user instanceof Attendee) {
                    attendees.add((Attendee) user);
                }
            }
            int index = attendeeList.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                Attendee attendee = attendees.get(index);
                for(int i = 0; i < attendee.getRegisteredEvents().size(); i++) {
                    Event event = attendee.getRegisteredEvents().get(i);
                    event.getAttendees().remove(attendee);
                }
                Database.users.remove(attendee);
                attendeeList.getItems().remove(index);
            }
        });

        vbox.getChildren().addAll(label, attendeeList,removeBtn, backBtn);
        return new Scene(vbox, 400, 400);
    }
}
