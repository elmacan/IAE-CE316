package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CreateProjectController implements Initializable {

    @FXML
    private TextField projectNameField, sourceFileField, expectedOutputFileField;

    @FXML
    private Button browseSourceButton;

    @FXML
    private TextArea argumentsArea;

    @FXML
    private ComboBox<Configuration> configurationComboBox;

    @FXML
    private Button runButton;

    @FXML
    private Button compareButton;
    @FXML
    private Button createProjectButton;

    private StudentSubmission currentSubmission;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (IAEController.configurationList != null) {
            configurationComboBox.setItems(FXCollections.observableArrayList(IAEController.configurationList));
        }
        browseSourceButton.setOnAction(this::chooseZipFileDirectory);
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


        File projectFile = new File(System.getProperty("user.home") + "/Documents/iae-app/projects.json");


        List<Project> updatedList = FileManager.saveProjectIfUnique(newProject, projectFile);

        if (updatedList != null) {
            IAEController.projectList = updatedList; // liste güncel
            System.out.println("Yeni proje başarıyla eklendi.");
            compareButton.setVisible(true);
            compareButton.setManaged(true);
            runButton.setVisible(true);
            runButton.setManaged(true);
            createProjectButton.setVisible(false);
            createProjectButton.setManaged(false);
        } else {
            System.out.println("Bu isimde bir proje zaten var. Lütfen farklı bir isim seçin.");
        }
    }

    @FXML
    private void chooseZipFileDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Submission Directory");

        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(window);

        if (selectedDirectory != null) {
            File[] zipFiles = selectedDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".zip"));
            if (zipFiles != null && zipFiles.length > 0) {
                sourceFileField.setText(selectedDirectory.getAbsolutePath());
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText("No ZIP files found in the selected directory!");
                alert.showAndWait();
            }
        }else{
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a directory!");
            alert.showAndWait();
        }
    }


    @FXML
    private void onRunButtonClick(ActionEvent event) {

    }

    @FXML
    private void onCompareButtonClick(ActionEvent event) {
        if (currentSubmission == null) {
            System.out.println("No submission loaded. Please load a submission first.");
            return;
        }

        File expectedOutputFile = new File(expectedOutputFileField.getText());
        if (!expectedOutputFile.exists()) {
            System.out.println("Expected output file does not exist.");
            return;
        }

        boolean comparisonResult = currentSubmission.compareOutput(expectedOutputFile);
        System.out.println("Comparison result: " + comparisonResult);
    }







}

