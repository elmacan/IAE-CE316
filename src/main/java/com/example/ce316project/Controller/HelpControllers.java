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

        MAIN_MENU,

        SUBMISSION_RESULTS,
        SAVE_EDIT_CONFIG,
        VIEW_CONFIG_DETAILS,
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
            case SUBMISSION_RESULTS:
                title = "Submission Results - User Guide";
                content = getSubmissionResultsHelpText();
                break;
            case SAVE_EDIT_CONFIG:
                title = "Add/Edit Configuration - User Guide";
                content = getSaveEditConfigHelpText();
                break;
            case VIEW_CONFIG_DETAILS:
                title = "View Configuration Details - Guide";
                content = getViewConfigDetailsHelpText();
                break;
            case MAIN_MENU:
                title = "Main Menu & Overview - Guide";
                content = getMainMenuHelpText();
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
           - Not used during compilation or execution.
           - A unique name for this setup.
           - This name appears in project configuration dropdowns. (Required)

        Language Type:
           - Choose the category:
             - "Compiled": Needs a compile step (e.g., Java, C++).
             - "Interpreted": Runs directly (e.g., Python).

        Program Path:
           - If it's in your system PATH, you can just use the command name (e.g., gcc, python).
           - Or use Browse to select the full path (e.g., C:\\Compilers\\gcc.exe)

        Parameters (Optional)
            Extra command-line flags for the compiler/interpreter.
            These are placed before the source files in compile commands.
                Examples:
                    C: -o main.exe
                    Java: -d out
                    Python: -O

        Runner Command:
           For interpreted languages, enter only the file name (e.g. main.py)
           For compiled languages, enter the full command (e.g. java Main).
           
           
                🔹 C
                Language [C]
                Language Type [Compiled]
                Program Path [gcc]
                Parameters [-o main.exe]
                Runner Command [main.exe]
                
                🔹 Java
                Language [Java]
                Language Type [Compiled]
                Program Path [javac]
                Parameters [-d out(Optional)]
                Runner Command [java -cp out Main]
                
                🔹 Python
                 Language [Python]
                 Language Type [Interpreted]
                 Program Path [python]
                 Parameters	[-O (optional)]
                 Runner Command	[main.py]
        """;
    }


    private String getViewConfigDetailsHelpText() {
        return """
        View Configuration Details - Guide
        
        Language Name:
           - The user-friendly name for this configuration.

        Language Path (Compiler/Interpreter Path):
           - The command or full file path for the language's compiler or interpreter
             (e.g., "python").

        Parameters (Compilation/Interpretation Flags):
           - Any command-line arguments or flags passed to the compiler or interpreter.

        Run Command:
           - The command template used to execute the code.
           - May use placeholders like `{mainFile}` (for the student's main file name)
             or `{executableName}` (for compiled output).
           - Example: "python {mainFile}.py", "java {mainFile}".

        Is Compiled:
           - "Yes": The language requires a separate compilation step.
           - "No": The language is interpreted.
        """;
    }


    // HelpControllers.java
    private String getMainMenuHelpText() {
        return """

    Welcome to the Integrated Assignment Environment (IAE)!

    This application helps you manage and evaluate student programming assignments.
    From this Main Menu, you can navigate to various sections of the application.

    --- AVAILABLE ACTIONS ---

    🔘 Create New Project:
       - Click this to start setting up a new programming assignment for evaluation.
       - You will be guided to define project details, select language configurations,
         specify student submission locations, and provide expected outputs.

    🔘 List Configurations:
       - Access this section to view, add, or modify settings for different
         programming languages (e.g., Java, Python, C++). These configurations
         determine how student code is compiled and executed.

    🔘 Previous Projects:
       - Select this option to view a list of projects you have created earlier.
       - From the list, you can typically view project details and submission results.

    🔘 Help (This Button/Icon):
       - Displays this general help guide, providing an overview of the application
         and navigation for the Main Menu.

    --- GENERAL NAVIGATION TIPS ---
    - Sidebar Icons (on other pages): Look for icons on the left sidebar of other screens
      for quick navigation back to Home (), Configuration List (), Project List (), etc.
    - Contextual Help (ℹ): On specific pages, an 'i' icon provides help relevant to that page.

    We hope you find this application useful for managing your assignments:)
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