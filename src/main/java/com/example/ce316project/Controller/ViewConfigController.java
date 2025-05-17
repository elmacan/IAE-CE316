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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

}
