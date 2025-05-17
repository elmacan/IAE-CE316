package com.example.ce316project.Controller;

import com.example.ce316project.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
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
    @FXML
    private Button addConfigButton;

    private StudentSubmission currentSubmission;

    private Runnable onConfigUpdate;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (IAEManager.configurationList != null) {
            //configurationComboBox.setItems(FXCollections.observableArrayList(IAEController.configurationList));
            configurationComboBox.setItems(IAEManager.configurationList);
        }
        browseSourceButton.setOnAction(this::chooseZipFileDirectory);
    }
    @FXML
    private void onCancelButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
            Parent root = loader.load();

            // Set the controller as UserData
            root.setUserData(this);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddConfigButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/listConfig.fxml"));
            Parent root = loader.load();
            IAEManager.sceneStack.push(((Node) event.getSource()).getScene()); // Mevcut sahneyi yığına ekle

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);

            // Add a listener to detect when the stage is closed
            stage.setOnHidden(windowEvent -> {
                // Notify the callback after adding a configuration
                if (onConfigUpdate != null) {
                    onConfigUpdate.run();
                    refreshConfigurationComboBox();
                }
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setOnConfigUpdate(Runnable onConfigUpdate) {
        this.onConfigUpdate = onConfigUpdate;
    }
    public void refreshConfigurationComboBox() {
        if (IAEManager.configurationList != null) {
            configurationComboBox.setItems(FXCollections.observableArrayList(IAEManager.configurationList));
        }
    }


    @FXML
    private void onCreateProjectButton() {
        // 1. Read values from UI fields
        String name = projectNameField.getText().trim();
        Configuration selectedConfig = configurationComboBox.getValue();
        String input = argumentsArea.getText(); // Ham girdiyi alalım
        String expectedOutput = expectedOutputFileField.getText().trim(); // Beklenen çıktıyı alıp trim edelim
        String zipPath = sourceFileField.getText().trim();

        // --- EKLENEN KONTROL ---
        // Gerekli alanların boş olup olmadığını kontrol et (expectedOutput dahil)
        if (name.isEmpty() || selectedConfig == null || zipPath.isEmpty() || expectedOutput.isEmpty()) { // expectedOutput kontrolü eklendi
            // Eksik bilgi varsa kullanıcıyı uyar
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please fill in the Project Name, Configuration, Source Directory AND Expected Output fields.");
            System.out.println("Proje oluşturma başarısız: Beklenen Çıktı dahil gerekli alanlar eksik.");
            return; // Proje oluşturmayı durdur
        }
        // --- KONTROL SONU ---

        // Kaynak dizinin geçerli olup olmadığını kontrol et
        File zipDirectory = new File(zipPath);
        if (!zipDirectory.exists() || !zipDirectory.isDirectory()) {
            showAlert(Alert.AlertType.ERROR, "Invalid Path", "The specified source path is not a valid directory.");
            System.out.println("Proje oluşturma başarısız: Geçersiz kaynak dizin yolu.");
            return;
        }

        // 3. Project nesnesini oluştur (Artık expectedOutput'un boş olmadığından eminiz)
        Project newProject = new Project(name, selectedConfig, input, expectedOutput, zipDirectory);
        newProject.runAllSubmission();
        System.out.println("DEBUG: onCreateProjectButton - Proje nesnesi oluşturuldu. Beklenen Çıktı: '" + newProject.getExpectedOutput() + "'"); // Debug log

        // 4. Projeyi kaydet (FileManager kullanarak) ve global listeleri güncelle
        File projectFile = new File(IAEManager.PROJECT_PATH);
        List<Project> updatedList = FileManager.saveProjectIfUnique(newProject, projectFile);

        // 5. Kaydetme sonucuna göre UI ve durumu güncelle
        if (updatedList != null) {
            IAEManager.projectList = updatedList; // Statik listeyi güncelle
            IAEManager.currentProject = newProject; // Yeni oluşturulan projeyi mevcut proje yap

            System.out.println("New project '" + name + "' was created and saved successfully.");
            System.out.println("DEBUG: onCreateProjectButton - IAEController.currentProject ayarlandı. Beklenen Çıktı: '" + IAEManager.currentProject.getExpectedOutput() + "'"); // Debug log
            showAlert(Alert.AlertType.INFORMATION, "Project Created", "The project '" + name + "' was created successfully.");

            // Run/Compare butonlarını göster, Create butonunu gizle
            compareButton.setVisible(true);
            compareButton.setManaged(true);
            createProjectButton.setVisible(false);
            createProjectButton.setManaged(false);
        } else {
            // Aynı isimde proje varsa kullanıcıyı uyar
            showAlert(Alert.AlertType.ERROR, "Duplicate Project", "A project named '" + name + "' already exists. Please choose a different name.");
            System.out.println("Proje oluşturma başarısız: Proje adı zaten mevcut.");
            // currentProject değişmediği için UI butonlarını değiştirme
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
    private void browseExpectedOutputFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser(); // Dosya seçici
        fileChooser.setTitle("Beklenen Çıktı Dosyasını Seçin");

        // Sadece .txt veya genel metin dosyalarını göster
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        fileChooser.getExtensionFilters().addAll(txtFilter, allFiles);

        // Başlangıç dizini olarak kullanıcının home klasörünü ayarla
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Pencere referansını al
        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            expectedOutputFileField.setText(selectedFile.getAbsolutePath());
            System.out.println("Seçilen beklenen çıktı dosyası: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("Beklenen çıktı dosya seçimi iptal edildi.");
        }
    }
    @FXML
    private void browseArguments(ActionEvent event) {
        FileChooser fileChooser = new FileChooser(); // Dosya seçici
        fileChooser.setTitle("Beklenen Çıktı Dosyasını Seçin");

        // Sadece .txt veya genel metin dosyalarını göster
        FileChooser.ExtensionFilter txtFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        FileChooser.ExtensionFilter allFiles = new FileChooser.ExtensionFilter("All Files (*.*)", "*.*");
        fileChooser.getExtensionFilters().addAll(txtFilter, allFiles);

        // Başlangıç dizini olarak kullanıcının home klasörünü ayarla
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Pencere referansını al
        Window window = ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            argumentsArea.setText(selectedFile.getAbsolutePath());
            System.out.println("Seçilen beklenen çıktı dosyası: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("Beklenen çıktı dosya seçimi iptal edildi.");
        }
    }


    //@FXML
    //private void onRunButtonClick(ActionEvent event) {
      // IAEController.currentProject.runAllSubmission();
   // }


   /* @FXML
    private void onCompareButtonClick(ActionEvent event) {
        if (IAEController.currentProject == null) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Aktif proje bulunamadı. Lütfen bir proje oluşturun veya yükleyin.");
            return;
        }

        List<StudentSubmission> submissions = IAEController.currentProject.getSubmissions();
        if (submissions == null || submissions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Bu proje için hiç submission bulunamadı. Lütfen önce submission'ları çalıştırın.");
            return;
        }

        String expectedOutput = IAEController.currentProject.getExpectedOutput();
        if (expectedOutput == null || expectedOutput.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "Beklenen çıktı tanımlı değil. Lütfen proje ayarlarını kontrol edin.");
            return;
        }


        StudentSubmission submission = submissions.get(0);
        if (submission == null) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "İlk submission nesnesi null.");
            return;
        }

        boolean comparisonResult = submission.compareOutput(expectedOutput);
        String comparisonText;

        if (comparisonResult) {
            comparisonText = " Çıktı, projede tanımlanan beklenen çıktı ile eşleşiyor.\n\n"
                    + "Beklenen Çıktı:\n" + expectedOutput + "\n\n"
                    + "Öğrenci Çıktısı:\n" + submission.getOutput();
        } else {
            comparisonText = " Çıktı, projede tanımlanan beklenen çıktı ile eşleşmiyor.\n\n"
                    + "Beklenen Çıktı:\n" + expectedOutput + "\n\n"
                    + "Öğrenci Çıktısı:\n" + submission.getOutput();
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultPage.fxml"));
            Parent root = loader.load();


            Stage stage = new Stage();
            stage.setTitle("Karşılaştırma Sonuçları");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Yükleme Hatası", "Sonuç sayfası yüklenirken bir hata oluştu.");
        }

    }*/



    @FXML
    private void onCompareButtonClick(ActionEvent event) {
        if (IAEManager.currentProject == null) {
            showAlert(Alert.AlertType.ERROR, "Comparison Error", "Active project is not selected.");
            return;
        }

        List<StudentSubmission> submissions = IAEManager.currentProject.getSubmissions();
        if (submissions == null || submissions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Comparison Error", "No submissions were found or processed. Please click 'Run' button first.");
            return;
        }

        String expectedOutput = IAEManager.currentProject.getExpectedOutputContent();
        if (expectedOutput == null || expectedOutput.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Comparison Error", "Expected output content could not be determined.");
            return;
        }

        System.out.println("Comparison process begins...");
        for (StudentSubmission submission : submissions) {
            if (submission != null && submission.getResult() != null && submission.getResult().isRunSuccessfully()) {
                submission.compareOutput(expectedOutput);
            }
        }
        System.out.println("Comparison process finished.");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ResultPage.fxml"));
            Parent resultPageRoot = loader.load();
            ResultPageController resultController = loader.getController();

            if (resultController == null) {
                System.err.println("Error: Could not get Controller for ResultPage.fxml.");
                showAlert(Alert.AlertType.ERROR, "Controller Error", "Failed to load results page controller.");
                return;
            }

            Scene currentScene = ((Node) event.getSource()).getScene();
            resultController.loadSubmissionResults(submissions, currentScene);

            Stage stage = (Stage) currentScene.getWindow();
            Scene resultScene = new Scene(resultPageRoot);
            stage.setScene(resultScene);
            stage.setTitle("Comparison Results - " + IAEManager.currentProject.getProjectName());
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

            System.out.println("The results page has been successfully loaded and displayed.");

        } catch (IOException e) {
            System.err.println("I/O Error while loading ResultPage.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Interface Loading Error", "Results page could not be loaded.");
        }
    }


    /*@FXML
    private void onCompareButtonClick(ActionEvent event) {
        if (IAEController.currentProject == null) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Aktif proje bulunamadı. Lütfen bir proje oluşturun veya yükleyin.");
            return;
        }

        List<StudentSubmission> submissions = IAEController.currentProject.getSubmissions();
        if (submissions == null || submissions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Bu proje için hiç submission bulunamadı. Lütfen önce submission'ları çalıştırın.");
            return;
        }

        String expectedOutputPath = IAEController.currentProject.getExpectedOutput();
        if (expectedOutputPath == null || expectedOutputPath.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "Bu proje için tanımlanmış beklenen çıktı yolu boş. Lütfen proje özelliklerini kontrol edin.");
            return;
        }

        File expectedFile = new File(expectedOutputPath);
        if (!expectedFile.exists()) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "Beklenen çıktı dosyası bulunamadı:\n" + expectedFile.getAbsolutePath());
            return;
        }

        String expectedOutput;
        try {
            expectedOutput = Files.readString(expectedFile.toPath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Dosya Okuma Hatası", "Beklenen çıktı dosyası okunamadı:\n" + e.getMessage());
            return;
        }

        StudentSubmission submission = submissions.get(0); // İleride seçim eklenebilir
        if (submission == null) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "İlk submission nesnesi null.");
            return;
        }

        boolean comparisonResult = submission.compareOutput(expectedOutput);
        Result result = submission.getResult();

        StringBuilder message = new StringBuilder();
        message.append("Derleme: ").append(result.isCompiledSuccessfully() ? "✅" : "❌").append("\n");
        message.append("Çalıştırma: ").append(result.isRunSuccessfully() ? "✅" : "❌").append("\n");
        message.append("Çıktı Eşleşmesi: ").append(result.isOutputMatches() ? "✅" : "❌").append("\n");

        if (!result.getErrorLog().isEmpty()) {
            message.append("\nHatalar:\n").append(result.getErrorLog());
        }

        Alert.AlertType alertType = result.isOutputMatches() ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        showAlert(alertType, "Karşılaştırma Sonucu", message.toString());
    }*/



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







