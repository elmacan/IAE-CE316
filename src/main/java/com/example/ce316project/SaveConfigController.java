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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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

    private Configuration editingConfig = null;

    private static final String CONFIG_PATH = System.getProperty("user.home") + "/Documents/iae-app/configs.json";
    //private static final File CONFIG_FILE = new File("C:\\Users\\msi\\IdeaProjects\\IAE-CE316\\configs.json"); hatıra kalsın eheh :D

    @FXML
    private void initialize() {
        saveConfigButton.setOnAction(this::handleSaveConfig);
        browseCompilerButton.setOnAction(this::handleBrowseCompiler);
    }


    @FXML
    private void handleBrowseCompiler(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compiler");
        File selected = fileChooser.showOpenDialog(null);
        if (selected != null) {
            compilerPathField.setText(selected.getAbsolutePath());
        }
    }

    public void setEditingConfiguration(Configuration config) {
        editingConfig = config;

        languageField.setText(config.getLanguageName());
        compilerPathField.setText(config.getLanguagePath());
        sourceFileField.setText(config.getLanguageParameters());
        runnerCommandField.setText(config.getRunCommand());
        //isCompiledCheckBox.setSelected(config.isCompiled());

        // Tipi ayarla
        if (config.isCompiled()) {
            languageTypeComboBox.setValue("Compiled");
        } else {
            languageTypeComboBox.setValue("Interpreted");
        }
    }

    @FXML
    private void handleSaveConfig(ActionEvent event) {
        try {
            String language = languageField.getText();
            String type = languageTypeComboBox.getValue();
            String compilerPath = compilerPathField.getText();
            String parameters = sourceFileField.getText();
            String runnerCommand = runnerCommandField.getText();
            //boolean isCompiled = isCompiledCheckBox.isSelected();

            Configuration newConfig = new Configuration();
            newConfig.setLanguageName(language);
            newConfig.setLanguagePath(compilerPath);
            newConfig.setLanguageParameters(parameters);
            newConfig.setRunCommand(runnerCommand);
            newConfig.setCompiled("Imperative".equalsIgnoreCase(type) ? false : true);
            //newConfig.setCompiled(isCompiled);

            File configFile = getWritableConfigFile();

            if (editingConfig != null) {
                int index = IAEController.configurationList.indexOf(editingConfig);
                if (index != -1) {
                    IAEController.configurationList.set(index, newConfig);
                }
            } else {
                IAEController.configurationList.add(newConfig);
            }

            FileManager.saveConfigurations(IAEController.configurationList, configFile);

            // Geri dön
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();
            ListConfigController listController = loader.getController();
            listController.updateList(IAEController.configurationList);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private File getWritableConfigFile() throws Exception {
        File writableFile = new File(CONFIG_PATH);

        // Check if the file already exists; if not, copy it from resources
        if (!writableFile.exists()) {
            File parentDir = writableFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            try (InputStream resourceStream = getClass().getResourceAsStream("/configs.json")) {
                if (resourceStream == null) {
                    throw new FileNotFoundException("Resource 'configs.json' not found in resources.");
                }
                Files.copy(resourceStream, writableFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return writableFile;
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
