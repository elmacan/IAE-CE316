package com.example.ce316project.Controller;

import com.example.ce316project.Project;
import com.example.ce316project.StudentResultRow;
import com.example.ce316project.StudentSubmission;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator; // Sıralama için
import java.util.List;
import java.util.stream.Collectors; // Stream API için

public class ResultPageController {
    @FXML private TableView<StudentResultRow> studentTable;
    @FXML private TableColumn<StudentResultRow, String> studentNumberColumn;
    @FXML private TableColumn<StudentResultRow, String> compileStatusColumn;
    @FXML private TableColumn<StudentResultRow, String> runStatusColumn;
    @FXML private TableColumn<StudentResultRow, String> outputMatchColumn;
    @FXML
    private TextArea expectedOutputArea;
    @FXML private Button backButton;
    @FXML private Button helpButton;
    private Project selectedProject;

    private ObservableList<StudentResultRow> resultData = FXCollections.observableArrayList();

    private Scene previousScene;

    @FXML
    public void initialize() {
        studentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("studentID"));

        compileStatusColumn.setCellValueFactory(cellData -> {
            boolean status = cellData.getValue().isCompileStatus();
            return new SimpleStringProperty(status ? "Success" : "Fail");
        });

        runStatusColumn.setCellValueFactory(cellData -> {
            StudentResultRow row = cellData.getValue();
            String display;
            if (!row.isCompileStatus()) {
                display = "N/A";
            } else {
                display = row.isRunStatus() ? "Success" : "Fail";
            }
            return new SimpleStringProperty(display);
        });

        outputMatchColumn.setCellValueFactory(cellData -> {
            StudentResultRow row = cellData.getValue();
            String display;
            if (!row.isCompileStatus() || !row.isRunStatus()) {
                display = "N/A";
            } else {
                display = row.isOutputMatch() ? "✅ Match" : "❌ Mismatch";
            }
            return new SimpleStringProperty(display);
        });

        studentTable.setItems(resultData);
    }

    @FXML
    private void handleRowClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            StudentResultRow selectedRow = studentTable.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/comparison_view.fxml"));
                    Parent root = loader.load();

                    CompareOutputController controller = loader.getController();
                    StudentSubmission submission = selectedRow.getSubmission();

                    // Sadece bu satır değişti:
                    controller.loadOutputComparison(selectedProject, submission);

                    Stage stage = new Stage();
                    stage.setTitle("Output Comparison");
                    stage.setScene(new Scene(root));
                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void loadSubmissionResults(Project project, List<StudentSubmission> submissionList, Scene previous) {
        this.previousScene = previous;
        this.selectedProject = project; // ← Projeyi burada saklıyoruz

        if (submissionList == null) {
            System.err.println("A null list received to the loadSubmissionResults.");
            resultData.clear();
            return;
        }

        List<StudentSubmission> sortedSubmissions;
        try {
            sortedSubmissions = submissionList.stream()
                    .filter(s -> s != null && s.getStudentID() != null)
                    .sorted(Comparator.comparing(StudentSubmission::getStudentID,
                            Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error while sorting submissions: " + e.getMessage());
            e.printStackTrace();
            sortedSubmissions = submissionList;
            showAlert(Alert.AlertType.WARNING, "Sorting error", "An error occurred while sorting by student number. The list is displayed out of order.");
        }

        resultData.clear();
        for (StudentSubmission submission : sortedSubmissions) {
            if (submission != null && submission.getResult() != null) {
                resultData.add(new StudentResultRow(
                        submission.getStudentID() != null ? submission.getStudentID() : "Unknown ID",
                        submission.getResult().isCompiledSuccessfully(),
                        submission.getResult().isRunSuccessfully(),
                        submission.getResult().isOutputMatches(),
                        submission // StudentSubmission objesi
                ));
            } else {
                System.err.println("Warning: Null reference or result found in sorted list.");
            }
        }
    }

    public void loadSubmissionResults(List<StudentSubmission> submissionList, Scene previous) {
        this.previousScene = previous;

        if (submissionList == null) {
            System.err.println("A null list received to the loadSubmissionResults.");
            resultData.clear();
            return;
        }

        System.out.println("ResultPageController " + submissionList.size() + " size. Processing...");

        List<StudentSubmission> sortedSubmissions;
        try {
            sortedSubmissions = submissionList.stream()
                    .filter(s -> s != null && s.getStudentID() != null)
                    .sorted(Comparator.comparing(StudentSubmission::getStudentID,
                            Comparator.nullsLast(String::compareToIgnoreCase)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error while sorting submissions: " + e.getMessage());
            e.printStackTrace();
            sortedSubmissions = submissionList;
            showAlert(Alert.AlertType.WARNING,"Sorting error","An error occurred while sorting by student number. The list is displayed out of order.");
        }


        resultData.clear();
        for (StudentSubmission submission : sortedSubmissions) {
            if (submission != null && submission.getResult() != null) {
                resultData.add(new StudentResultRow(
                        submission.getStudentID() != null ? submission.getStudentID() : "Unknown ID",
                        submission.getResult().isCompiledSuccessfully(),
                        submission.getResult().isRunSuccessfully(),
                        submission.getResult().isOutputMatches(),
                        submission
                ));
            } else {
                System.err.println("Warning: Null reference or result found in sorted list.");
            }
        }

        System.out.println("TableView " + resultData.size() + " updated with line.");
    }

    @FXML
    void onBackButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (stage == null) {
                System.err.println("Unable to get Stage for back button.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
            Parent createProjectRoot = loader.load();

            Scene createProjectScene = new Scene(createProjectRoot);

            stage.setScene(createProjectScene);
            stage.setTitle("IAE Application - Create Project");
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

            System.out.println("Returned to the Create Project screen.");

        } catch (IOException e) {
            System.err.println("createProject.fxml error while loading: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "The Create Project screen could not be loaded. Check file path: /com/example/ce316project/createProject.fxml");
        } catch (NullPointerException e) {
            System.err.println("createProject.fxml not found (NullPointerException). Control the path.");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File Not Found", "The Create Project screen FXML file was not found. Expected: /com/example/ce316project/createProject.fxml");
        } catch (Exception e) {
            System.err.println("Unexpected error when returning to Create Project screen: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An error occurred while returning to the Create Project screen.");
        }
    }


    // ResultPageController.java
// ...
    @FXML
    private void showResultsHelpPage(MouseEvent event) {
        try {
            // !!! BU SATIRI KONTROL EDİN !!!
            // "/com/example.ce316project/helpPages.fxml" yolunun doğru olduğundan emin olun.
            // Ekran görüntünüze göre FXML dosyanız "src/main/resources/com/example/ce316project/helpPages.fxml" altında.
            String fxmlPath = "/com/example/ce316project/helpPages.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath)); // Satır ~160 burası olmalı

            // getResource null döndürürse, loader.load() bir sonraki satırda IllegalStateException verir.
            // Bunu önlemek için null kontrolü ekleyebiliriz:
            if (loader.getLocation() == null) { // Veya getClass().getResource(fxmlPath) == null
                System.err.println("FATAL ERROR: Help FXML file not found at: " + fxmlPath);
                showAlert(Alert.AlertType.ERROR, "Internal Error", "Could not find the help page template. Path: " + fxmlPath);
                return;
            }

            Parent helpPageRoot = loader.load(); // Eğer getLocation() null ise bu satır hata verir

            HelpControllers helpCtrl = loader.getController();
            if (helpCtrl == null) {
                System.err.println("Error: Could not get HelpController instance from FXML: " + fxmlPath);
                showAlert(Alert.AlertType.ERROR, "Help Error", "Failed to load help controller.");
                return;
            }

            helpCtrl.loadHelpContent(HelpControllers.HelpTopic.SUBMISSION_RESULTS);

            Stage helpStage = new Stage();
            helpStage.initModality(Modality.APPLICATION_MODAL);
            // ... (geri kalan kod)
            Scene helpScene = new Scene(helpPageRoot, 600, 400);
            helpStage.setScene(helpScene);
            helpStage.setResizable(true);
            helpStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading help page FXML: " + e.getMessage() + " (Path: /com/example/ce316project/helpPages.fxml)");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "Could not load the help page due to an I/O error.");
        } catch (Exception e) { // Diğer beklenmedik hatalar için
            System.err.println("An unexpected error occurred while showing help page: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Help Error", "An unexpected error occurred.");
        }
    }
    // ...
    @FXML
    void onHelpButtonClick(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Help", "...");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
