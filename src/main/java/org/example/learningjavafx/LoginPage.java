package org.example.learningjavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;

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
        submitBtn.setStyle(Database.confirmButtonStyle);

        // hyperlink to allow switching between
        Hyperlink alternativeLink = new Hyperlink("Create new account");

        ComboBox<String> userTypeForm = new ComboBox<>(); // attendee or organizer
        userTypeForm.getItems().addAll("Attendee","Organizer");
        userTypeForm.setPromptText("Attendee/Organizer");
        // using date picker as it makes validation of dates much easier or pretty much non existent
        // we only have to check for date validity and that a date was inputted and we dont have to worry about the format
        DatePicker dobInput = new DatePicker();
        dobInput.setPromptText("Date of Birth");

        // Feedback label to show errors
        Label feedbackLabel = new Label();

        alternativeLink.setOnAction(e->{
            // event for switching from login to register and vice versa
            if(loginMode){
                login.getChildren().clear();
                login.getChildren().addAll(usernameInput,passwordInput,genderInput,userTypeForm,dobInput,submitBtn,alternativeLink,feedbackLabel);
                stage.setTitle("Register");
                submitBtn.setText("Register");
                alternativeLink.setText("Already have an account?");
                loginMode = false;
            }
            else{
                login.getChildren().clear();
                login.getChildren().addAll(usernameInput,passwordInput,alternativeLink,submitBtn);
                stage.setTitle("Login");
                alternativeLink.setText("Create an account");
                submitBtn.setText("Login");
                loginMode = true;
            }
        });

        submitBtn.setOnAction(e->{
            if(loginMode) {
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                LoginAuth.login(username,password,stage);
                // note that the updated login function already displays the user's dashboard

            }
            else{
                // Register mode
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                String gender = genderInput.getValue();
                String type = userTypeForm.getValue();
                LocalDate dob;
                try {
                    dob = dobInput.getValue();
                } catch (RuntimeException ex) {
                    feedbackLabel.setText("invalid date format");
                    return;
                }
                // Check if any input is missing
                if (username.isEmpty() || password.isEmpty() || gender == null || type == null || dob == null) {
                    feedbackLabel.setText("Please fill all fields.");
                    return;
                }

                // Validate username
                if (!Validation.username(username)) {
                    feedbackLabel.setText("Invalid username format.");
                    return;
                }
                if (LoginAuth.isUsernameTaken(username)) {
                    feedbackLabel.setText("Username already taken.");
                    return;
                }

                // Validate password
                if (!Validation.password(password)) {
                    feedbackLabel.setText("Invalid password format.");
                    return;
                }
                // date of birth validation
                if(dob.isAfter(LocalDate.now().minusYears(18))){
                    feedbackLabel.setText("You must be at least 18 years old.");
                    return;
                }

                // Validate gender input
                Gender userGender = Gender.valueOf(gender.toUpperCase());

                // Create user and add to database
                User newUser;
                if (type.equals("Attendee")) {
                    newUser = new Attendee(username, password, dob.toString(), userGender);
                } else if (type.equals("Organizer")) {
                    newUser = new Organizer(username, password, dob.toString(), userGender);
                } else {
                    feedbackLabel.setText("Invalid user type.");
                    return;
                }

                Database.users.add(newUser);
                newUser.displayDashboard(stage); // Move to dashboard if success
            }
        });
        login.getChildren().addAll(usernameInput,passwordInput,alternativeLink,submitBtn);
        Scene scene = new Scene(login,300,300);
        stage.setScene(scene);
        stage.show();
    }
}