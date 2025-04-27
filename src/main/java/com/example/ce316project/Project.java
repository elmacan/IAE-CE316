package com.example.ce316project;

import java.io.File;
import java.util.List;

public class Project {
      private  String ProjectName;
      private  Configuration projectConfig;
      private  String input;//  komut satırı argümanları , file da olabilir
      private  String expectedOutput; // hocanın beklediği gerçek sonuç , bu da file olabilir
      private  File zipDirectory; // öğrencilerin ziplerinin bulunduğu klasör
      private  List<StudentSubmission> submissions;

      public void addSubmission(StudentSubmission submission) {
            if(submissions == null){
                  throw new IllegalStateException("Submissions list is not initialized.");
            }
            submissions.add(submission);
      }


}
