package com.example.ce316project;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class EntranceController {
    @FXML
    private Button listConfigurationsButton;

    @FXML
    public void onListConfigurationsButtonClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Pane root = fxmlLoader.load();   // Scene yerine şimdilik Pane alıyoruz
            Scene scene = new Scene(root);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            IAEController.sceneStack.push(stage.getScene());
            stage.setScene(scene);
            stage.show();

            // Fade animation ekliyoruz noice
            FadeTransition fadeIn = new FadeTransition(Duration.millis(700), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCreateNewProjectButtonClick(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/createProject.fxml"));
            Parent root= loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Create New Project");
            stage.show();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onPreviousProjectsButtonClick(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/ce316project/listProjects.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Previous Projects");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onHelpButtonClick(ActionEvent event) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/ce316project/mainHelp.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
