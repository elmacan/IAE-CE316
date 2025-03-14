module com.example.ce316project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ce316project to javafx.fxml;
    exports com.example.ce316project;
}