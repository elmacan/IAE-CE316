package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaveConfigController {

    @FXML private TextField languageField;
    @FXML private ComboBox<String> languageTypeComboBox;
    @FXML private TextField compilerPathField;
    @FXML private TextField sourceFileField;
    @FXML private TextField runnerCommandField;
    @FXML private Button browseCompilerButton;
    @FXML private Button saveConfigButton;

    private static final File CONFIG_FILE = new File("C:\\Users\\msi\\IdeaProjects\\copy\\configs.json");

    @FXML
    private void initialize() {
        saveConfigButton.setOnAction(this::handleSaveConfig);
        browseCompilerButton.setOnAction(this::handleBrowseCompiler);
    }

    private void handleBrowseCompiler(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compiler");
        File selected = fileChooser.showOpenDialog(null);
        if (selected != null) {
            compilerPathField.setText(selected.getAbsolutePath());
        }
    }

    private void handleSaveConfig(ActionEvent event) {
        try {
            String language = languageField.getText();
            String type = languageTypeComboBox.getValue();
            String compilerPath = compilerPathField.getText();
            String parameters = sourceFileField.getText();
            String runnerCommand = runnerCommandField.getText();

            boolean isCompiled = type.equalsIgnoreCase("Compiled");

            Configuration newConfig = new Configuration();
            newConfig.setLanguageName(language);
            newConfig.setLanguagePath(compilerPath);
            newConfig.setLanguageParameters(parameters);
            newConfig.setRunCommand(runnerCommand);
            newConfig.setCompiled(isCompiled);

            List<Configuration> existing = FileManager.loadConfigurations(CONFIG_FILE);
            if (existing == null) existing = new ArrayList<>();
            existing.add(newConfig);
            FileManager.saveConfigurations(existing, CONFIG_FILE);

            // Listeye geri dön
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();

            ListConfigController listController = loader.getController();
            listController.updateList(existing);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
