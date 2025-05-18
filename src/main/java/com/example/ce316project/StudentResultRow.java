package com.example.ce316project;

public class StudentResultRow {
    private String studentID;
    private boolean compileStatus;
    private boolean runStatus;
    private boolean outputMatch;

    private final StudentSubmission submission; // Yeni alan

    public StudentResultRow(String studentID, boolean compileStatus, boolean runStatus, boolean outputMatch, StudentSubmission submission) {
        this.studentID = studentID;
        this.compileStatus = compileStatus;
        this.runStatus = runStatus;
        this.outputMatch = outputMatch;
        this.submission = submission;
    }

    public String getStudentID() { return studentID; }
    public boolean isCompileStatus() { return compileStatus; }
    public boolean isRunStatus() { return runStatus; }
    public boolean isOutputMatch() { return outputMatch; }
    public StudentSubmission getSubmission() { return submission; }
}


