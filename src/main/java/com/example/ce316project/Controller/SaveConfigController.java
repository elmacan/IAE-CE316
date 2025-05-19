package com.example.ce316project.Controller;

import com.example.ce316project.Configuration;
import com.example.ce316project.FileManager;
import com.example.ce316project.IAEManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Window;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SaveConfigController {

    @FXML private TextField languageField;
    @FXML private ComboBox<String> languageTypeComboBox;
    @FXML private TextField compilerPathField;
    @FXML private TextField sourceFileField;
    @FXML private TextField runnerCommandField;
    @FXML private Button browseCompilerButton;
    @FXML private Button saveConfigButton;

    private Configuration editingConfig = null;
    
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

            // validate compiler path if not empty
            if (!compilerPath.isBlank() && !validateLanguagePath(compilerPath)) {
                showAlert(Alert.AlertType.ERROR, "Invalid Compiler/Interpreter",
                        "The path you entered is not valid.\n\n");


                compilerPathField.clear();
                compilerPathField.requestFocus();
                return; // stop save operation
            }


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
                int index = IAEManager.configurationList.indexOf(editingConfig);
                if (index != -1) {
                    IAEManager.configurationList.set(index, newConfig);
                }
            } else {
                IAEManager.configurationList.add(newConfig);
            }

            FileManager.saveConfigurations(IAEManager.configurationList, configFile);

            // Geri dön
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();
            ListConfigController listController = loader.getController();
            listController.updateList(IAEManager.configurationList);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateLanguagePath(String languagePath) {
        if (languagePath == null || languagePath.isBlank()) return false;

        //Dosya uzantısı kontrolü
        String[] disallowedExtensions = {".png", ".jpg", ".jpeg", ".pdf", ".doc", ".docx", ".xlsx", ".ppt", ".gif", ".bmp"};
        for (String ext : disallowedExtensions) {
            if (languagePath.toLowerCase().endsWith(ext)) {
                return false;
            }
        }

        File file = new File(languagePath);
        if (file.exists() && file.isFile() && file.canExecute()) {
            return true;
        }

        // Komut satırı üzerinden kontrol
        try {
            ProcessBuilder pb = new ProcessBuilder(languagePath, "--version");
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            String out = output.toString().toLowerCase();

            return exitCode == 0 &&
                    out.length() > 0 &&
                    !out.contains("not recognized") &&
                    !out.contains("command not found") &&
                    !out.contains("access is denied") &&
                    !out.contains("error") &&
                    !out.contains("cannot be found") &&
                    !out.contains("microsoft") &&
                    !out.contains("adobe") &&
                    !out.contains("viewer") &&
                    !out.contains("photo") &&
                    !out.contains("image");
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }




    private File getWritableConfigFile() throws Exception {
        File writableFile = new File(IAEManager.CONFIG_PATH);

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

    // SaveConfigController.java
// ... (importlar aynı kalır) ...

    @FXML
    private void showSaveConfigHelpPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/helpPages.fxml"));
            Parent helpPageRoot = loader.load();

            HelpControllers helpCtrl = loader.getController();
            if (helpCtrl == null) {
                // ... (hata yönetimi) ...
                return;
            }
            helpCtrl.loadHelpContent(HelpControllers.HelpTopic.SAVE_EDIT_CONFIG);

            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);

            Window ownerWindow = null;
            Node sourceNode = (Node) event.getSource();
            if (languageField != null && languageField.getScene() != null && languageField.getScene().getWindow() != null) {
                ownerWindow = languageField.getScene().getWindow();
            } else if (sourceNode != null && sourceNode.getScene() != null && sourceNode.getScene().getWindow() != null) {
                ownerWindow = sourceNode.getScene().getWindow();
            }

            if (ownerWindow != null) {
                helpStage.initOwner(ownerWindow);
            } else {
                System.err.println(this.getClass().getSimpleName() + ": Could not determine owner window for help page.");
            }

            if (helpCtrl.getLoadedTitleForStage() != null) {
                helpStage.setTitle(helpCtrl.getLoadedTitleForStage());
            } else {
                helpStage.setTitle("Help");
            }

            double helpWidth = 700;
            double helpHeight = 400;
            Scene helpScene = new Scene(helpPageRoot, helpWidth, helpHeight);
            helpStage.setScene(helpScene);
            helpStage.setResizable(true);

            // --- DEĞİŞİKLİK BURADA ---
            final Window finalOwnerWindow = ownerWindow; // ownerWindow'ı final bir değişkene kopyala

            if (finalOwnerWindow != null) { // Lambda içinde finalOwnerWindow'ı kullan
                helpStage.setOnShown(e -> {
                    helpStage.setX(finalOwnerWindow.getX() + (finalOwnerWindow.getWidth() - helpStage.getWidth()) / 2);
                    helpStage.setY(finalOwnerWindow.getY() + (finalOwnerWindow.getHeight() - helpStage.getHeight()) / 2);
                });
            } else {
                helpStage.centerOnScreen();
            }
            // --- ---

            helpStage.showAndWait();

        } catch (IOException e) {
            // ... (hata yönetimi) ...
        } // ...
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void onIconHome(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) saveConfigButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Navigation Error Failed to load the entrance page.");
        }
    }

    @FXML
    public void onIconConfigList(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) saveConfigButton.getScene().getWindow();
            IAEManager.sceneStack.push(stage.getScene());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Navigation Error Failed to load the entrance page.");
        }
    }
    @FXML
    public void onIconProjectlist(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listProjects.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) saveConfigButton.getScene().getWindow();
            IAEManager.sceneStack.push(stage.getScene());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Navigation Error Failed to load the entrance page.");
        }
    }
}
