package com.example.ce316project;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private Button backButton;
    @FXML private Button helpButton;

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
                        submission.getResult().isOutputMatches()
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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entranceScreen.fxml"));
            Parent entranceRoot = loader.load();

            Scene entranceScene = new Scene(entranceRoot);

            stage.setScene(entranceScene);
            stage.setTitle("IAE Application - Entrance");
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();

            System.out.println("Returned to the main login screen.");

        } catch (IOException e) {
            System.err.println("entranceScreen.fxml error while loading: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "The main login screen could not be loaded. Is the file path correct?");
        } catch (NullPointerException e) {
            // getResource null döndürürse (dosya yolu yanlışsa)
            System.err.println("entranceScreen.fxml did not found (NullPointerException).Control the path.");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "File did not found", "The main login screen file was not found.");
        } catch (Exception e) {
            // Diğer beklenmedik hatalar
            System.err.println("Unexpected error when returning to login screen: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", "An error occurred while returning to the login screen.");
        }
    }
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
