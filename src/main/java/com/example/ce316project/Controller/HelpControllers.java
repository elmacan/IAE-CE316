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

    @FXML
    private TextArea helpContentArea;

    public enum HelpTopic {
        CREATE_PROJECT,
        LIST_CONFIG,
        MAIN_MENU,
        PROJECT_LIST,
        SUBMISSION_RESULTS, // BU DEĞERİN VAR OLDUĞUNDAN EMİN OLUN
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

            case SUBMISSION_RESULTS: // YENİ EKLENEN CASE
                title = "Submission Results - User Guide";
                content = getSubmissionResultsHelpText();
                break;

            default:
                System.out.println("Warning: Help topic '" + topic + "' not explicitly handled. Displaying default message.");
                break;
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

    // --- CREATE PROJECT SAYFASI İÇİN DETAYLI YARDIM METNİ ---
    private String getCreateProjectHelpText() {
        return """
        Create New Project - Step-by-Step Guide

        This screen is where you define a new project to evaluate student programming assignments.
        Carefully fill in each section to ensure accurate processing.

        --- SECTION 1: PROJECT IDENTIFICATION ---

          Project Name:
           - Purpose: Give your project a unique and easily identifiable name.
           - Example: "CS101 - Assignment 3 - Sorting Algorithms"
           - Note: The system will prevent you from creating a project with an existing name.

        --- SECTION 2: EXECUTION SETUP ---

          Select Configuration:
           - Purpose: Choose how the student submissions will be compiled (if applicable) and run.
           - Options: Select from a predefined list of language configurations (e.g., "Java JDK 17", "Python 3.9", "GCC C++11").
           - Need a new one?
             - Click the "Add" button directly next to the ComboBox. This will take you to the "Add New Configuration" screen.
             - Alternatively, use the Gear icon () on the left sidebar to navigate to the "Manage Configurations" page.

        --- SECTION 3: STUDENT SUBMISSIONS ---

          Zip Directory:
           - Purpose: Specify the location of student submissions.
           - Action: Click the "Browse" button. A directory chooser dialog will appear.
           - Requirement: Select the FOLDER that contains all student submissions. Each student's work MUST be in a separate .zip file located directly within this chosen folder (not in subfolders).
           - Example: If you select "C:\\Users\\YourName\\Desktop\\Submissions", the system will look for .zip files like "C:\\Users\\YourName\\Desktop\\Submissions\\student1.zip", "C:\\Users\\YourName\\Desktop\\Submissions\\student2.zip", etc.

        --- SECTION 4: PROGRAM INPUTS & OUTPUTS ---

          Arguments (Optional):
           - Purpose: Provide any command-line arguments that need to be passed to the student programs when they are executed.
           - How to use:
             - Direct Input: Type the arguments directly into the text area. If there are multiple arguments, separate them with spaces, just as you would on a command line (e.g., "input.txt 100 verbose").
             - From File: Click the "Browse" button next to this field to select a .txt file. The content of this file (all lines) will be used as the arguments.
           - Note: If the programs do not require any arguments, leave this field blank.

          Expected Output File:
           - Purpose: Provide the "gold standard" output. The system will compare each student's program output against the content of this file.
           - Action: Click the "Browse" button. A file chooser dialog will appear.
           - Requirement: Select a plain text file (.txt) that contains the exact output your reference solution (or the assignment's correct solution) produces for the given inputs/arguments.
           - Importance: This is crucial for automated grading and comparison.

        --- SECTION 5: ACTIONS ---

        🔘 Page Buttons:
           - Create Project:
             - Prerequisite: All mandatory fields (Project Name, Configuration, Zip Directory, Expected Output File) must be filled.
             - Action: Clicking this initiates the project creation process:
               1. The project settings are saved.
               2. The system finds all .zip files in the specified Zip Directory.
               3. Each .zip file is extracted.
               4. Student code is compiled (if necessary) and run using the selected configuration and provided arguments.
               5. The actual output from each student's program is captured.
               6. (Comparison happens on the Results page or can be triggered after this step)
             - Result: You will be notified if the project is created successfully or if there's an issue (e.g., duplicate project name).
                        After successful creation, the "Compare" button usually becomes active.

           - Cancel:
             - Action: Discards any information entered on this page and returns you to the application's main entrance screen. No project will be created.

           - Compare:
             - Visibility: This button typically becomes visible and enabled only AFTER a project has been successfully created and its submissions have been processed (compiled/run).
             - Action: Navigates you to the "Results Page," where you can see a detailed breakdown of each student's performance, including whether their output matched the expected output.

        --- NAVIGATION (LEFT SIDEBAR) ---

        ⚙️ Icons:
           - Home : Takes you back to the main entrance page of the application.
           - Config List : Opens the screen for managing language configurations (add, view, import, export).
           - Project List : Shows a list of all previously created projects, allowing you to load and review them.
           - Help : Displays this help guide for the current "Create Project" page.

        --- TROUBLESHOOTING & TIPS ---
       ️    Missing Information: If you miss any required field, the system will prompt you.
      ️     Invalid Paths: Ensure that the Zip Directory and Expected Output File paths are correct and accessible.
       ️    System PATH: For compiled languages, ensure the necessary compilers (e.g., `javac`, `gcc`, `g++`) and for interpreted languages, the interpreters (e.g., `python`) are correctly installed and added to your system's PATH environment variable. The application relies on these being callable from the command line.
       ️    ZIP Structure: Student .zip files should ideally contain their source code directly, or in a clearly defined structure that your selected configuration's run command can handle.
        """;
    }

    private String getListConfigHelpText() {
        // Önceki cevaptaki gibi veya daha detaylı bir metin
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


    // HelpControllers.java
// ... (diğer getXXXHelpText metodlarınızdan sonra) ...

    private String getSubmissionResultsHelpText() {
        return """
    📊 Submission Results - Understanding the Report

    This screen provides a detailed report of how each student's submission performed
    after being processed by the system (compiled, run, and output compared).

    --- TABLE COLUMNS EXPLAINED ---

    👤 Student Number / Submission ID:
       - This column identifies the individual student or submission.
       - Typically, this is derived from the name of the .zip file submitted by the student
         (e.g., if the file was "12345.zip", this might show "12345").

    🛠️ Compile Status:
       - Indicates if the student's source code compiled successfully.
       - "Success": The code compiled without any errors, and an executable (if applicable) was generated.
       - "Fail": The compilation process encountered errors.
         (Future Enhancement: Clicking or hovering might show specific compiler error messages).
       - "N/A" (Not Applicable): This appears if the selected language configuration is for an
         interpreted language (like Python) that doesn't have a separate, explicit compilation step
         handled by an external compiler command.

    🏃 Run Status:
       - Shows whether the compiled program (or interpreted script) executed without runtime errors.
       - "Success": The program started and ran to completion as expected.
       - "Fail": The program started but crashed due to a runtime error (e.g., NullPointerException, division by zero, file not found error within the student's code, timeout).
         (Future Enhancement: Clicking or hovering might show runtime error messages or logs).
       - "N/A": This will typically appear if the "Compile Status" was "Fail," as an uncompiled
         program cannot be run. It might also appear if the run command itself was invalid.

    ⚖️ Output Match:
       - This is the core of the evaluation. It compares the actual output produced by the
         student's program against the "Expected Output File" you provided when creating the project.
       - "Match" (or "Pass"): The student's program output is identical to the expected output.
       - "Mismatch" (or "Fail"): The student's program output differs from the expected output.
         Even a single character difference (including whitespace or line endings, depending on
         the comparison logic) can result in a mismatch.
       - "N/A": This usually appears if the "Run Status" was "Fail," meaning the program didn't
         produce a complete output to compare, or if there was no expected output file defined.

    --- ACTIONS & NAVIGATION ---

    ⬅️ Back Button:
       - Located at the bottom right of the screen.
       - Clicking this button will take you back to the "Project List" screen, where you
         selected this project to view its results.

    ℹ️ Help Icon (This Icon):
       - Displays this help information, explaining how to interpret the submission results table.

    --- TIPS FOR INTERPRETATION ---
    - Ideal Scenario: "Success" in Compile, "Success" in Run, and "Match" in Output.
    - Compilation Failures: Usually syntax errors or missing dependencies in student code.
    - Runtime Failures: Often logical errors, unhandled exceptions, or incorrect resource handling
      within the student's program.
    - Output Mismatches: Can be due to algorithmic errors, incorrect formatting, or subtle
      differences in how the output is generated.

    For a more in-depth analysis of a specific student's failure, you might need to
    manually inspect their code and the error logs/outputs generated by the system,
    if such detailed logging is available or has been implemented.
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