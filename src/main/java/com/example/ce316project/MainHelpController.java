package com.example.ce316project;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainHelpController {
    @FXML
    private TextArea helpTextArea;

    @FXML
    public void initialize() {
        String helpText = """
            🆘 Help Page – Integrated Assignment Environment

            🧩 Overview
            This application is designed to automate the evaluation of student programming assignments. It allows lecturers to process ZIP submissions, compile and run the code, compare the output against an expected result, and display a summary of each student’s success or failure in a structured and user-friendly interface.

            🏠 Main Menu
            Location: First screen
            Title: The Integrated Environment

            📌 Buttons:
            Create a New Project
            Starts a new project for a programming assignment.

            List Configurations
            View and manage configurations for different programming languages (e.g., Java, C, Python).

            Previous Projects
            Open and review previously created projects.

            Help Menu
            Displays this help screen.

            User Guide
            Opens a more detailed documentation or guide (optional link can be added).

            📁 Create Project Screen
            Location: Shown after clicking “Create a New Project”

            📝 Fields:
            Project Name
            A unique name for your project. Duplicate names are not allowed.

            Select Configuration
            Choose a language configuration (e.g., Java, Python).
            ➕ Click “Add” to define a new configuration if needed.

            Zip Directory
            Select the folder containing ZIP files submitted by students.

            Arguments
            Command-line arguments to be passed to student programs. Can be typed directly or selected from a .txt file.

            Expected Output
            The correct output expected from student programs. This can also be provided via a .txt file.

            🔘 Buttons:
            Browse
            Opens a file or folder selection dialog.

            Create Project
            Starts the process: extracts ZIP files, compiles and runs student programs, compares results.

            Cancel
            Returns to the main menu without creating a project.

            ✅ After Project Creation
            Once a project is successfully created:

            The system extracts all ZIP files.

            It compiles and executes student code.

            The output is compared with the expected result.

            All results are saved and available for review.

            📊 Result Screen (Submission Results)
            This screen appears after a project is created and processed.

            Displays each student’s status in a table:

            Whether compilation was successful

            Whether the program executed correctly

            Whether the actual output matches the expected output

            🔙 Other Features
            Help Menu
            Opens this help guide.

            Back
            Navigates to the previous screen.

            List Configurations
            Shows available language/tool configurations.

            Import/Export Configurations
            Save or load configuration files for reuse or sharing across devices.

            ⚠️ Error Handling and Messages
            All required fields must be filled to create a project.

            ZIP directory must exist and be valid.

            Errors during compilation or execution are logged per student.

            The system assumes compilers like javac, python, or gcc are available in the system PATH.
            """;

        helpTextArea.setText(helpText);
    }
}
