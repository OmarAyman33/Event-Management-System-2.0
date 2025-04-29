package org.example.learningjavafx;


import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class AttendeePage {
    private Attendee attendee;
    AttendeePage(Attendee attendee){this.attendee = attendee;}
    AttendeePage(){}
    public void displayDashboard(Stage stage ){
        /* main pane for this dashboard will be a VBox
        * all options will be presented as buttons
        * logout options sends user back to login page
        * */
        VBox Welcome=new VBox();
        Welcome.setSpacing(10);
        Welcome.setPadding(new Insets(20));
        stage.setTitle("Welcome");

        // menu buttons
        Button ViewAllAvailableEvents = new Button("View all available events");
        Button FilterEventsByInterest = new Button("View events by interest");
        Button viewMyEvents = new Button("View my events");
        Button LogOut = new Button("Log out");
        Button manageWallet = new Button("Manage Wallet");
        Button BuyTicket = new Button("Buy ticket");
        Button manageInterests = new Button("Manage Interests");

        //styling
        String style = Database.menuButtonStyle;
        ViewAllAvailableEvents.setStyle(style); FilterEventsByInterest.setStyle(style);viewMyEvents.setStyle(style);LogOut.setStyle(style);manageWallet.setStyle(style);BuyTicket.setStyle(style);manageInterests.setStyle(style);
        ViewAllAvailableEvents.setMaxWidth(Double.MAX_VALUE);FilterEventsByInterest.setMaxWidth(Double.MAX_VALUE);viewMyEvents.setMaxWidth(Double.MAX_VALUE);LogOut.setMaxWidth(Double.MAX_VALUE);manageWallet.setMaxWidth(Double.MAX_VALUE);BuyTicket.setMaxWidth(Double.MAX_VALUE);manageInterests.setMaxWidth(Double.MAX_VALUE);

        // adding nodes to vBox
        Welcome.getChildren().addAll(ViewAllAvailableEvents, FilterEventsByInterest,viewMyEvents , manageWallet, BuyTicket,manageInterests,  LogOut
        );
        // setting button actions to curresponding pages
        viewMyEvents.setOnAction(e->stage.setScene(viewMyEvents(stage)));
        ViewAllAvailableEvents.setOnAction(e -> stage.setScene(viewAllEvents(stage)));
        FilterEventsByInterest.setOnAction(e -> stage.setScene(viewEventsbyInterest(stage)));
        manageWallet.setOnAction(e -> stage.setScene(manageWallet(stage)));
        LogOut.setOnAction(e->new LoginPage().start(stage));
        BuyTicket.setOnAction(e-> stage.setScene(buyTicket(stage)));
        manageInterests.setOnAction(e -> stage.setScene(manageInterests(stage)));

        Scene scene = new Scene(Welcome, 300, 400);
        stage.setScene(scene);
        stage.show();
    }
    /*
    * From here on out the following functions will be pages corresponding to the above buttons
    * each function will return a scene which will be set as the stage scene in the buttons' event
    * all scenes are in a vBox structure with the same spacing and padding
    * */
    // Displays a list of all future events available for booking
    public Scene viewAllEvents(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Available Events:");
        titleLabel.setFont(new Font(20));

        ListView<String> events = new ListView<>();
        // Loop through all events in the database
        for (int i = 0; i < Database.events.size(); i++) {
            Event event = Database.events.get(i);
            // Only show events that haven't already happened
            if (event.getDate().isAfter(LocalDate.now()))
                events.getItems().add(event.getName() + " - $" + event.getPrice() + " " + event.getDate());
        }

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage)); // Go back to dashboard

        vBox.getChildren().addAll(titleLabel, events, backBtn);
        return new Scene(vBox, 300, 300);
    }

    // Displays a list of events the attendee has registered for
    public Scene viewMyEvents(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Your Events:");
        titleLabel.setFont(new Font(20));

        ListView<String> events = new ListView<>();
        ArrayList<Event> attendeeRegisteredEvents = attendee.getRegisteredEvents();

        // Loop through user's registered events
        for (int i = 0; i < attendeeRegisteredEvents.size(); i++) {
            Event event = attendeeRegisteredEvents.get(i);
            // Only show future events
            if (event.getDate().isAfter(LocalDate.now()))
                events.getItems().add(event.getName() + " - $" + event.getPrice() + " " + event.getDate());
        }

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage));

        Button refundTicket = new Button("refund selected event ticket");
        refundTicket.setStyle(Database.menuButtonStyle);
        Label successLabel = new Label();
        refundTicket.setOnAction(e->{
            if(events.getSelectionModel() == null)
                return;
            int index = events.getSelectionModel().getSelectedIndex();
            ArrayList<Event> futureEvents = new ArrayList<>();
            for(int i = 0 ; i < attendeeRegisteredEvents.size(); i++)
            {
                if(attendeeRegisteredEvents.get(i).getDate().isAfter(LocalDate.now()))
                    futureEvents.add(attendeeRegisteredEvents.get(i));
            }
            attendee.removeEvent(futureEvents.get(index));
            successLabel.setText("event removed successfully");
            events.refresh();
        });

        vBox.getChildren().addAll(titleLabel, events,refundTicket, backBtn,successLabel);
        return new Scene(vBox, 300, 300);
    }

    // Manage wallet balance: deposit funds
    public Scene manageWallet(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        stage.setTitle("Wallet Management");

        Label titleLabel = new Label("Manage Wallet");
        titleLabel.setFont(new Font(20));

        // Display current wallet balance
        Label balanceLabel = new Label("Your current Balance: " + attendee.getWallet().getBalance());

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to add");

        Button depositBtn = new Button("Deposit Funds");

        // Handle deposit action
        depositBtn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    balanceLabel.setText("Enter a positive amount.");
                    return;
                }
                attendee.getWallet().addFunds(amount);
                balanceLabel.setText("Your current Balance: $" + attendee.getWallet().getBalance());
                amountField.clear();
            } catch (Exception ex) {
                balanceLabel.setText("Invalid Input.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage));

        vBox.getChildren().addAll(titleLabel, balanceLabel, amountField, depositBtn, backBtn);
        return new Scene(vBox, 300, 300);
    }

    // View events related to attendee's personal interests
    public Scene viewEventsbyInterest(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Events Matching Your Interests:");
        titleLabel.setFont(new Font(20));

        ListView<String> eventsList = new ListView<>();
        ArrayList<Category> interests = attendee.getInterests();

        // Loop through all events and match them to interests
        for (int i = 0; i < Database.events.size(); i++) {
            Event event = Database.events.get(i);
            if (event.getDate().isAfter(LocalDate.now())) {
                for (int j = 0; j < interests.size(); j++) {
                    if (event.getCategory().equals(interests.get(j))) {
                        eventsList.getItems().add(event.getName() + " - $" + event.getPrice() + " - " + event.getDate());
                        break;
                    }
                }
            }
        }

        // If no events match, show message
        if (eventsList.getItems().isEmpty()) {
            eventsList.getItems().add("No events match your interests.");
        }

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage));

        vBox.getChildren().addAll(titleLabel, eventsList, backBtn);
        return new Scene(vBox, 300, 300);
    }

    // Buy tickets for available events
    public Scene buyTicket(Stage stage) {
        VBox BuyTicket = new VBox();
        BuyTicket.setSpacing(10);
        BuyTicket.setPadding(new Insets(10));
        stage.setTitle("Buy Ticket");

        ArrayList<Event> events = new ArrayList<>();
        ComboBox<String> futureEvents = new ComboBox<>();
        futureEvents.setPromptText("Select the event");

        // Load events into combo box
        for (int i = 0; i < Database.events.size(); i++) {
            Event event = Database.events.get(i);
            if (event.getDate().isAfter(LocalDate.now()) && !attendee.getRegisteredEvents().contains(event)) {
                futureEvents.getItems().add(event.getName() + " - $" + event.getPrice() + " " + event.getDate());
                events.add(event);
            }
        }

        Button buyTicket = new Button("Buy the Ticket");
        Label successMessage = new Label();
        Button backBtn = new Button("Back");

        BuyTicket.getChildren().addAll(futureEvents, buyTicket, backBtn);

        // Buying action logic
        buyTicket.setOnAction(e -> {
            int index = futureEvents.getItems().indexOf(futureEvents.getValue());
            Event event = events.get(index);

            if (attendee.buyTicket(event)) {
                successMessage.setText("Transaction successful");
                futureEvents.getItems().remove(index);
                events.remove(index);
                BuyTicket.getChildren().add(successMessage);
            } else {
                successMessage.setText("Insufficient Funds, Transaction unsuccessful");
                BuyTicket.getChildren().add(successMessage);
            }
        });

        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage));

        return new Scene(BuyTicket, 300, 300);
    }

    // Manage user's interests (add new interests)
    public Scene manageInterests(Stage stage) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Manage Interests:");
        titleLabel.setFont(new Font(20));

        ListView<String> interestsList = new ListView<>();
        // Show current interests
        for (int i = 0; i < attendee.getInterests().size(); i++) {
            interestsList.getItems().add(attendee.getInterests().get(i).getName());
        }

        // Available categories to add
        ComboBox<String> availableCategories = new ComboBox<>();
        availableCategories.setPromptText("Add a new interest");

        for (int i = 0; i < Database.categories.size(); i++) {
            Category cat = Database.categories.get(i);
            if (!attendee.getInterests().contains(cat)) {
                availableCategories.getItems().add(cat.getName());
            }
        }

        Button addInterestBtn = new Button("Add Interest");
        Label feedbackLabel = new Label();

        // Add selected category to interests
        addInterestBtn.setOnAction(e -> {
            String selectedCategory = availableCategories.getValue();
            if (selectedCategory != null) {
                for (int i = 0; i < Database.categories.size(); i++) {
                    Category cat = Database.categories.get(i);
                    if (cat.getName().equals(selectedCategory)) {
                        attendee.addInterest(cat);
                        interestsList.getItems().add(cat.getName());
                        availableCategories.getItems().remove(selectedCategory);
                        feedbackLabel.setText("Interest added successfully!");
                        break;
                    }
                }
            } else {
                feedbackLabel.setText("Please select a category.");
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setStyle(Database.confirmButtonStyle);
        backBtn.setOnAction(e -> displayDashboard(stage));

        vBox.getChildren().addAll(titleLabel, interestsList, availableCategories, addInterestBtn, feedbackLabel, backBtn);
        return new Scene(vBox, 300, 400);
    }


}