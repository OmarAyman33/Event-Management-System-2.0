module org.example.learningjavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.learningjavafx to javafx.fxml;
    exports org.example.learningjavafx;
}