package org.example.learningjavafx;

import java.util.ArrayList;

public class Category {
    private String name;
    private ArrayList<Event> events;

    public Category(String name) {
        this.name = name;
        events = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event){
        events.add(event);
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof Category)
        {
            if(((Category) o).getName().equals(this.name))
                return true;
        }
        return false;
    }
    //Simplfied for Organizer class
    @Override
    public String toString() {
        return this.name;
    }
}
