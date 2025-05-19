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
import javafx.scene.input.MouseEvent;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/resultPage.fxml"));
            Parent resultPageRoot = loader.load();
            ResultPageController resultController = loader.getController();

            if (resultController == null) {
                System.err.println("Error: Could not get Controller for resultPage.fxml.");
                showAlert(Alert.AlertType.ERROR, "Controller Error", "Failed to load results page controller.");
                return;
            }

            resultController.loadSubmissionResults(
                    IAEManager.currentProject,
                    IAEManager.currentProject.getSubmissions(),
                    ((Node) event.getSource()).getScene()
            );

            Scene resultScene = new Scene(resultPageRoot);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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



    @FXML
    public void onIconHome(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) createProjectButton.getScene().getWindow();
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
            Stage stage = (Stage) createProjectButton.getScene().getWindow();
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
            Stage stage = (Stage) createProjectButton.getScene().getWindow();
            IAEManager.sceneStack.push(stage.getScene());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Navigation Error Failed to load the entrance page.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void showHelpPage(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/helpPages.fxml"));
            Parent helpPageRoot = loader.load();

            HelpControllers helpCtrl = loader.getController();
            if (helpCtrl == null) {
                System.err.println("CreateProjectController: Error - Could not get HelpController instance.");
                showAlert(Alert.AlertType.ERROR, "Help Error", "Failed to load help controller.");
                return;
            }

            // "Create Project" sayfası için spesifik yardım konusunu yükle
            helpCtrl.loadHelpContent(HelpControllers.HelpTopic.CREATE_PROJECT);

            // Yeni bir Stage (pencere) oluştur
            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL); // Ana pencereyi bloke et

            // Sahip pencereyi (owner) ayarla
            Window ownerWindow = null;
            Node sourceNode = null;

            if (event != null && event.getSource() instanceof Node) {
                sourceNode = (Node) event.getSource(); // Tıklanan ikon
            }

            // Önce olay kaynağından (tıklanan ikon) ana pencereyi almayı dene
            if (sourceNode != null && sourceNode.getScene() != null && sourceNode.getScene().getWindow() != null) {
                ownerWindow = sourceNode.getScene().getWindow();
            }
            // Eğer olay kaynağından alınamazsa, formdaki bilinen bir elemandan (projectNameField) almayı dene
            else if (projectNameField != null && projectNameField.getScene() != null && projectNameField.getScene().getWindow() != null) {
                ownerWindow = projectNameField.getScene().getWindow();
            }
            // Başka bir bilinen eleman üzerinden de fallback eklenebilir, örn: createProjectButton

            if (ownerWindow != null) {
                helpStage.initOwner(ownerWindow);
            } else {
                System.err.println(this.getClass().getSimpleName() + ": Could not determine owner window for help page. Centering on screen.");
            }

            // Pencere başlığını HelpController'dan al
            if (helpCtrl.getLoadedTitleForStage() != null) {
                helpStage.setTitle(helpCtrl.getLoadedTitleForStage());
            } else {
                helpStage.setTitle("Create Project - Help"); // Varsayılan başlık
            }

            // Yardım penceresi için istenen boyutlar
            double helpWidth = 750;  // İstediğiniz genişlik
            double helpHeight = 400; // İstediğiniz yükseklik (içeriğinize göre ayarlayın)
            Scene helpScene = new Scene(helpPageRoot, helpWidth, helpHeight);
            helpStage.setScene(helpScene);
            helpStage.setResizable(true); // Kullanıcı boyutlandırabilsin

            // Pencereyi göstermeden ÖNCE pozisyonunu ayarlamak için final değişken
            final Window finalOwnerWindow = ownerWindow;

            if (finalOwnerWindow != null) {
                // Sahip pencerenin merkezine göre pozisyonla (pencere gösterildikten sonra)
                helpStage.setOnShown(e -> {
                    // Bu kontrol, pencere gösterildiğinde boyutların kesinleşmiş olmasını sağlar
                    if (helpStage.isShowing() && finalOwnerWindow.isShowing()) {
                        helpStage.setX(finalOwnerWindow.getX() + (finalOwnerWindow.getWidth() - helpStage.getWidth()) / 2);
                        helpStage.setY(finalOwnerWindow.getY() + (finalOwnerWindow.getHeight() - helpStage.getHeight()) / 2);
                    }
                });
            } else {
                // Sahip pencere bulunamazsa ekranın ortasına yerleştir
                helpStage.centerOnScreen();
            }

            helpStage.showAndWait(); // Pencere kapanana kadar bekle

        } catch (IOException e) {
            System.err.println("CreateProjectController: Error loading help page FXML - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "Could not load the help page.");
        } catch (Exception e) { // Diğer olası hatalar için
            System.err.println("CreateProjectController: Unexpected error showing help - " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "An unexpected error occurred while showing help.");
        }
    }


}







