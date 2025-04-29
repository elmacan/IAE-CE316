package com.example.ce316project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListConfigController implements Initializable {

    @FXML
    private ListView<Configuration> configurationListView;

    private List<Configuration> configurationList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurationList = FileManager.loadConfigurations(new File("configs.json"));

        if (configurationList != null) {
            configurationListView.getItems().addAll(configurationList);

            configurationListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Configuration config, boolean empty) {
                    super.updateItem(config, empty);
                    if (empty || config == null) {
                        setText(null);
                    } else {
                        setText(config.getLanguageName());
                    }
                }
            });
        }
    }
    @FXML
    private void onAddConfigurationButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/saveConfig.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Mevcut stage alınıyor
            stage.setScene(new Scene(root)); // Yeni sahneye geçiş
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateList(List<Configuration> updatedConfigurations) {
        configurationListView.getItems().setAll(updatedConfigurations);
    }
    @FXML
    private void onImportButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration(s)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            List<Configuration> imported = FileManager.importConfigurations(selectedFile);
            configurationListView.getItems().addAll(imported); // Görsel olarak listeye ekle
            FileManager.saveConfigurations(imported, new File("configs.json")); // Kalıcı olarak kaydet
        }
    }

    @FXML
    private void onExportButtonClick() {
        Configuration selected = configurationListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Export için bir configuration seçmelisiniz.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Configuration");
        fileChooser.setInitialFileName("configuration.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            FileManager.exportConfiguration(selected, file);
        }
    }
}
