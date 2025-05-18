package com.example.ce316project.Controller;

import com.example.ce316project.Configuration;
import com.example.ce316project.FileManager;
import com.example.ce316project.IAEManager;
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
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListConfigController implements Initializable {

    @FXML
    private ListView<Configuration> configurationListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //configurationList = FileManager.loadConfigurations(new File("configs.json"));
        List<Configuration> configurationList= IAEManager.configurationList;
        configurationListView.setFixedCellSize(26);

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
        configurationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Configuration selected = configurationListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/viewConfig.fxml"));
                        Parent root = loader.load();

                        ViewConfigController controller = loader.getController();
                        controller.setConfiguration(selected);

                        Stage stage = (Stage) configurationListView.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
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


            // Save the updated list to the writable config file   TEKRAR BAKCAM
            File configFile = new File(IAEManager.CONFIG_PATH);
            FileManager.saveConfigurations(configurationListView.getItems(), configFile);
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

   /* private void onBackButton(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (!IAEController.sceneStack.isEmpty()) {
                stage.setScene(IAEController.sceneStack.pop()); // Önceki sahneye geçiş
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrance.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root)); // Entrance sahnesine geçiş
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

   @FXML
   private void onBackButton(ActionEvent event) {
       try {
           Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

           if (!IAEManager.sceneStack.isEmpty()) {
               Scene previousScene = IAEManager.sceneStack.pop();
               stage.setScene(previousScene);

               Parent root = previousScene.getRoot();
               Object controller = root.getUserData();
               if (controller instanceof CreateProjectController) {
                   ((CreateProjectController) controller).refreshConfigurationComboBox();
               }
           } else {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrance.fxml"));
               Parent root = loader.load();
               stage.setScene(new Scene(root));
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
}
