package com.example.ce316project.Controller;

import com.example.ce316project.Configuration;
import com.example.ce316project.FileManager;
import com.example.ce316project.IAEManager;
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
            String type = languageTypeComboBox.getValue(); // getValue() null dönebilir, kontrol edin
            String compilerPath = compilerPathField.getText();
            String parameters = sourceFileField.getText(); // Bu alanın neyi temsil ettiği önemli
            String runnerCommand = runnerCommandField.getText();

            if (language == null || language.trim().isEmpty()) {
                // Kullanıcıya hata göster (Alert ile)
                System.err.println("Language name cannot be empty.");
                return;
            }
            if (type == null) {
                System.err.println("Language type must be selected.");
                return;
            }


            Configuration configToSave = (editingConfig != null) ? editingConfig : new Configuration();
            // Eğer editingConfig null değilse, mevcut nesneyi güncelliyoruz.
            // Böylece indexOf ile arama yapmaya gerek kalmaz ve referans korunur.

            configToSave.setLanguageName(language);
            configToSave.setLanguagePath(compilerPath);
            configToSave.setLanguageParameters(parameters); // Bu "sourceFileField" aslında parametreleri mi tutuyor?
            configToSave.setRunCommand(runnerCommand);
            // "Imperative" olmayan her şey "Compiled" mı sayılacak? Daha net bir ayrım gerekebilir.
            // Genellikle 'Compiled' ve 'Interpreted' (veya Scripted) olur.
            configToSave.setCompiled(!"Imperative".equalsIgnoreCase(type)); // Eğer tip "Imperative" değilse compiled=true

            File configFile = getWritableConfigFile();

            if (editingConfig == null) { // Yeni konfigürasyon ekleniyor
                IAEManager.configurationList.add(configToSave);
            } else { // Mevcut konfigürasyon düzenleniyor
                // Eğer editingConfig doğrudan IAEManager.configurationList'teki bir referans ise,
                // yukarıdaki setter'lar zaten ObservableList'i tetiklemiş olmalı.
                // Eğer değilse, veya emin olmak için:
                int index = IAEManager.configurationList.indexOf(editingConfig); // Orijinal editingConfig'i bul
                if (index != -1) {
                    IAEManager.configurationList.set(index, configToSave); // Güncellenmiş config ile değiştir
                } else {
                    // Bu durum beklenmez, düzenleme modunda config listede olmalı.
                    // Güvenlik için yine de eklenebilir veya hata verilebilir.
                    IAEManager.configurationList.add(configToSave);
                    System.err.println("Warning: Editing config not found in the list, added as new.");
                }
            }

            FileManager.saveConfigurations(IAEManager.configurationList, configFile);
            System.out.println("Configuration saved successfully.");

            // Bir önceki sahneye (ListConfigController'ın sahnesi olmalı) geri dön
            Scene previousScene = IAEManager.popScene(); // Yığından al
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (previousScene != null) {
                stage.setScene(previousScene);
                // ListConfigController'daki ListView zaten IAEManager.configurationList'e
                // bağlı olduğu için otomatik olarak güncellenmiş olmalı.
                // Ekstra bir updateList() çağrısına gerek yok.
                stage.setTitle("Configuration List"); // Başlığı geri ayarla
                stage.show();
            } else {
                // Bu durum normalde olmamalı, çünkü saveConfig sayfasına bir yerden gelinmiş olmalı.
                // Güvenlik önlemi olarak giriş ekranına yönlendirilebilir.
                System.err.println("Error: Previous scene not found in stack. Navigating to entrance.");
                // FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
                // Parent root = loader.load();
                // stage.setScene(new Scene(root));
                // stage.setTitle("Integrated Application Environment");
                // IAEManager.clearStack();
                // stage.show();
                // Ya da daha iyisi, hata mesajı gösterip kullanıcıyı yönlendirmek.
                // Şimdilik sadece konsola yazdıralım.
            }

        } catch (Exception e) { // Daha spesifik Exception'lar yakalamak daha iyi olabilir (örn: IOException)
            System.err.println("Error saving configuration: " + e.getMessage());
            e.printStackTrace();
            // Kullanıcıya bir Alert ile hata mesajı gösterilebilir.
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
