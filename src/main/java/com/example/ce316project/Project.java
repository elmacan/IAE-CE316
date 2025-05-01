package com.example.ce316project;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
      @SerializedName("name")
      private String projectName;

      @SerializedName("configuration")
      private Configuration projectConfig;

      private String input;
      private String expectedOutput;

      private File zipDirectory; // JSON'da "zipDirectory": "submissions/" gibi olacak

      private List<StudentSubmission> submissions;

      public Project() {
            // Default constructor
      }

      public Project(String projectName, Configuration projectConfig, String input, String expectedOutput, File zipDirectory) {
            this.projectName = projectName;
            this.projectConfig = projectConfig;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.zipDirectory = zipDirectory;
            this.submissions = new ArrayList<>();
      }
      public String getProjectName() {
            return projectName;
      }

      public Configuration getProjectConfig() {
            return projectConfig;
      }

      public String getInput() {
            return input;
      }

      public String getExpectedOutput() {
            return expectedOutput;
      }

      public File getZipDirectory() {
            return zipDirectory;
      }

      public List<StudentSubmission> getSubmissions() {
            return submissions;
      }

      public void addSubmission(StudentSubmission submission) {
            if(submissions == null){
                  throw new IllegalStateException("Submissions list is not initialized.");
            }
            submissions.add(submission);
      }

      public List<Result> getResults() {
            List<Result> results = new ArrayList<>();
            for (StudentSubmission submission : submissions) {
                  results.add(submission.getResult());
            }
            return results;
      }
      @Override
      public String toString() {
            return "Project{" +
                    "name='" + projectName + '\'' +
                    ", configuration=" + projectConfig +
                    ", zipDirectory=" + zipDirectory +
                    '}';
      }





}
