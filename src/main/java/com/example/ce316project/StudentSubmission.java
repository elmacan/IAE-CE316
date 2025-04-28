package com.example.ce316project;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class StudentSubmission {
       private  String studentID;
       private  File zipFile;    //	öğrencinin gönderdiği orijinal ZIP dosyası
       private  File extractedDirectory;   // ZIP içeriğinin açıldığı klasör
       private Result result;
       private String studentOutput;


       public File findSourceFile(){
              //şimdilik deneme methodu sonra orijinali yapılcak
              System.out.println("armut");
              Configuration configuration=new Configuration();
              FileChooser fileChooser=new FileChooser();
              fileChooser.setTitle("source file seç");
              Window stage=null;
              File selectedFile=fileChooser.showOpenDialog(stage);
             // System.out.println(selectedFile.getAbsolutePath());
              configuration.setLanguagePath("gcc");
              configuration.setLanguageParameters("-o elma.exe");
              configuration.setCompiled(true);

              System.out.println(configuration.generateCompileCommand(selectedFile.getName()));



       return null;
       }




}