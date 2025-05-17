/*
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
    */
/*@FXML
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
    }*//*



    // ListConfigController.java
    @FXML
    private void onAddConfigurationButtonClick(ActionEvent event) { // "Add Config" butonu
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene(); // Mevcut sahneyi al (listConfigScene)

            // Config ekleme/düzenleme FXML'i
            String editConfigPagePath = "/com/example/ce316project/editConfig.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(editConfigPagePath));
            Parent root = loader.load();
            // EditConfigController controller = loader.getController(); // Gerekirse controller'a erişim

            IAEManager.pushScene(currentScene); // ÖNEMLİ: editConfig'e gitmeden önce listConfig'i yığına ekle

            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Add/Edit Configuration");

            // Sizin onHidden listener'ınız (gerekliyse)
            // stage.setOnHidden(windowEvent -> { ... });
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

   */
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
    }*//*

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


*/




package com.example.ce316project.Controller;

import com.example.ce316project.Configuration;
import com.example.ce316project.FileManager;
import com.example.ce316project.IAEManager;
import javafx.collections.FXCollections; // FXCollections importu eklendi
import javafx.collections.ObservableList; // ObservableList importu eklendi
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
import javafx.util.Callback; // Callback için

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects; // Objects.requireNonNull için
import java.util.ResourceBundle;

public class ListConfigController implements Initializable {

    @FXML
    private ListView<Configuration> configurationListView;

    // Bu metod ListView'ın içeriğini günceller.
    // Genellikle başka bir controller'dan (örneğin, bir config eklendikten/düzenlendikten sonra) çağrılır.
    public void updateListView(ObservableList<Configuration> updatedConfigurations) {
        if (updatedConfigurations != null) {
            configurationListView.setItems(updatedConfigurations);
        } else {
            // Eğer null bir liste gelirse, mevcut listeyi temizleyebiliriz veya olduğu gibi bırakabiliriz.
            // Genellikle null gelmemesi beklenir, boş bir liste gelebilir.
            configurationListView.setItems(FXCollections.observableArrayList()); // Boş observable list ile ayarla
        }
        System.out.println("ListConfigController: ListView updated.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // IAEManager'daki ObservableList'i doğrudan kullan
        // Bu sayede IAEManager.configurationList güncellendiğinde ListView otomatik olarak yansıtmayacak
        // ama başlangıç verisi olarak ve referans olarak kullanılacak.
        // ListView'ın kendi items listesini kullanmak daha iyi bir pratiktir.
        if (IAEManager.configurationList != null) {
            // Kendi ObservableList'imizi oluşturup IAEManager'dan kopyalayalım
            // veya IAEManager.configurationList'i doğrudan atayalım.
            // En iyisi IAEManager.configurationList'i kullanmak ve değişiklikleri oraya yansıtmak.
            configurationListView.setItems(IAEManager.configurationList);
        } else {
            // IAEManager.configurationList null ise, boş bir ObservableList oluştur
            IAEManager.configurationList = FXCollections.observableArrayList();
            configurationListView.setItems(IAEManager.configurationList);
        }


        configurationListView.setCellFactory(new Callback<ListView<Configuration>, ListCell<Configuration>>() {
            @Override
            public ListCell<Configuration> call(ListView<Configuration> listView) {
                return new ListCell<Configuration>() {
                    @Override
                    protected void updateItem(Configuration config, boolean empty) {
                        super.updateItem(config, empty);
                        if (empty || config == null) {
                            setText(null);
                            setGraphic(null); // Grafik de temizlenmeli
                        } else {
                            // Daha açıklayıcı bir metin
                            setText(config.getLanguageName() + " - Path: " + config.getLanguagePath());
                        }
                    }
                };
            }
        });

        configurationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Çift tıklama kontrolü
                Configuration selected = configurationListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    try {
                        Stage stage = (Stage) configurationListView.getScene().getWindow();
                        Scene currentScene = stage.getScene(); // Yığına eklemek için mevcut sahne

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/viewConfig.fxml"));
                        Parent root = loader.load();

                        ViewConfigController controller = loader.getController();
                        controller.setConfiguration(selected); // Seçilen konfigürasyonu view controller'a gönder

                        IAEManager.pushScene(currentScene); // Yeni sayfaya gitmeden önce mevcut sahneyi yığına ekle

                        stage.setScene(new Scene(root));
                        stage.setTitle("View Configuration"); // Pencere başlığını ayarla
                        stage.show();
                    } catch (IOException e) { // Sadece IOException yakala
                        System.err.println("Error loading viewConfig.fxml: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void onAddConfigurationButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene currentScene = stage.getScene();

            String addConfigPagePath = "/com/example/ce316project/saveConfig.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(addConfigPagePath));
            Parent root = loader.load();


            IAEManager.pushScene(currentScene);

            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Add New Configuration");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading saveConfig.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onImportButtonClick(ActionEvent event) { // ActionEvent parametresini ekledim, FXML'de onAction ile bağlanıyorsa gerekir.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration(s)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(null); // Stage'i (Node) event.getSource().getScene().getWindow() ile almak daha iyi

        if (selectedFile != null) {
            List<Configuration> importedConfigs = FileManager.importConfigurations(selectedFile);
            if (importedConfigs != null && !importedConfigs.isEmpty()) {
                // IAEManager.configurationList'e ekle, bu da ListView'ı (eğer items'ı bu listeye bağlıysa) güncellemeli
                IAEManager.configurationList.addAll(importedConfigs);
                // Eğer ListView doğrudan IAEManager.configurationList'i kullanmıyorsa:
                // configurationListView.getItems().addAll(importedConfigs);

                // Güncellenmiş listeyi dosyaya kaydet
                File configFile = new File(IAEManager.CONFIG_PATH);
                FileManager.saveConfigurations(IAEManager.configurationList, configFile); // IAEManager'daki listeyi kaydet
                System.out.println(importedConfigs.size() + " configurations imported and saved.");
            } else {
                System.out.println("No configurations found in the selected file or an error occurred.");
            }
        }
    }

    @FXML
    private void onExportButtonClick(ActionEvent event) { // ActionEvent parametresini ekledim
        Configuration selected = configurationListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            System.out.println("Please select a configuration to export.");
            // Kullanıcıya bir Alert ile bildirim göstermek daha iyi olabilir.
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Configuration");
        fileChooser.setInitialFileName(selected.getLanguageName().replaceAll("\\s+", "_") + "_config.json"); // Daha iyi bir dosya adı
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null); // Stage'i (Node) event.getSource().getScene().getWindow() ile almak daha iyi

        if (file != null) {
            FileManager.exportConfiguration(selected, file);
            System.out.println("Configuration exported to: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void onBackButton(ActionEvent event) {
        Scene previousScene = IAEManager.popScene();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        if (previousScene != null) {
            stage.setScene(previousScene);
            stage.show();
        } else {
            // stack boşsa giriş ekranına git
            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/ce316project/entrancePage.fxml")));
                stage.setScene(new Scene(root));
                stage.setTitle("Integrated Application Environment"); // Başlığı yeniden ayarla
                stage.show();
                IAEManager.clearStack(); // Giriş ekranına döndüğümüzde stack i  temizle
            } catch (IOException e) {
                System.err.println("Error loading entrancePage.fxml on back button: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
