package org.example.learningjavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage extends Application {
    private boolean loginMode = true;
    public void start(Stage stage){
        stage.setTitle("Login");
        VBox login = new VBox();
        login.setSpacing(10); // to make the components not look tightly packed
        login.setPadding(new Insets(20));
        TextField usernameInput= new TextField();
        usernameInput.setPromptText("Username");

        PasswordField passwordInput = new PasswordField();
        passwordInput.setPromptText("Password");

        ComboBox<String> genderInput = new ComboBox<>(); // using combo box for drop down list
        genderInput.getItems().addAll("MALE","FEMALE");
        genderInput.setPromptText("Gender");

        Button submitBtn = new Button("Login");
        submitBtn.setStyle( // using CSS esque style to make the button look modern
                "-fx-background-color: #4CAF50;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-border-radius: 20px;" +
                "-fx-cursor: hand;"
        );

        // hyperlink to allow switching between
        Hyperlink alternativeLink = new Hyperlink("Create new account");

        ComboBox<String> userTypeForm = new ComboBox<>(); // attendee or organizer
        userTypeForm.getItems().addAll("Attendee","Organizer");
        userTypeForm.setPromptText("Attendee/Organizer");

        alternativeLink.setOnAction(e->{
            // event for switching from login to regisster and vice versa
            if(loginMode){
                login.getChildren().clear();
                login.getChildren().addAll(usernameInput,passwordInput,genderInput,userTypeForm,submitBtn,alternativeLink);
                stage.setTitle("Register");
                alternativeLink.setText("Already have an account?");
                loginMode = false;
            }
            else{
                login.getChildren().clear();
                login.getChildren().addAll(usernameInput,passwordInput,alternativeLink,submitBtn);
                stage.setTitle("Login");
                alternativeLink.setText("Create an account");
                loginMode = true;
            }
        });
        submitBtn.setOnAction(e->{
            if(loginMode) {
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                // call on functions from login class to validate and verify
                // if username and password are correct, call on user.DisplayDashboard()
            }
            else{
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                String gender = genderInput.getValue();
                String type = userTypeForm.getValue();
                // validate inputs and make a new account
                // then call on .displayDashboard()
            }
        });
        login.getChildren().addAll(usernameInput,passwordInput,alternativeLink,submitBtn);
        Scene scene = new Scene(login,300,300);
        stage.setScene(scene);
        stage.show();
    }
}
