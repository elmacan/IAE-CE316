package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreateProjectController implements Initializable {

    @FXML
    private TextField projectNameField, sourceFileField, expectedOutputFileField;

    @FXML
    private TextArea argumentsArea;

    @FXML
    private ComboBox<Configuration> configurationComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Configuration> configs = FileManager.loadConfigurations(new File("configs.json"));
        if (configs != null) {
            configurationComboBox.setItems(FXCollections.observableArrayList(configs));
        }
    }
    @FXML
    private void onCancelButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCreateProjectButton() {
        String name = projectNameField.getText();
        Configuration selectedConfig = configurationComboBox.getValue();
        String input = argumentsArea.getText();
        String expectedOutput = expectedOutputFileField.getText();
        String zipPath = sourceFileField.getText();

        if (name.isEmpty() || selectedConfig == null || zipPath.isEmpty()) {
            System.out.println("Lütfen tüm gerekli alanları doldurun.");
            return;
        }

        Project newProject = new Project(name, selectedConfig, input, expectedOutput, new File(zipPath));
        File projectFile = new File("C:\\Users\\msi\\IdeaProjects\\IAE-CE316\\projects.json");

        // Yeni yöntemle aynı isimde varsa kaydetme
        boolean added = FileManager.saveProjectIfUnique(newProject, projectFile);

        if (added) {
            System.out.println("Yeni proje başarıyla eklendi.");
        } else {
            System.out.println("Bu isimde bir proje zaten var. Lütfen farklı bir isim seçin.");
        }
    }

}
