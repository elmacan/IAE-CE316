package com.example.ce316project.Controller;

import com.example.ce316project.Configuration;
import com.example.ce316project.FileManager;
import com.example.ce316project.IAEManager;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class ViewConfigController {

    @FXML private Label languageNameLabel;
    @FXML private Label languagePathLabel;
    @FXML private Label languageParamsLabel;
    @FXML private Label runCommandLabel;
    @FXML private Label isCompiledLabel;

    @FXML
    private VBox rootVBox;

    @FXML
    private Label configurationDetailsLabel;

    private Configuration configuration;

    public void initialize() {
        ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> updateFontSize();
        rootVBox.widthProperty().addListener(sizeListener);
        rootVBox.heightProperty().addListener(sizeListener);
    }
    private void updateFontSize() {
        double width = rootVBox.getWidth();
        double height = rootVBox.getHeight();

        double scale = Math.min(width, height); // en küçük boyuta göre
        double baseSize = Math.max(14, scale / 35); // daha büyük minimum

        String fontStyle = String.format("-fx-font-size: %.1fpx;", baseSize);
        String titleStyle = String.format("-fx-font-size: %.1fpx; -fx-font-weight: bold;", baseSize + 6);

        configurationDetailsLabel.setStyle(titleStyle);

        languageNameLabel.setStyle(fontStyle);
        languagePathLabel.setStyle(fontStyle);
        languageParamsLabel.setStyle(fontStyle);
        runCommandLabel.setStyle(fontStyle);
        isCompiledLabel.setStyle(fontStyle);
    }

    public void setConfiguration(Configuration config) {
        this.configuration = config;

        languageNameLabel.setText(config.getLanguageName());
        languagePathLabel.setText(config.getLanguagePath());
        languageParamsLabel.setText(config.getLanguageParameters());
        runCommandLabel.setText(config.getRunCommand());
        isCompiledLabel.setText(config.isCompiled() ? "Yes" : "No");
    }

    @FXML
    private void onEditClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/editConfig.fxml"));
            Parent root = loader.load();

            // Edit sayfasına mevcut Configuration'ı aktarıyoruz
            SaveConfigController controller = loader.getController();
            controller.setEditingConfiguration(configuration); // Bu methodu SaveConfigController'da yazmalısın

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onDeleteClick(ActionEvent event) {
        IAEManager.configurationList.remove(configuration);
        File configFile = new File(System.getProperty("user.home") + "/Documents/iae-app/configs.json");
        FileManager.saveConfigurations(IAEManager.configurationList, configFile);
        onBackClick(event);
    }

    @FXML
    private void onBackClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) languageNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void showViewConfigHelpPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/helpPages.fxml"));
            Parent helpPageRoot = loader.load();

            HelpControllers helpCtrl = loader.getController();
            if (helpCtrl == null) {
                System.err.println("ViewConfigController: Error - Could not get HelpController instance.");
                showAlert(Alert.AlertType.ERROR, "Help Error", "Failed to load help controller.");
                return;
            }

            helpCtrl.loadHelpContent(HelpControllers.HelpTopic.VIEW_CONFIG_DETAILS);

            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);

            Window ownerWindow = null;
            Node sourceNode = (Node) event.getSource();
            if (sourceNode.getScene() != null && sourceNode.getScene().getWindow() != null) {
                ownerWindow = sourceNode.getScene().getWindow();
            } else if (languageNameLabel.getScene() != null && languageNameLabel.getScene().getWindow() != null) { // Fallback
                ownerWindow = languageNameLabel.getScene().getWindow();
            }

            if (ownerWindow != null) {
                helpStage.initOwner(ownerWindow);
            } else {
                System.err.println(this.getClass().getSimpleName() + ": Could not determine owner window for help page.");
            }

            if (helpCtrl.getLoadedTitleForStage() != null) {
                helpStage.setTitle(helpCtrl.getLoadedTitleForStage());
            } else {
                helpStage.setTitle("View Configuration - Help"); // Varsayılan
            }

            double helpWidth = 500;
            double helpHeight = 300;
            Scene helpScene = new Scene(helpPageRoot, helpWidth, helpHeight);
            helpStage.setScene(helpScene);
            helpStage.setResizable(true);

            final Window finalOwnerWindow = ownerWindow;
            if (finalOwnerWindow != null) {
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
            System.err.println("ViewConfigController: Error loading help page FXML - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "Could not load the help page.");
        } catch (Exception e) {
            System.err.println("ViewConfigController: Unexpected error showing help - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "An unexpected error occurred.");
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
