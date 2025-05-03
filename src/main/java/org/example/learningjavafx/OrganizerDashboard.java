package org.example.learningjavafx; // Use your actual package

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.control.Alert.AlertType;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class OrganizerDashboard {
    private Organizer organizer;
    public OrganizerDashboard(Organizer organizer) { this.organizer = organizer; }
    public OrganizerDashboard() {}

    public void displayDashboard(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(25));
        stage.setTitle("Organizer Dashboard - Welcome " + organizer.getUsername());

        Button viewMyEvents = new Button("View My Events");
        Button manageEvents = new Button("Manage Events (Create/Update/Delete)");
        Button viewAvailableRooms = new Button("View Available Rooms");
        Button viewWallet = new Button("View Wallet / Balance");
        Button logout = new Button("Log Out");

        String style = Database.menuButtonStyle;
        viewMyEvents.setStyle(style);
        manageEvents.setStyle(style);
        viewAvailableRooms.setStyle(style);
        viewWallet.setStyle(style);
        logout.setStyle(style);

        viewMyEvents.setMaxWidth(Double.MAX_VALUE);
        manageEvents.setMaxWidth(Double.MAX_VALUE);
        viewAvailableRooms.setMaxWidth(Double.MAX_VALUE);
        viewWallet.setMaxWidth(Double.MAX_VALUE);
        logout.setMaxWidth(Double.MAX_VALUE);

        layout.getChildren().addAll(viewMyEvents, manageEvents, viewAvailableRooms, viewWallet, logout);

        viewMyEvents.setOnAction(e -> stage.setScene(viewMyEventsScene(stage)));
        manageEvents.setOnAction(e -> stage.setScene(manageEventsScene(stage)));
        viewAvailableRooms.setOnAction(e -> stage.setScene(viewAvailableRoomsScene(stage)));
        viewWallet.setOnAction(e -> stage.setScene(manageWalletScene(stage)));
        logout.setOnAction(e -> new LoginPage().start(stage));

        stage.setScene(new Scene(layout, 400, 450));
        stage.show();
    }

    private Scene viewMyEventsScene(Stage stage) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        Label title = new Label("Events You Created:");
        title.setFont(new Font(20));
        ListView<String> list = new ListView<>();
        List<Event> events = organizer.getMyCreatedEvents();

        if (events != null && !events.isEmpty()) {
            for (Event e : events) list.getItems().add(e.getName() + " - Date: " + e.getDate() + " - Price: $" + e.getPrice());
        } else {
            list.getItems().add("You have not created any events yet.");
            list.setDisable(true);
        }

        Button back = new Button("Back");
        back.setStyle(Database.confirmButtonStyle);
        back.setOnAction(e -> displayDashboard(stage));

        vbox.getChildren().addAll(title, list, back);
        return new Scene(vbox, 450, 400);
    }

    private Scene manageEventsScene(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Manage Your Events");
        title.setFont(new Font(20));

        Button create = new Button("Create New Event");
        Button update = new Button("Update Existing Event");
        Button delete = new Button("Delete Event");
        Button back = new Button("Back");

        String style = Database.menuButtonStyle;
        create.setStyle(style); update.setStyle(style); delete.setStyle(style);
        back.setStyle(Database.confirmButtonStyle);

        create.setMaxWidth(Double.MAX_VALUE);
        update.setMaxWidth(Double.MAX_VALUE);
        delete.setMaxWidth(Double.MAX_VALUE);
        back.setMaxWidth(Double.MAX_VALUE);

        layout.getChildren().addAll(title, create, update, delete, back);

        create.setOnAction(e -> stage.setScene(createEventFormScene(stage)));
        update.setOnAction(e -> stage.setScene(selectEventForUpdateScene(stage)));
        delete.setOnAction(e -> stage.setScene(deleteEventScene(stage)));
        back.setOnAction(e -> displayDashboard(stage));

        return new Scene(layout, 400, 300);
    }


    //Create Event Scene

    private Scene createEventFormScene(Stage stage) {
        // Layout
        VBox formLayout = new VBox();
        formLayout.setSpacing(10);
        formLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Create New Event");
        titleLabel.setFont(new Font(20));

        // --- Input Controls ---
        Label nameLabel = new Label("Event Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter the name of the event");

        Label dateLabel = new Label("Event Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select event date");

        Label priceLabel = new Label("Ticket Price ($):");
        TextField priceField = new TextField();
        priceField.setPromptText("e.g., 25.00");

        Label categoryLabel = new Label("Category:");
        ComboBox<Category> categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Select category");

        // Populate Category ComboBox
        categoryComboBox.getItems().addAll(Database.categories);

        Label roomLabel = new Label("Room (Select Date First):");
        ComboBox<Room> roomComboBox = new ComboBox<>();
        roomComboBox.setPromptText("Select date first");
        roomComboBox.setDisable(true);

        // Action Buttons
        Button saveButton = new Button("Save Event");
        Button backButton = new Button("Back");

        // Styling
        saveButton.setStyle(Database.menuButtonStyle);
        backButton.setStyle(Database.confirmButtonStyle);

        // --- Add Controls to Layout ---
        formLayout.getChildren().addAll(
                titleLabel, nameLabel, nameField, dateLabel, datePicker, priceLabel, priceField, categoryLabel, categoryComboBox, roomLabel, roomComboBox, saveButton, backButton
        );

        // Actions
        datePicker.setOnAction(e -> {
            populateAvailableRooms(roomComboBox, datePicker.getValue());
        });

        saveButton.setOnAction(e -> {
            handleSaveEvent(stage, nameField, datePicker, priceField, categoryComboBox, roomComboBox);
        });

        backButton.setOnAction(e -> {
            stage.setScene(manageEventsScene(stage));
        });

        // Create and Return Scene
        return new Scene(formLayout, 450, 500);
    }

    // Helper method to populate rooms
    private void populateAvailableRooms(ComboBox<Room> roomCombo, LocalDate selectedDate) {
        roomCombo.getItems().clear();
        roomCombo.setDisable(true);
        roomCombo.setPromptText("Select room");

        if (selectedDate == null) {
            roomCombo.setPromptText("Select date first");
            return;
        }
        if (selectedDate.isBefore(LocalDate.now())) {
            new Alert(AlertType.WARNING, "Event date must be in the future.").showAndWait();
            roomCombo.setPromptText("Select date first");
            return;
        }

        boolean roomFound = false;
        for (Room room : Database.rooms) {
            if (room.isAvailableOn(selectedDate)) {
                roomCombo.getItems().add(room);
                roomFound = true;
            }
        }
        if (roomFound) {
            roomCombo.setDisable(false);
        } else {
            roomCombo.setPromptText("No rooms available on " + selectedDate);
        }
    }

    // Helper method for save logic
    private void handleSaveEvent(Stage stage, TextField nameField, DatePicker datePicker, TextField priceField,
                                 ComboBox<Category> categoryComboBox, ComboBox<Room> roomComboBox) {

        //  Get Inputs
        String eventName = nameField.getText();
        LocalDate eventDate = datePicker.getValue();
        String priceText = priceField.getText();
        Category selectedCategory = categoryComboBox.getValue();
        Room selectedRoom = roomComboBox.getValue();

        // Basic validation
        if (eventName == null || eventName.trim().isEmpty()) { new Alert(AlertType.WARNING, "Please enter an Event Name.").showAndWait(); return; }
        if (eventDate == null) { new Alert(AlertType.WARNING, "Please select an Event Date.").showAndWait(); return; }
        if (eventDate.isBefore(LocalDate.now())) { new Alert(AlertType.WARNING, "Event Date must be in the future.").showAndWait(); return; }
        if (priceText == null || priceText.trim().isEmpty()) { new Alert(AlertType.WARNING, "Please enter a Ticket Price.").showAndWait(); return; }
        if (selectedCategory == null) { new Alert(AlertType.WARNING, "Please select a Category.").showAndWait(); return; }
        if (selectedRoom == null) { new Alert(AlertType.WARNING, "Please select an available Room.").showAndWait(); return; }

        // Price Validation
        double eventPrice;
        try {
            eventPrice = Double.parseDouble(priceText);
            if (eventPrice < 0) { new Alert(AlertType.WARNING, "Price cannot be negative.").showAndWait(); return; }
        } catch (NumberFormatException ex) {
            new Alert(AlertType.WARNING, "Invalid Price. Please enter a valid number.").showAndWait(); return;
        }

        boolean success = this.organizer.createEvent(eventName, eventDate, eventPrice, selectedCategory, selectedRoom);

        // FeedBack
        if (success) {
            Alert successAlert = new Alert(AlertType.INFORMATION, "Event '" + eventName + "' created successfully!");
            successAlert.setHeaderText(null);
            successAlert.showAndWait();
            stage.setScene(manageEventsScene(stage)); // Go back
        } else {
            Alert errorAlert = new Alert(AlertType.ERROR, "Failed to create event.\nPossible reasons: Room unavailable, insufficient funds, or other error.");
            errorAlert.setHeaderText("Creation Failed");
            errorAlert.showAndWait();
        }

    }

    private Scene deleteEventScene(Stage stage) {
        // Layout setup
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20));

        // Title Label
        Label label = new Label("Delete Event:");
        label.setFont(new Font(20));

        // ComboBox for Event Selection
        ComboBox<Event> eventComboBox = new ComboBox<>();
        eventComboBox.setPromptText("Select event to remove");
        eventComboBox.setMaxWidth(Double.MAX_VALUE);

        // Populate ComboBox (Using tostring from Events)
        if (this.organizer != null && this.organizer.getMyCreatedEvents() != null && !this.organizer.getMyCreatedEvents().isEmpty()) {
            eventComboBox.getItems().addAll(this.organizer.getMyCreatedEvents());
        } else {
            eventComboBox.setPromptText("No events available to delete");
            eventComboBox.setDisable(true);
        }

        // Action Buttons
        Button removeBtn = new Button("Remove Selected Event");
        removeBtn.setStyle(Database.menuButtonStyle);
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        // Set Buttons
        removeBtn.setOnAction(e -> {
            Event selectedEvent = eventComboBox.getValue(); // Get the selected Event object directly

            if (selectedEvent != null) { // Check if something was selected
                // Confirmation Dialog
                Alert confirmAlert = new Alert(
                        AlertType.CONFIRMATION,
                        "Are you sure you want to delete the event: '" + selectedEvent.getName() + "'?",
                        ButtonType.YES, ButtonType.NO
                );
                confirmAlert.setHeaderText("Confirm Deletion");
                Optional<ButtonType> result = confirmAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.YES) {
                    // User confirmed
                    boolean success = this.organizer.deleteEvent(selectedEvent);

                    if (success) {
                        new Alert(AlertType.INFORMATION, "Event removed successfully.").showAndWait();
                        stage.setScene(manageEventsScene(stage));
                    } else {
                        new Alert(AlertType.ERROR, "Failed to remove event.").showAndWait();
                    }
                } else {
                    // User cancelled
                    System.out.println("Deletion cancelled.");
                }
            } else {
                // Nothing selected in ComboBox
                new Alert(AlertType.WARNING, "Please select an event to remove.").showAndWait();
            }
        });

        backBtn.setOnAction(e -> stage.setScene(manageEventsScene(stage))); // Back to manage

        vbox.getChildren().addAll(label, eventComboBox, removeBtn, backBtn);

        // Create and return the scene
        return new Scene(vbox, 400, 300); // Adjust size
    }
    private Scene manageWalletScene(Stage stage) {
        // Layout
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        // Set title specific to Organizer
        stage.setTitle(this.organizer.getUsername() + " - Wallet Management");

        // Title Label
        Label titleLabel = new Label("Manage Your Wallet");
        titleLabel.setFont(new Font(20));

        // Display current wallet balance
        Label balanceLabel = new Label("Current Balance: $" + this.organizer.getWallet().getBalance());

        // Input for deposit amount
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to deposit");

        // Deposit Button
        Button depositBtn = new Button("Deposit Funds");
        depositBtn.setStyle(Database.menuButtonStyle); // Use menu style

        // Back Button
        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);

        // Handle deposit action , While using Alerts
        depositBtn.setOnAction(e -> {
            try {
                String amountText = amountField.getText();
                if (amountText == null || amountText.trim().isEmpty()){
                    new Alert(AlertType.WARNING, "Please enter an amount.").showAndWait();
                    return;
                }
                double amount = Double.parseDouble(amountText);
                if (amount <= 0) {
                    new Alert(AlertType.WARNING, "Deposit amount must be positive.").showAndWait();
                    return;
                }


                this.organizer.getWallet().addFunds(amount);

                // Update the balance display label
                balanceLabel.setText("Current Balance: $" + this.organizer.getWallet().getBalance());
                amountField.clear();


               //Erors and Validation
            } catch (NumberFormatException ex) {
                new Alert(AlertType.ERROR, "Invalid Amount. Please enter a number.").showAndWait();
            } catch (Exception ex) {
                new Alert(AlertType.ERROR, "An error occurred during deposit: " + ex.getMessage()).showAndWait();
                ex.printStackTrace();
            }
        });

        // Handle back button action
        backBtn.setOnAction(e -> displayDashboard(stage)); // Go back to main dashboard

        // Add controls to layout
        vBox.getChildren().addAll(titleLabel, balanceLabel, amountField, depositBtn, backBtn);

        // Create and return scene
        return new Scene(vBox, 350, 250); // Adjust size
    }

    private Scene selectEventForUpdateScene(Stage stage) {
        VBox layout = new VBox(10); // Use VBox like delete scene
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Select Event to Update:");
        titleLabel.setFont(new Font(20));

        ComboBox<Event> eventComboBox = new ComboBox<>();
        eventComboBox.setPromptText("Choose an event you created...");
        eventComboBox.setMaxWidth(Double.MAX_VALUE); // Stretch

        // Populate with ONLY the organizer's events
        if (this.organizer != null) {
            List<Event> myEvents = this.organizer.getMyCreatedEvents();
            if (myEvents != null && !myEvents.isEmpty()) {
                eventComboBox.getItems().addAll(myEvents);
            } else {
                eventComboBox.setPromptText("You have no events to update.");
                eventComboBox.setDisable(true);
            }
        } else {
            eventComboBox.setPromptText("Error: Organizer data unavailable.");
            eventComboBox.setDisable(true);
            new Alert(AlertType.ERROR, "Cannot load events: Organizer data missing.").showAndWait();
        }

        Button selectButton = new Button("Edit Selected Event");
        selectButton.setStyle(Database.menuButtonStyle); // Style
        selectButton.setMaxWidth(Double.MAX_VALUE);
        selectButton.setDisable(true); // Disable until selection

        // Enable button only when an event is selected
        eventComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectButton.setDisable(newVal == null);
        });

        // Action: Go to the form scene when the button is clicked
        selectButton.setOnAction(e -> {
            Event selectedEvent = eventComboBox.getValue();
            if (selectedEvent != null) {
                // Pass the selected event to the form scene
                stage.setScene(updateEventFormScene(stage, selectedEvent));
            }
            // No else needed, button should be disabled if null
        });

        Button backButton = new Button("Back");
        backButton.setStyle(Database.confirmButtonStyle); // Style
        backButton.setMaxWidth(Double.MAX_VALUE);
        // Back to the main manage events menu
        backButton.setOnAction(e -> stage.setScene(manageEventsScene(stage)));

        layout.getChildren().addAll(titleLabel, eventComboBox, selectButton, backButton);
        return new Scene(layout, 400, 250); // Size similar to delete scene
    }

    private void populateAvailableRooms(ComboBox<Room> roomBox, LocalDate date, Event currentEvent) {
        roomBox.getItems().clear();
        roomBox.setDisable(true);

        if (date == null || date.isBefore(LocalDate.now())) {
            roomBox.setPromptText("Select valid future date");
            return;
        }

        for (Room room : Database.rooms) {
            boolean isBooked = !room.isAvailableOn(date) && !room.equals(currentEvent.getRoom());
            if (!isBooked) roomBox.getItems().add(room);
        }

        roomBox.setDisable(roomBox.getItems().isEmpty());
        if (roomBox.getItems().isEmpty()) roomBox.setPromptText("No rooms available");
    }

    // --- Step 2: Scene for the UPDATE FORM ---
    // (Layout similar to createEventFormScene, takes Event parameter)
    private Scene updateEventFormScene(Stage stage, Event event) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label title = new Label("Update Event: " + event.getName());
        title.setFont(new Font(20));

        TextField nameField = new TextField(event.getName());
        DatePicker datePicker = new DatePicker(event.getDate());
        TextField priceField = new TextField(String.format("%.2f", event.getPrice()));
        ComboBox<Category> categoryBox = new ComboBox<>();
        ComboBox<Room> roomBox = new ComboBox<>();

        categoryBox.getItems().addAll(Database.categories);
        categoryBox.setValue(event.getCategory());

        roomBox.setDisable(true);
        datePicker.setOnAction(e -> populateAvailableRooms(roomBox, datePicker.getValue(), event));
        populateAvailableRooms(roomBox, event.getDate(), event);
        roomBox.setValue(event.getRoom());

        Button saveBtn = new Button("Save Changes"), backBtn = new Button("Back");
        saveBtn.setStyle(Database.menuButtonStyle);
        backBtn.setStyle(Database.confirmButtonStyle);

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String priceText = priceField.getText().trim();
            LocalDate date = datePicker.getValue();
            Category category = categoryBox.getValue();
            Room room = roomBox.getValue();

            if (name.isEmpty() || priceText.isEmpty() || date == null || category == null || room == null) {
                new Alert(AlertType.WARNING, "Please fill all fields.").showAndWait(); return;
            }
            if (date.isBefore(LocalDate.now())) {
                new Alert(AlertType.WARNING, "Date must be in the future.").showAndWait(); return;
            }

            double price;
            try {
                price = Double.parseDouble(priceText);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                new Alert(AlertType.WARNING, "Invalid price.").showAndWait(); return;
            }

            boolean changedDate = !date.equals(event.getDate());
            boolean changedRoom = !room.equals(event.getRoom());
            if ((changedDate || changedRoom) && !room.isAvailableOn(date)) {
                new Alert(AlertType.WARNING, "Selected room is unavailable on that date.").showAndWait(); return;
            }

            if (organizer.updateEvent(event, name, date, price, category, room)) {
                new Alert(AlertType.INFORMATION, "Event updated!").showAndWait();
                stage.setScene(manageEventsScene(stage));
            } else {
                new Alert(AlertType.ERROR, "Update failed.").showAndWait();
            }
        });

        backBtn.setOnAction(e -> stage.setScene(selectEventForUpdateScene(stage)));

        layout.getChildren().addAll(
                title,
                new Label("Name:"), nameField,
                new Label("Date:"), datePicker,
                new Label("Price ($):"), priceField,
                new Label("Category:"), categoryBox,
                new Label("Room:"), roomBox,
                saveBtn, backBtn
        );


        return new Scene(layout, 450, 500);
    }



    private Scene viewAvailableRoomsScene(Stage stage) {
        // Layout
        VBox formLayout = new VBox();
        formLayout.setSpacing(10);
        formLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("View Available Rooms");
        titleLabel.setFont(new Font(20));

        // Date Picker to select date
        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select date to view availability");

        // ListView to show available rooms
        ListView<String> availableRoomsList = new ListView<>();

        // Action Buttons
        Button checkBtn = new Button("Check Available Rooms");
        Button backBtn = new Button("Back");
        checkBtn.setStyle(Database.menuButtonStyle);
        backBtn.setStyle(Database.confirmButtonStyle);

        // Button Actions
        checkBtn.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            availableRoomsList.getItems().clear();

            if (selectedDate == null) {
                new Alert(AlertType.WARNING, "Please select a date.").showAndWait();
                return;
            }

            boolean found = false;
            for (Room room : Database.rooms) {
                if (room.isAvailableOn(selectedDate)) {
                    availableRoomsList.getItems().add("Room: " + room.getName() + " (Capacity: " + room.getCapacity() + ")");
                    found = true;
                }
            }

            if (!found) {
                availableRoomsList.getItems().add("No rooms available on " + selectedDate);
            }
        });

        backBtn.setOnAction(e -> displayDashboard(stage));

        // Add all elements to layout
        formLayout.getChildren().addAll(
                titleLabel,
                dateLabel, datePicker,
                checkBtn,
                availableRoomsList,
                backBtn
        );

        return new Scene(formLayout, 450, 500);
    }
}