package com.example.ce316project;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        System.out.println("DEBUG: onCreateProjectButton - Proje nesnesi oluşturuldu. Beklenen Çıktı: '" + newProject.getExpectedOutput() + "'"); // Debug log

        // 4. Projeyi kaydet (FileManager kullanarak) ve global listeleri güncelle
        File projectFile = new File(IAEController.PROJECT_PATH);
        List<Project> updatedList = FileManager.saveProjectIfUnique(newProject, projectFile);

        // 5. Kaydetme sonucuna göre UI ve durumu güncelle
        if (updatedList != null) {
            IAEController.projectList = updatedList; // Statik listeyi güncelle
            IAEController.currentProject = newProject; // Yeni oluşturulan projeyi mevcut proje yap

            System.out.println("New project '" + name + "' was created and saved successfully.");
            System.out.println("DEBUG: onCreateProjectButton - IAEController.currentProject ayarlandı. Beklenen Çıktı: '" + IAEController.currentProject.getExpectedOutput() + "'"); // Debug log
            showAlert(Alert.AlertType.INFORMATION, "Project Created", "The project '" + name + "' was created successfully.");

            // Run/Compare butonlarını göster, Create butonunu gizle
            compareButton.setVisible(true);
            compareButton.setManaged(true);
            runButton.setVisible(true);
            runButton.setManaged(true);
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
        DirectoryChooser directoryChooser = new DirectoryChooser(); // DirectoryChooser kullan
        directoryChooser.setTitle("Select Expected Output Directory"); // Başlığı güncelle

        // İsteğe bağlı: Başlangıç dizinini ayarla
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Etkinlik kaynağından pencereyi al
        Window window = ((Node) event.getSource()).getScene().getWindow();
        if (window == null) {
            System.err.println("Hata: Beklenen çıktı dizin seçici için pencere alınamadı.");
            showAlert(Alert.AlertType.ERROR,"Error", "Could not open directory chooser.");
            return;
        }

        // Dizin seçme iletişim kutusunu göster
        File selectedDirectory = directoryChooser.showDialog(window);

        if (selectedDirectory != null) {
            // Seçilen dizinin MUTLAK YOLUNU metin alanına ayarla
            expectedOutputFileField.setText(selectedDirectory.getAbsolutePath());
            System.out.println("Seçilen beklenen çıktı dizini: " + selectedDirectory.getAbsolutePath());
        } else {
            // Kullanıcı dizin seçmekten vazgeçti
            System.out.println("Beklenen çıktı dizin seçimi iptal edildi.");
            // İptal edildiğinde bir uyarı göstermek isteğe bağlıdır
            // showAlert(AlertType.INFORMATION, "İptal Edildi", "Dizin seçimi iptal edildi.");
        }
    }


    @FXML
    private void onRunButtonClick(ActionEvent event) {
       IAEController.currentProject.runAllSubmission();
    }


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
            comparisonText = "✅ Çıktı, projede tanımlanan beklenen çıktı ile eşleşiyor.\n\n"
                    + "Beklenen Çıktı:\n" + expectedOutput + "\n\n"
                    + "Öğrenci Çıktısı:\n" + submission.getOutput();
        } else {
            comparisonText = "❌ Çıktı, projede tanımlanan beklenen çıktı ile eşleşmiyor.\n\n"
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
        if (IAEController.currentProject == null) {
            showAlert(Alert.AlertType.ERROR, "Comparison Error", "Active project is not selected.");
            return;
        }
        List<StudentSubmission> submissions = IAEController.currentProject.getSubmissions();
        if (submissions == null || submissions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Comparison Error", "No submissions were found or processed. Please click 'Run' button first.");
            return;
        }

        String expectedOutputContent = IAEController.currentProject.getExpectedOutput();
        if (expectedOutputContent == null) {
            showAlert(Alert.AlertType.ERROR, "Comparison Error", "Expected output content missing from project settings.");
            return;
        }
        System.out.println("Comparison process begins...");
        for (StudentSubmission currentSubmission : submissions) {
            if (currentSubmission != null && currentSubmission.getResult() != null) {
                if (currentSubmission.getResult().isRunSuccessfully()) {
                    currentSubmission.compareOutput(expectedOutputContent);
                }
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
            stage.setTitle("Comparison Results - " + IAEController.currentProject.getProjectName());
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



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}







