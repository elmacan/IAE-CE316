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
        // JSON dosyasından Configuration listesi oku
        configurationList = FileManager.loadConfigurations(
                new File("src/main/resources/com/example/ce316project/configs.json"));

        if (configurationList != null) {
            configurationListView.getItems().addAll(configurationList);

            // Sadece languageName gösterimi için (isteğe bağlı ama güzel duruyor)
            configurationListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Configuration config, boolean empty) {
                    super.updateItem(config, empty);
                    if (empty || config == null) {
                        setText(null);
                    } else {
                        setText(config.getLanguageName()); // sadece dil adını göster
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
}
