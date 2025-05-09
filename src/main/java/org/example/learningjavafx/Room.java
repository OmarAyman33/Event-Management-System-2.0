package org.example.learningjavafx;

import java.time.LocalDate;
import java.util.ArrayList;
//made by Ahmed Ashraf 16/4/2025


public class Room {

    private final String name;
    private final int capacity;
    private ArrayList<Event> events;
    private double price;

   //excibation handling and constructors
   public Room(String name, int capacity, double price) {
       if (name == null || name.trim().isEmpty())
           throw new IllegalArgumentException("Room name cannot be null or empty");
       if (capacity <= 0)
           throw new IllegalArgumentException("Capacity must be positive");
       if(price < 0){
           throw new IllegalArgumentException("Price can not be less than zero");
       }
       this.name = name.trim();
       this.capacity = capacity;
       this.events = new ArrayList<>();
       this.price = price;
   }


    // Core functionality
    /**
     * Checks if the room is available on a specific date
     * return true if available, false if already booked
     */

    public boolean isAvailableOn(LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date cannot be null");

        for (int i = 0; i < events.size(); i++) {
            if (date.equals(events.get(i).getDate())) {
                return false;
            }
        }
        return true;
    }

    public void bookEvent(Event event) {
        LocalDate date = event.getDate();
        if (isAvailableOn(date))
            events.add(event);
    }


    // Getters (no setters - rooms are immutable after creation)
    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<Event> getEvents() {
        return new ArrayList<>(events);
    }

    public void removeEvent(Event event){
        events.remove(event);
    }
     @Override
     public String toString() {
         return "Room: name= " + name + ", capacity= " + capacity;
     }
    // Equality check (important for database operations)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return name.equalsIgnoreCase(room.name);
    }


}
