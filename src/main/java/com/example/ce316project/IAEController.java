package com.example.ce316project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class IAEController extends Application {
    private List<Configuration> configurationList;
    private List<Project> projectList;
    private Project currentProject;

    @Override
    public void start(Stage stage) throws IOException {
        Label label=new Label("elma");

        VBox vbox=new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().add(label);

        Scene scene=new Scene(vbox,400,300);
        stage.setScene(scene);
        stage.show();



    }

    public static void main(String[] args) {
        launch();
    }
}