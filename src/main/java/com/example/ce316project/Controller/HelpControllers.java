package com.example.ce316project.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class HelpControllers {

    @FXML
    private Label helpTitleLabel;

    private String currentLoadedTitle = "Help";
    @FXML
    private TextArea helpContentArea;

    public enum HelpTopic {
        CREATE_PROJECT,
        LIST_CONFIG,
        MAIN_MENU,
        PROJECT_LIST,
        SUBMISSION_RESULTS,
        SAVE_EDIT_CONFIG,
        EDIT_CONFIGURATION,
        VIEW_RESULTS
        // Diğer konular...
    }

    public void loadHelpContent(HelpTopic topic) {
        String title = "General Help"; // Varsayılan başlık
        String content = "No specific help content found for the selected topic. Please check the available options."; // Varsayılan içerik

        switch (topic) {
            case CREATE_PROJECT:
                title = "Create New Project - User Guide";
                content = getCreateProjectHelpText();
                break;
            case LIST_CONFIG:
                title = "Manage Configurations - User Guide";
                content = getListConfigHelpText();
                break;

            case SUBMISSION_RESULTS:
                title = "Submission Results - User Guide";
                content = getSubmissionResultsHelpText();
                break;
            case SAVE_EDIT_CONFIG:
                title = "Add/Edit Configuration - User Guide";
                content = getSaveEditConfigHelpText();
                break;

            default:
                System.out.println("Warning: Help topic '" + topic + "' not explicitly handled. Displaying default message.");
                break;
        }
        this.currentLoadedTitle = title;

        if (helpTitleLabel != null) {
            helpTitleLabel.setText(title);
        } else {
            System.err.println("HelpControllers: helpTitleLabel is null! Check FXML fx:id for the title Label.");
        }

        if (helpTitleLabel != null) {
            helpTitleLabel.setText(title);
        }
        if (helpContentArea != null) {
            helpContentArea.setText(content);
            helpContentArea.setWrapText(true);
            helpContentArea.setEditable(false);
            helpContentArea.positionCaret(0); // Metin yüklendikten sonra en başa kaydır
        }
    }

    public String getLoadedTitleForStage() {
        return this.currentLoadedTitle;
    }


    private String getCreateProjectHelpText() {
        return """
        --- SECTION 1: PROJECT IDENTIFICATION ---
           - Purpose: Give your project a unique and easily identifiable name.
       
        --- SECTION 2: EXECUTION SETUP ---
           - Purpose: Choose how the student submissions will be compiled and run.
           - Options: Select from a predefined list of language configurations.
           - Need a new one?
             - Click the "Add" button directly next to the ComboBox. This will take you to the "Add New Configuration" screen.
       
        --- SECTION 3: STUDENT SUBMISSIONS ---
           - Action: Click the "Browse" button. A directory chooser dialog will appear.
           - Requirement: Select the FOLDER that contains all student submissions. Each student's work MUST be in a separate .zip file located directly within this chosen folder (not in subfolders).
          
        --- SECTION 4: PROGRAM INPUTS & OUTPUTS ---
             - Direct Input: Type the arguments directly into the text area. If there are multiple arguments, separate them with spaces, just as you would on a command line (e.g., "input.txt 100 verbose").
             - From File: Click the "Browse" button next to this field to select a .txt file. The content of this file (all lines) will be used as the arguments.

          Expected Output File
           - Action: Click the "Browse" button. A file chooser dialog will appear.
           - Requirement: Select a plain text file (.txt) that contains the exact output your reference solution (or the assignment's correct solution) produces for the given inputs/arguments.

        --- SECTION 5: ACTIONS ---
             - Prerequisite: All mandatory fields (Project Name, Configuration, Zip Directory, Expected Output File) must be filled.
             -After successful creation, the "Compare" button usually becomes active.
        """;
    }

    private String getListConfigHelpText() {
        return """
        ℹ️ Manage Configurations - User Guide

        This page allows you to view, add, import, and export language configurations.
        A configuration defines how the application handles source code for a specific
        programming language or environment.

        Key Elements of a Configuration:
        - Language Name: A user-friendly name (e.g., "Java 17 OpenJDK").
        - Language/Compiler Path: The command to invoke the compiler or interpreter (e.g., "javac", "python", "C:\\MinGW\\bin\\g++.exe"). If it's in the system PATH, just the command name is usually sufficient.
        - Language Parameters (Compilation Flags): Arguments passed to the compiler (e.g., "-std=c++17", "-cp .").
        - Run Command: The command to execute the compiled program or script. Use placeholders like {mainFile} for the student's primary source file (without extension) or {executableName} for the compiled output. Example for Java: "java {mainFile}". Example for C: "./{executableName}".
        - Compiled: A checkbox indicating if the language requires a separate compilation step before running.

        Actions:
        - Add Configuration: Opens a form to define a new configuration.
        - Import: Allows you to load configurations from a .json file. Useful for sharing setups.
        - Export: (After selecting a configuration from the list) Saves the selected configuration to a .json file.
        - Back: Returns to the previous screen.
        - (Future: Edit/Delete existing configurations directly from the list).
        """;
    }



    private String getSubmissionResultsHelpText() {
        return """
        Submission Results - Quick Guide
    
          This page shows the evaluation outcome for each student's submission in the current project.
    
          --- TABLE COLUMNS ---
    
           Student ID:
             - Identifier for the submission (usually from the ZIP file name).
    
           Compile Status:
             - "Success": Code compiled without errors.
             - "Fail": Compilation errors occurred.
             - "N/A": No separate compilation step (e.g., Python).
    
           Run Status:
             - "Success": Program executed(completion).
             - "Fail": Program crashed during execution (runtime error).
             - "N/A": Not run, typically due to a compile fail.
    
           Output Match:
             - "Match/Pass": Student's output is identical to the expected output.
             - "Mismatch/Fail": Student's output differs from the expected output.
             - "N/A": Not compared, usually because the program didn't run successfully.
            """;
    }

    private String getSaveEditConfigHelpText() {
        return """
         Add/Edit Configuration - Quick Guide

        Language Name:
           - A unique name for this setup.
           - This name appears in project configuration dropdowns. (Required)

        Language Type:
           - Choose the category:
             - "Compiled": Needs a compile step (e.g., Java, C++).
             - "Interpreted": Runs directly (e.g., Python).

        Program Path:
           - Command or full path to the compiler/interpreter (e.g., "javac", "python", "C:\\path\\to\\g++.exe").
           - Use "Browse" to find the file.

        Parameters (Optional)

        Runner Command:
           - Command to execute the code after compilation (if any).
           - Use placeholders:
             - `{mainFile}`: Student's main file name (no extension).
             - `{executableName}`: Name of compiled output (e.g., "main.exe").
        """;
    }




    @FXML
    public void initialize() {
        if (helpTitleLabel != null) {
            helpTitleLabel.setText("Help"); // Varsayılan başlık
        }
        if (helpContentArea != null) {
            // Varsayılan bir metin göster, loadHelpContent çağrıldığında üzerine yazılacak
            helpContentArea.setText("Help content will be loaded based on the context...\nIf you see this, it means a specific help topic was not requested when this window was opened.");
            helpContentArea.setWrapText(true);
            helpContentArea.setEditable(false);
            helpContentArea.positionCaret(0);
        }
        System.out.println("HelpController initialized. Awaiting specific topic via loadHelpContent().");
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}