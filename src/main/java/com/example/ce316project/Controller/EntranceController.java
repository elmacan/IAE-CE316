package com.example.ce316project.Controller;

import com.example.ce316project.IAEManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;

public class EntranceController {
    @FXML
    private Button listConfigurationsButton;





    @FXML
    public void onListConfigurationsButtonClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Pane root = fxmlLoader.load();
            Scene scene = new Scene(root);


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            IAEManager.sceneStack.push(stage.getScene());
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
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listProjects.fxml"));
            Parent root= loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Project List");
            stage.show();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onHelpButtonClick(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/mainHelp.fxml"));
            Parent root= loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Project List");
            stage.show();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void showMainMenuHelp(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/helpPages.fxml"));
            Parent helpPageRoot = loader.load();

            HelpControllers helpCtrl = loader.getController();
            if (helpCtrl == null) {
                System.err.println("EntranceController: Error - Could not get HelpController instance.");
                showAlert(Alert.AlertType.ERROR, "Help Error", "Failed to load help controller.");
                return;
            }

            helpCtrl.loadHelpContent(HelpControllers.HelpTopic.MAIN_MENU);

            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);

            Window ownerWindow = null;
            if (event.getSource() instanceof Node) {
                Node sourceNode = (Node) event.getSource();
                if (sourceNode.getScene() != null && sourceNode.getScene().getWindow() != null) {
                    ownerWindow = sourceNode.getScene().getWindow();
                    helpStage.initOwner(ownerWindow);
                } else {
                    System.err.println("EntranceController: Source node for help button is not part of a scene/window. Centering on screen.");
                }
            } else {
                System.err.println("EntranceController: Event source is not a Node. Cannot determine owner window. Centering on screen.");
            }



            if (helpCtrl.getLoadedTitleForStage() != null) {
                helpStage.setTitle(helpCtrl.getLoadedTitleForStage());
            } else {
                helpStage.setTitle("Application Help");
            }

            double helpWidth = 600;
            double helpHeight = 400;
            Scene helpScene = new Scene(helpPageRoot, helpWidth, helpHeight);
            helpStage.setScene(helpScene);
            helpStage.setResizable(true);

            if (ownerWindow != null && ownerWindow.isShowing()) {
                final Window finalOwnerWindow = ownerWindow;
                helpStage.setOnShown(e -> {
                    if (helpStage.isShowing() && finalOwnerWindow.isShowing()) {
                        helpStage.setX(finalOwnerWindow.getX() + (finalOwnerWindow.getWidth() - helpStage.getWidth()) / 2);
                        helpStage.setY(finalOwnerWindow.getY() + (finalOwnerWindow.getHeight() - helpStage.getHeight()) / 2);
                    }
                });
            } else {
                helpStage.centerOnScreen();
            }

            helpStage.showAndWait();

        } catch (IOException e) {
            System.err.println("EntranceController: Error loading help FXML - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "Could not load the help page.");
        } catch (Exception e) { // Diğer beklenmedik hatalar için
            System.err.println("EntranceController: Unexpected error showing help - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "An unexpected error occurred while showing help.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
