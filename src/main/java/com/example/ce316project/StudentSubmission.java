package com.example.ce316project;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StudentSubmission {
       private  String studentID;
       private  File zipFile;    //	öğrencinin gönderdiği orijinal ZIP dosyası
       private  File extractedDirectory;   // ZIP içeriğinin açıldığı klasör
       private Result result;
       private String studentOutput;


       public File findSourceFile(){
              //şimdilik deneme methodu sonra orijinali yapılcak
              System.out.println("armut");

              FileChooser fileChooser=new FileChooser();
              fileChooser.setTitle("source file seç");
              Window stage=null;
              File selectedFile=fileChooser.showOpenDialog(stage);
             // System.out.println(selectedFile.getAbsolutePath());
              //System.out.println(configuration.generateCompileCommand(selectedFile.getName()));




       return selectedFile;
       }

       public void compile(Configuration configuration){
              try {
                     File sourceFile=findSourceFile();
                     if (sourceFile==null) {
                            throw new IllegalStateException("Source file not found! Compilation is not possible.");
                     }


                     String compileCommand = configuration.generateCompileCommand(sourceFile.getName());

                     String[] compileParts = compileCommand.split(" ");

                     // ProcessBuilder ile derleme başlatıyoruz
                     ProcessBuilder pb = new ProcessBuilder(compileParts);
                     //pb.directory(extractedDirectory); // Doğru klasörde çalıştırıyoruz
                     pb.directory(sourceFile.getParentFile());
                     pb.redirectErrorStream(true); // stdout ve stderr akışlarını birleştir
                     Process process = pb.start();  //CMD açılır ve komut çalıştırılır

                     // Çıktıyı okuyup ekrana bastırıyoruz
                     InputStream is = process.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                     String line;
                     while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                     }

                     int exitCode = process.waitFor(); // Derleme bitene kadar bekliyoruz


                     //result = new Result();
                     //result.setStudentID(studentID);
                     //result.setCompiledSuccessfully(exitCode == 0); // exitCode 0 ise başarılıdır

                     if (exitCode != 0) {
                            System.out.println("Derleme başarısız oldu.");
                     } else {
                            System.out.println("Derleme başarılı!");
                     }

              } catch (Exception e) {
                     e.printStackTrace();
                     if (result == null) {
                          //  result = new Result();
                          //  result.setStudentID(studentID);
                     }
                     //result.setCompiledSuccessfully(false);
              }


       }




}