package com.example.ce316project.Controller;
import java.util.Objects; // Objects.requireNonNull için
import javafx.scene.control.Alert;

import com.example.ce316project.IAEManager;
import com.example.ce316project.Project;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListProjectController implements Initializable {

    @FXML
    private ListView<Project> projectListView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Project> projectList = IAEManager.projectList;


        if (projectList != null) {
            projectListView.getItems().setAll(projectList);

            // Custom cell: Her projeye buton ekle
            projectListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Project project, boolean empty) {
                    super.updateItem(project, empty);
                    if (empty || project == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Text projectName = new Text(project.getProjectName());
                        Button seeResultButton = new Button("See Result");

                        seeResultButton.setOnAction(event -> showResultPage(event, project));

                        Region spacer = new Region();
                        HBox.setHgrow(spacer, Priority.ALWAYS); // Flexible space

                        HBox hBox = new HBox(10, projectName, spacer, seeResultButton);

                        setGraphic(hBox);
                    }
                }
            });
        }
    }

    private void showResultPage(ActionEvent event, Project selectedProject) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/resultPage.fxml"));
            Parent root = loader.load();

            ResultPageController controller = loader.getController();
            // Burada projeyi ve onun submissionlarını birlikte gönderiyoruz
            controller.loadSubmissionResults(selectedProject, selectedProject.getSubmissions(),
                    ((Node) event.getSource()).getScene());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Submission Results - " + selectedProject.getProjectName());
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load result page: " + e.getMessage());
            e.printStackTrace();
        }
    }


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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ce316project/entrancePage.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void updateList(List<Project> updatedProjects) {
        projectListView.getItems().setAll(updatedProjects);
    }
}
