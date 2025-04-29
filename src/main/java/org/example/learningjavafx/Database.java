package org.example.learningjavafx;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Database {
    public static List<User> users = new ArrayList<User>();
    public static List<Event> events = new ArrayList<Event>();
    public static List<Room> rooms = new ArrayList<Room>();
    public static List<Category> categories = new ArrayList<Category>();
    public static LoginAuth login = new LoginAuth();
    public static String menuButtonStyle =
            "-fx-background-color: #2196F3;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 12px;" +
            "-fx-border-radius: 12px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;";
    public static String confirmButtonStyle = "-fx-background-color: #F5F5F5;" +
            "-fx-text-fill: #333333;" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-color: #CCCCCC;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 8px 16px;" +
            "-fx-cursor: hand;";

    static {
        // === CATEGORIES ===
        Category tech = new Category("Tech");
        Category music = new Category("Music");
        Category art = new Category("Art");
        categories.add(tech);
        categories.add(music);
        categories.add(art);

        // === ROOMS ===
        Room r1 = new Room("Main Hall", 100,50);
        Room r2 = new Room("Workshop Room", 30,60);
        Room r3 = new Room("VIP Lounge", 10,70);
        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);

        // === ORGANIZERS ===
        Organizer org1 = new Organizer("org1", "pass1", "01/01/1990", Gender.MALE,4000);
        Organizer org2 = new Organizer("org2", "pass2", "15/03/1991", Gender.FEMALE,1500);
        Organizer org3 = new Organizer("org3", "pass3", "20/06/1992", Gender.MALE, 4000);
        users.add(org1);
        users.add(org2);
        users.add(org3);

        // === ADMINS ===
        Admin ad1 = new Admin("admin1", "adminpass1", "12/12/1985", Gender.FEMALE, "Supervisor", "9-6");
        Admin ad2 = new Admin("admin2", "adminpass2", "10/11/1988", Gender.MALE, "Coordinator", "10-7");
        Admin ad3 = new Admin("admin3", "adminpass3", "02/02/1980", Gender.FEMALE, "Director", "8-5");
        users.add(ad1);
        users.add(ad2);
        users.add(ad3);

        // === ATTENDEES ===
        Attendee att1 = new Attendee("att1", "attpass1", "05/05/2000", Gender.FEMALE, 100);
        Attendee att2 = new Attendee("att2", "attpass2", "09/09/1999", Gender.MALE, 120);
        Attendee att3 = new Attendee("att3", "attpass3", "11/11/1998", Gender.MALE, 90);
        users.add(att1);
        users.add(att2);
        users.add(att3);

        // === EVENTS ==
        events.add(new Event(LocalDate.now().plusDays(10), r1, tech, "Tech Expo 2025", 20.0, org1));
        events.add(new Event(LocalDate.now().plusDays(15), r2, music, "Jazz Night", 15.0, org2));
        events.add(new Event(LocalDate.now().plusDays(5), r3, art, "Art Showcase", 25.0, org3));
        events.add(new Event(LocalDate.now().plusDays(20), r1, music, "EDM Bash", 30.0, org1));
        events.add(new Event(LocalDate.now().plusDays(12), r2, tech, "AI Conference", 35.0, org2));
        events.add(new Event(LocalDate.now().plusDays(18), r3, art, "Digital Gallery", 18.0, org3));
        org1.addCreatedEvent(events.get(0)); org1.addCreatedEvent(events.get(3));
        org2.addCreatedEvent(events.get(1));org2.addCreatedEvent(events.get(4));
        org3.addCreatedEvent(events.get(2)); org3.addCreatedEvent(events.get(5));
    }
}
