package org.example.learningjavafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CircleFun extends Application {
    public void start(Stage stage) {
        Pane pane = new Pane();

        Circle circle = new Circle();
        circle.centerXProperty().bind(pane.widthProperty().divide(2));
        circle.centerYProperty().bind(pane.heightProperty().divide(2));
        circle.radiusProperty().bind(pane.widthProperty().divide(6));
        circle.setFill(Color.LIGHTGREEN);
        circle.setStroke(Color.BLACK);
        pane.getChildren().add(circle);

        Scene scene = new Scene(pane, 200, 200);
        stage.setTitle("Testing out Properties");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
