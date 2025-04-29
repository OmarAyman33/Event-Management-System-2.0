package org.example.learningjavafx;

import java.time.LocalDate;
import java.util.ArrayList;
public class Event {
    private LocalDate date;
    private Room room;
    private Category category;
    private String name;
    private double price;
    private ArrayList<Attendee> attendees;
    private Organizer organizer;

    public Event(LocalDate date, Room room, Category category, String name, double price, Organizer organizer) {
        if(date.isAfter(LocalDate.now()))
            this.date = date;
        this.room = room;
        this.category = category;
        this.name = name;
        this.price = price;
        this.organizer = organizer;
        attendees = new ArrayList<Attendee>();
        room.bookEvent(this);
    }

    public void addAttendee(Attendee attendee){
        if(attendees.size()<= room.getCapacity()) {
            if (!attendees.contains(attendee))
                attendees.add(attendee);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public ArrayList<Attendee> getAttendees() {
        return attendees;
    }

    public Organizer getOrganizer(){
        return organizer;
    }

    public Room getRoom(){
        return room;
    }
}
