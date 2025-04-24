package org.example.learningjavafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.control.Label;

import java.time.LocalDate;

public class AttendeePage {
    private Attendee attendee;
    AttendeePage(Attendee attendee){
        this.attendee = attendee;
    }
    public void displayDashboard(){
        Stage stage = new Stage();
        VBox dashboard = new VBox();
        dashboard.setSpacing(10);
        dashboard.setPadding(new Insets(20));
        Label welcome = new Label("Welcome " + attendee.getUsername());

        Button viewEventsBtn = new Button("View All Upcoming Events");
        Button interestEventsBtn = new Button("View Events by Interest");
        Button buyTicketBtn = new Button("Buy Ticket");
        Button myEventsBtn = new Button("My Events");
        Button walletBtn = new Button("Wallet");
        Button addInterstsBtn = new Button("Add Interests");
        Button logoutBtn = new Button("Logout");

        dashboard.getChildren().addAll(welcome,viewEventsBtn,interestEventsBtn,buyTicketBtn,myEventsBtn,walletBtn, addInterstsBtn, logoutBtn);
        Scene AttendeeDashboard = new Scene(dashboard,300,300);
        stage.setScene(AttendeeDashboard);
        stage.setTitle("Dashboard");

        viewEventsBtn.setOnAction(e->{
            dashboard.getChildren().clear();
            stage.setTitle("Avalaible Events");
            ListView<String> eventList = new ListView<>();
            for(int i = 0 ; i < Database.events.size(); i++)
            {
                Event event = Database.events.get(i);
                if (event.getDate().isAfter(LocalDate.now())) {
                    eventList.getItems().add(
                            event.getName() + " - $" + event.getPrice() + " - " + event.getDate()
                    );
                }
            }
            Button back = new Button("Back");
            back.setOnAction(ev->{

            });
        });
    }
}
