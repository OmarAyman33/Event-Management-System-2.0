package org.example.learningjavafx;

import javafx.stage.Stage;

enum Gender {
    MALE, FEMALE
}

public abstract class User {
    protected String username;
    protected String password;
    protected String dateOfBirth;
    protected Gender gender;

    // constructors
    public User(String username, String password, String dateOfBirth, Gender gender) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public User(){}

    // Getters
    public String getUsername() {
        return username;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    // Setters
    public void setPassword(String password) {
        this.password = password;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // centralized log in function as all loginable functions inherit from this class
    public boolean login(String username, String password){
        if (username == null || password == null) return false; // checking for nullptr exception

        if(this.username.equals(username))
        {
            if(this.password.equals(password.strip()))
                return true;
        }
        return false;
    }

    public void displayDashboard(Stage stage){}
}
