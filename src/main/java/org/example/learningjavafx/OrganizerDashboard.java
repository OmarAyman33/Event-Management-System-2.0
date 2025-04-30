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
    private Organizer organizer;     // Instance variable to hold the specific Organizer logged in
    public OrganizerDashboard(Organizer organizer) {
            this.organizer = organizer;
    }
    public OrganizerDashboard() {
    }


    //DashBoard of organizer , Includes validation


    public void displayDashboard(Stage stage) {

        // Layout
        VBox dashboardLayout = new VBox();
        dashboardLayout.setSpacing(10);
        dashboardLayout.setPadding(new Insets(25));
        stage.setTitle("Organizer Dashboard - Welcome " + organizer.getUsername());

        // Menu Buttons
        Button viewMyEventsButton = new Button("View My Events");
        Button manageEventsButton = new Button("Manage Events (Create/Update/Delete)");
        Button viewAvailableRoomsButton = new Button("View Available Rooms");
        Button viewWalletButton = new Button("View Wallet / Balance");
        Button logoutButton = new Button("Log Out");

        //Styling (will apply css later)
        String menuStyle = Database.menuButtonStyle;
        viewMyEventsButton.setStyle(menuStyle);
        manageEventsButton.setStyle(menuStyle);
        viewAvailableRoomsButton.setStyle(menuStyle);
        viewWalletButton.setStyle(menuStyle);
        logoutButton.setStyle(menuStyle);

        //Fill Horizontal
        viewMyEventsButton.setMaxWidth(Double.MAX_VALUE);
        manageEventsButton.setMaxWidth(Double.MAX_VALUE);
        viewAvailableRoomsButton.setMaxWidth(Double.MAX_VALUE);
        viewWalletButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setMaxWidth(Double.MAX_VALUE);

        //Vbox
        dashboardLayout.getChildren().addAll(
                viewMyEventsButton,
                manageEventsButton,
                viewAvailableRoomsButton,
                viewWalletButton,
                logoutButton
        );

        // Button Actions (Main Dashboard)
        viewMyEventsButton.setOnAction(e -> {
            stage.setScene(viewMyEventsScene(stage));
        });

        manageEventsButton.setOnAction(e -> {
            stage.setScene(manageEventsScene(stage));

        });

        //baher
        viewAvailableRoomsButton.setOnAction(e -> {
            // TODO: Implement viewAvailableRoomsScene method
            // stage.setScene(viewAvailableRoomsScene(stage));
        });

        viewWalletButton.setOnAction(e -> {
            stage.setScene(manageWalletScene(stage));
        });

        logoutButton.setOnAction(e -> {
            // Navigate back to the Login Page
            System.out.println("Action: Logging out...");
            new LoginPage().start(stage); // Assumes LoginPage().start(stage) exists and works
        });

        // 6. Create and Show Scene
        Scene scene = new Scene(dashboardLayout, 400, 450);
        stage.setScene(scene);
        stage.show();
    }


    //Scenes

    //ViewMyevents Scene (Pulls from database events (Local arraylist) created by this specifc organizer)
    private Scene viewMyEventsScene(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Events You Created:");
        titleLabel.setFont(new Font(20));

        ListView<String> eventsListView = new ListView<>();

        if (this.organizer != null) {
            List<Event> organizerCreatedEvents = this.organizer.getMyCreatedEvents();

            if (organizerCreatedEvents != null && !organizerCreatedEvents.isEmpty()) {
                for (Event event : organizerCreatedEvents) {

                    String eventInfo = event.getName() + " - Date: " + event.getDate().toString() + " - Price: $" + event.getPrice();

                    eventsListView.getItems().add(eventInfo);
                }
            } else {
                eventsListView.getItems().add("You have not created any events yet.");
                eventsListView.setDisable(true);
            }
        } else {
            eventsListView.getItems().add("Error: Organizer data not available.");
            eventsListView.setDisable(true);
        }

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage)); // Go back to main dashboard

        vBox.getChildren().addAll(titleLabel, eventsListView, backBtn);
        return new Scene(vBox, 450, 400); // Adjust size
    }


    // ManageEvents Scene
    private Scene manageEventsScene(Stage stage) {
        // Layout
        VBox manageLayout = new VBox();
        manageLayout.setSpacing(10);
        manageLayout.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Manage Your Events");
        titleLabel.setFont(new Font(20));

        // Action Buttons
        Button createEventButton = new Button("Create New Event");
        Button updateEventButton = new Button("Update Existing Event");
        Button deleteEventButton = new Button("Delete Event");

        // Back Button
        Button backBtn = new Button("Back");

        // Styling (will apply css later)
        String menuStyle = Database.menuButtonStyle;
        String confirmStyle = Database.confirmButtonStyle;
        createEventButton.setStyle(menuStyle);
        updateEventButton.setStyle(menuStyle);
        deleteEventButton.setStyle(menuStyle);
        backBtn.setStyle(confirmStyle);

        // Fill Horizontal
        createEventButton.setMaxWidth(Double.MAX_VALUE);
        updateEventButton.setMaxWidth(Double.MAX_VALUE);
        deleteEventButton.setMaxWidth(Double.MAX_VALUE);
        backBtn.setMaxWidth(Double.MAX_VALUE); // Optional for Back button

        // Vbox
        manageLayout.getChildren().addAll(titleLabel, createEventButton, updateEventButton, deleteEventButton, backBtn);

        // Button Actions
        createEventButton.setOnAction(e -> {
            stage.setScene(createEventFormScene(stage));
        });

        updateEventButton.setOnAction(e -> {
            // stage.setScene(selectEventForUpdateScene(stage));
        });

        deleteEventButton.setOnAction(e -> {

            stage.setScene(deleteEventScene(stage));
        });

        backBtn.setOnAction(e -> displayDashboard(stage)); // Navigate back to main dashboard

        // Create and Return Scene
        return new Scene(manageLayout, 400, 300); // Adjust size as needed
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
                titleLabel,
                nameLabel, nameField,
                dateLabel, datePicker,
                priceLabel, priceField,
                categoryLabel, categoryComboBox,
                roomLabel, roomComboBox,
                saveButton, backButton
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






}