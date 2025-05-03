/*package com.example.ce316project;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ResultPageController {

    @FXML private TableView<Result> resultTable;
    @FXML private TableColumn<Result, String> studentNumberColumn;
    @FXML private TableColumn<Result, String> compileStatusColumn;
    @FXML private TableColumn<Result, String> runStatusColumn;
    @FXML private TableColumn<Result, String> outputMatchColumn;

    @FXML
    public void initialize() {
        studentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        compileStatusColumn.setCellValueFactory(new PropertyValueFactory<>("compileStatus"));
        runStatusColumn.setCellValueFactory(new PropertyValueFactory<>("runStatus"));

        outputMatchColumn.setCellValueFactory(cellData -> {
            boolean matches = cellData.getValue().isOutputMatches();
            return new SimpleStringProperty(matches ? "✅" : "❌");
        });
    }

    public void setSubmissions(List<StudentSubmission> submissions, String expectedOutput) {
        List<Result> results = new ArrayList<>();

        for (StudentSubmission submission : submissions) {
            submission.compareOutput(expectedOutput);
            Result result = submission.getResult();
            result.setStudentNumber(submission.getStudentID());
            results.add(result);
        }

        resultTable.getItems().setAll(results);
    }
}*/


package com.example.ce316project;

// ... (Diğer importlar: javafx..., java.util.List, java.util.Comparator, java.util.stream.Collectors)
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // FXMLLoader importu
import javafx.scene.Node;
import javafx.scene.Parent;   // Parent importu
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality; // İsteğe bağlı: Pencere türünü ayarlamak için
import javafx.stage.Stage;

import java.io.IOException; // IOException importu
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResultPageController {

    // --- FXML Alanları ---
    @FXML
    private TableView<Result> resultTable;
    @FXML private TableColumn<Result, String> studentNumberColumn;
    @FXML private TableColumn<Result, String> compileStatusColumn;
    @FXML private TableColumn<Result, String> runStatusColumn;
    @FXML private TableColumn<Result, String> outputMatchColumn;
    @FXML private Button backButton; // Geri butonu varsa

    // Bu metod, FXML'deki root elemanı almak için kullanılır (varsa)
    @FXML private javafx.scene.layout.VBox rootPane; // FXML'deki en dış VBox'un fx:id'si rootPane olmalı
    private Scene previousScene;

    // Geri butonu işlevselliği için önceki sahne (bu yaklaşımda pek kullanılmaz)
    // private Scene previousScene;

    @FXML
    public void initialize() {
        // Sütunları ayarlama (önceki gibi)
        studentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentNumber"));
        compileStatusColumn.setCellValueFactory(new PropertyValueFactory<>("compileStatus"));
        runStatusColumn.setCellValueFactory(new PropertyValueFactory<>("runStatus"));
        outputMatchColumn.setCellValueFactory(new PropertyValueFactory<>("outputMatch"));
    }


    public void populateAndShowInNewWindow(List<Result> resultsList) {
        if (resultsList == null) {
            System.err.println("populateAndShowInNewWindow'a null liste geldi.");
            resultTable.getItems().clear();
            return;
        }

        // 1. Sırala (isteğe bağlı, önceki gibi)
        List<Result> sortedList;
        try {
            sortedList = resultsList.stream()
                    .sorted(Comparator.comparing(Result::getStudentNumber, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Sıralama hatası: " + e.getMessage());
            sortedList = resultsList; // Hata durumunda sırasız kullan
        }

        // 2. Tabloyu Doldur
        resultTable.getItems().setAll(sortedList);
        System.out.println("TableView (Yeni Pencere) güncellendi.");

        // 3. Yeni Pencere Oluştur ve Göster (ÖNERİLMEZ KISIM)
        try {
            // Bu controller ile ilişkili sahneyi almanın bir yolu (FXML root elemanı üzerinden)
            // Bu yaklaşım, FXML'in zaten yüklenmiş olduğunu varsayar, bu da mantığa aykırı.
            // Daha güvenilir yol FXML'i burada tekrar yüklemektir ama bu daha da kötü.
            if (rootPane == null || rootPane.getScene() == null) {
                // Eğer FXML root elemanı veya sahne yoksa, FXML'i tekrar yüklemeyi deneyebiliriz
                // ama bu çok verimsiz ve hatalara açık.
                System.err.println("Yeni pencere gösterilemiyor: Sahne veya rootPane bulunamadı.");
                showAlert(Alert.AlertType.ERROR, "Gösterim Hatası", "Sonuç sayfası penceresi oluşturulamadı.");
                return;


            } else {
                // Mevcut sahneyi kullanarak yeni bir pencere açmaya çalışmak (genellikle işe yaramaz)
                Stage newStage = new Stage();
                newStage.setTitle("Karşılaştırma Sonuçları (Yeni)");
                // Yeni bir sahne oluşturmak gerekir, mevcut olanı kopyalamak zor.
                // newStage.setScene(rootPane.getScene()); // Bu genellikle çalışmaz veya beklenmedik sonuç verir.

                // En iyi ihtimalle, FXML'i burada tekrar yüklemek:
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/ResultPage.fxml"));
                // ÖNEMLİ: Yeni bir controller örneği oluşturulacak, bu yüzden veriyi tekrar ayarlamamız gerekir.
                Parent newRoot = loader.load();
                ResultPageController newControllerInstance = loader.getController();
                newControllerInstance.populateTableOnly(resultsList); // Sadece tabloyu dolduran ayrı bir metod gerekir

                newStage.setScene(new Scene(newRoot));
                newStage.initModality(Modality.APPLICATION_MODAL); // İsteğe bağlı
                newStage.show();


                System.out.println("Yeni sonuç penceresi açıldı.");
            }

        } catch (Exception e) {
            System.err.println("Yeni sonuç penceresi gösterilirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Gösterim Hatası", "Sonuç sayfası penceresi oluşturulamadı.");
        }
    }


    private void populateTableOnly(List<Result> resultsList) {
        if (resultsList == null) {
            resultTable.getItems().clear();
            return;
        }
        // Sıralama
        List<Result> sortedList;
        try {
            sortedList = resultsList.stream()
                    .sorted(Comparator.comparing(Result::getStudentNumber, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            sortedList = resultsList;
        }
        // Doldurma
        resultTable.getItems().setAll(sortedList);
    }


    // Geri Butonu (Yeni pencere yaklaşımında anlamsız hale gelir, pencereyi kapatmalı)
    @FXML
    void onBackButtonClick(ActionEvent event) {
        // Yeni pencere açıldıysa, bu buton pencereyi kapatmalı
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (stage != null) {
            stage.close(); // Pencereyi kapat
        }
    }



    public void setResults(List<Result> resultsList, Scene previous) { // <-- ADI BURADA setResults (çoğul)
        this.previousScene = previous;

        if (resultsList == null) {
            // ... null kontrolü ...
            return;
        }
        // ... sıralama ve tabloyu doldurma ...
        List<Result> sortedList;
        try {
            sortedList = resultsList.stream()
                    .sorted(Comparator.comparing(Result::getStudentNumber, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Sonuçlar sıralanırken hata oluştu: " + e.getMessage());
            sortedList = resultsList;
        }
        this.resultTable.getItems().setAll(sortedList);
    }


    // showAlert yardımcı metodu
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}