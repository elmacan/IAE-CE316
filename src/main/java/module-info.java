module com.example.ce316project {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.example.ce316project to javafx.fxml,com.google.gson;
    exports com.example.ce316project;
}