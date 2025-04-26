package org.example.learningjavafx;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.util.Scanner;

public class LoginAuth {
    // start page of program
    Scanner input = new Scanner(System.in);

    public static void login(String username, String password, Stage stage){
        for (int i = 0; i < Database.users.size(); i++) {
            if (Database.users.get(i).login(username, password)) {
                Database.users.get(i).displayDashboard(stage);
                return;
            }
        }
    }


    public static boolean isUsernameTaken(String username) {
        for (int i = 0; i < Database.users.size(); i++) {
            if (Database.users.get(i).getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public static void register(String username, String password, String genderInput, String type, String dob, Stage stage) {
        int choice = 0;
        if (type.equals("Attendee"))
            choice = 1;
        else if (type.equals("Organizer"))
            choice = 2;

        // Create a VBox to display feedback to user
        VBox feedbackPane = new VBox();
        feedbackPane.setSpacing(10);
        Label messageLabel = new Label();
        feedbackPane.getChildren().add(messageLabel);

        Scene feedbackScene = new Scene(feedbackPane, 300, 150);
        stage.setScene(feedbackScene);

        // Username validation
        if (!Validation.username(username)) {
            messageLabel.setText("Invalid username. Please try again.");
            return;
        }
        if (isUsernameTaken(username)) {
            messageLabel.setText("Username already taken. Please choose another one.");
            return;
        }

        // Password validation
        if (!Validation.password(password)) {
            messageLabel.setText("Invalid password. Please try again.");
            return;
        }

        // Gender validation
        Gender gender;
        try {
            gender = Gender.valueOf(genderInput.toUpperCase());
        } catch (IllegalArgumentException e) {
            messageLabel.setText("Invalid gender. Please select Male or Female.");
            return;
        }

        // Create new user
        User newUser;
        if (choice == 1) {
            newUser = new Attendee(username, password, dob, gender);
        } else if (choice == 2) {
            newUser = new Organizer(username, password, dob, gender);
        } else {
            messageLabel.setText("Invalid user type.");
            return;
        }

        // Add to database and open dashboard
        Database.users.add(newUser);
        newUser.displayDashboard(stage);
    }
}
