package com.example.ce316project.Controller;

import com.example.ce316project.Project;
import com.example.ce316project.StudentSubmission;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class CompareOutputController {
    @FXML private TextArea expectedOutputArea;
    @FXML private TextArea actualOutputArea;
    @FXML private TextArea errorLogArea;

    public void setData(String expected, String actual, String error) {
        expectedOutputArea.setText(expected);
        actualOutputArea.setText(actual);
        errorLogArea.setText(error);
    }

    public void loadOutputComparison(Project project, StudentSubmission submission) {
        if (project != null) {
            expectedOutputArea.setText(project.getExpectedOutputContent()); // ← Dosya ise içeriği okunur
        } else {
            expectedOutputArea.setText("Expected output not available.");
        }

        if (submission != null && submission.getResult() != null) {
            actualOutputArea.setText(submission.getStudentOutput());
            errorLogArea.setText(submission.getResult().getErrorLog());
        } else {
            actualOutputArea.setText("No actual output found.");
            errorLogArea.setText("No error log available.");
        }
    }
}

