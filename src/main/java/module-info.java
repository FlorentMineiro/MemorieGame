module com.example.memoriegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.memoriegame to javafx.fxml;
    exports com.example.memoriegame;
}