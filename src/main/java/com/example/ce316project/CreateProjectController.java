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




    /*@FXML
    private void onCreateProjectButton() {
        String name = projectNameField.getText();
        Configuration selectedConfig = configurationComboBox.getValue();
        String input = argumentsArea.getText();
        String expectedOutput = expectedOutputFileField.getText();
        String zipPath = sourceFileField.getText();

        if (name.isEmpty() || selectedConfig == null || zipPath.isEmpty()) {
            System.out.println("Lütfen tüm gerekli alanları doldurun.");
            return;
        }

        Project newProject = new Project(name, selectedConfig, input, expectedOutput, new File(zipPath));
        IAEController.currentProject=newProject;

        File projectFile = new File(System.getProperty("user.home") + "/Documents/iae-app/projects.json");


        List<Project> updatedList = FileManager.saveProjectIfUnique(newProject, projectFile);

        if (updatedList != null) {
            IAEController.projectList = updatedList; // liste güncel
            System.out.println("Yeni proje başarıyla eklendi.");
            compareButton.setVisible(true);
            compareButton.setManaged(true);
            runButton.setVisible(true);
            runButton.setManaged(true);
            createProjectButton.setVisible(false);
            createProjectButton.setManaged(false);
        } else {
            System.out.println("Bu isimde bir proje zaten var. Lütfen farklı bir isim seçin.");
        }
    }*/

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



    @FXML
    private void onCompareButtonClick(ActionEvent event) {
        // 1. Proje ve submission varlığını kontrol et
        if (IAEController.currentProject == null) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Aktif proje bulunamadı. Lütfen bir proje oluşturun veya yükleyin.");
            System.out.println("Karşılaştırma Uyarısı: Aktif proje yok.");
            return;
        }

        List<StudentSubmission> submissions = IAEController.currentProject.getSubmissions();
        if (submissions == null || submissions.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Karşılaştırma Uyarısı", "Bu proje için hiç submission bulunamadı. Lütfen önce submission'ları çalıştırın.");
            System.out.println("Karşılaştırma Uyarısı: Submission bulunamadı veya henüz çalıştırılmadı.");
            return;
        }

        // 2. BEKLENEN ÇIKTIYI PROJE NESNESİNDEN AL
        String expectedOutput = IAEController.currentProject.getExpectedOutput();
        System.out.println("DEBUG: onCompareButtonClick - Projeden alınan beklenen çıktı: '" + expectedOutput + "'"); // Debug log

        // 3. PROJEDE SAKLANAN DEĞERİN boş/null OLMADIĞINI KONTROL ET
        if (expectedOutput == null || expectedOutput.trim().isEmpty()) {
            // Bu hata artık projenin kendisinin eksik bilgiyle oluşturulduğu anlamına gelir
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "Bu proje için tanımlanmış beklenen çıktı boş. Lütfen proje özelliklerini düzenleyin veya oluştururken doğru ayarlandığından emin olun.");
            System.out.println("Karşılaştırma Hatası: Projede saklanan beklenen çıktı boş veya null.");
            return; // Projenin verisi hatalıysa çık
        }

        // 4. Karşılaştırılacak submission'ı al (şimdilik ilkini alıyoruz)
        StudentSubmission submission = submissions.get(0);
        if (submission == null) {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Hatası", "İlk submission nesnesi beklenmedik şekilde null.");
            System.err.println("Hata: 0 indeksinde null submission bulundu, proje: " + IAEController.currentProject.getProjectName());
            return;
        }

        // 5. Projenin beklenen çıktısını kullanarak karşılaştırmayı yap
        System.out.println("Submission için çıktı karşılaştırılıyor..."); // Mümkünse submission ID ekle
        System.out.println("Kullanılan Beklenen Çıktı (projeden): \"" + expectedOutput + "\"");
        boolean comparisonResult = submission.compareOutput(expectedOutput); // Doğru compareOutput çağrılıyor

        // 6. Sonuçları göster
        if (comparisonResult) {
            showAlert(Alert.AlertType.INFORMATION, "Karşılaştırma Sonucu", "✅ Çıktı, projede tanımlanan beklenen çıktı ile eşleşiyor.");
            System.out.println("✅ Çıktı eşleşiyor.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Karşılaştırma Sonucu", "❌ Çıktı, projede tanımlanan beklenen çıktı ile eşleşmiyor. Detaylar için hata günlüğünü kontrol edin.");
            System.out.println("❌ Çıktı eşleşmiyor.");
        }
    }







    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Helper method (keep this in your controller)

    // Helper method (keep this in your controller)


}

