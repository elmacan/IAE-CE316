package com.example.ce316project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project {
      private  String projectName;
      private  Configuration projectConfig;
      private  String input;//  komut satırı argümanları , file da olabilir
      private  String expectedOutput; // hocanın beklediği gerçek sonuç , bu da file olabilir
      private  File zipDirectory; // öğrencilerin ziplerinin bulunduğu klasör
      private  List<StudentSubmission> submissions;

      public Project(String projectName, Configuration projectConfig, String input, String expectedOutput, File zipDirectory) {
            this.projectName = projectName;
            this.projectConfig = projectConfig;
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.zipDirectory = zipDirectory;
            this.submissions = new ArrayList<>();
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



}
