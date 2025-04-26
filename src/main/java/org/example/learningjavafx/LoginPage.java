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
        submitBtn.setStyle(Database.confirmButtonStyle);

        // hyperlink to allow switching between
        Hyperlink alternativeLink = new Hyperlink("Create new account");

        ComboBox<String> userTypeForm = new ComboBox<>(); // attendee or organizer
        userTypeForm.getItems().addAll("Attendee","Organizer");
        userTypeForm.setPromptText("Attendee/Organizer");

        alternativeLink.setOnAction(e->{
            // event for switching from login to register and vice versa
            if(loginMode){
                login.getChildren().clear();
                login.getChildren().addAll(usernameInput,passwordInput,genderInput,userTypeForm,submitBtn,alternativeLink);
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
                String username = usernameInput.getText();
                String password = passwordInput.getText();
                String gender = genderInput.getValue();
                String type = userTypeForm.getValue();
                // validate inputs and make a new account
                // then call on .displayDashboard()
                // still not complete, waiting for Organizer dashboard to be complete
            }
        });
        login.getChildren().addAll(usernameInput,passwordInput,alternativeLink,submitBtn);
        Scene scene = new Scene(login,300,300);
        stage.setScene(scene);
        stage.show();
    }
}