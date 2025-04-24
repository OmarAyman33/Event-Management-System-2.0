package org.example.learningjavafx;

import java.util.Scanner;

public class LoginAuth {
    // start page of program
    Scanner input = new Scanner(System.in);
    public void start(){
        int choice;
        System.out.println("Press 1. for Login, 2. for Registration, 3. to exit the program");
        choice = input.nextInt();
        input.nextLine(); // clear input
        if(choice == 1)
            login();
        else if (choice == 2)
            register();
        else if (choice == 3) {
            System.out.println("Thanks for using our program!");
            System.exit(0);
        } else
        {
            System.out.println("invalid choice, try again.");
            start();
        }
    }
    private void login(){
        String username;
        String password;
        System.out.println("Enter your username: ");
        username = input.nextLine();
        System.out.println("Enter your password: ");
        password = input.nextLine();
        for(int i = 0; i < Database.users.size(); i++)
        {
            if(Database.users.get(i).login(username,password))
                Database.users.get(i).displayDashboard();
        }
    }

    private boolean isUsernameTaken(String username) {
        for (int i = 0; i < Database.users.size(); i++) {
            if (Database.users.get(i).getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    private void register(){
        int choice;
        System.out.println("1. for Registration as Attendee, 2. for Registration as Organizer");
        choice = input.nextInt();
        input.nextLine(); // clear the scanner
        System.out.println("Enter your username: ");
        String username = input.nextLine();

        while (!Validation.username(username) || isUsernameTaken(username)) {
            if (!Validation.username(username)) {
                System.out.println("Invalid username. Try again.");
            } else {
                System.out.println("Username already taken. Try another.");
            }
            username = input.nextLine();
        }

        System.out.println("Enter your password: ");
        String password = input.nextLine();
        while (!Validation.password(password)){
            System.out.println("Invalid password. Try again");
            password = input.nextLine();
        }
        System.out.print("Enter your date of birth (DD/MM/YYYY): ");
        String dob = input.nextLine();

        System.out.print("Enter your gender (MALE / FEMALE): ");
        String genderInput = input.nextLine().toUpperCase();
        while (!genderInput.equals("MALE") && !genderInput.equals("FEMALE")) {
            System.out.println("Invalid input. Please enter MALE or FEMALE.");
            genderInput = input.nextLine().toUpperCase();
        }
        Gender gender = Gender.valueOf(genderInput);
        User newUser;
        if (choice == 1) {
            newUser = new Attendee(username, password, dob, gender);
        } else {
            newUser = new Organizer(username, password, dob, gender);
        }

        Database.users.add(newUser); // Add to user list
        newUser.displayDashboard();  // Call display dashboard
    }
}
