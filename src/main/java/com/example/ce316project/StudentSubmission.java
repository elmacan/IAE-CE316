package com.example.ce316project;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

        public class StudentSubmission {
               private String studentID;
               private File zipFile;
               private File extractedDirectory;
               private Result result;
               private String studentOutput;

               public StudentSubmission(String studentID, File zipFile) {
                      this.studentID = studentID;
                      this.zipFile = zipFile;
                      this.result = new Result();
               }

               public boolean extract() {
                      if (zipFile == null || !zipFile.exists()) {
                             return false;
                      }

                      Thread extractionThread = new Thread(() -> {
                             try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
                                    String extractedFolderName = zipFile.getName().replace(".zip", "");
                                    extractedDirectory = new File(zipFile.getParent(), extractedFolderName);
                                    if (!extractedDirectory.exists()) {
                                           extractedDirectory.mkdirs();
                                    }

                                    ZipEntry entry;
                                    while ((entry = zipInputStream.getNextEntry()) != null) {
                                           File newFile = new File(extractedDirectory, entry.getName());
                                           if (entry.isDirectory()) {
                                                  newFile.mkdirs();
                                           } else {

                                                  new File(newFile.getParent()).mkdirs();
                                                  try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                                                         byte[] buffer = new byte[1024];
                                                         int length;
                                                         while ((length = zipInputStream.read(buffer)) > 0) {
                                                                fileOutputStream.write(buffer, 0, length);
                                                         }
                                                  }
                                           }
                                    }
                             } catch (IOException e) {
                                    e.printStackTrace();

                             }
                      });

                      extractionThread.start();
                      try {
                             extractionThread.join();
                      } catch (InterruptedException e) {
                             e.printStackTrace();
                             return false;
                      }

                      return true;
               }

               public Result getResult() {
                      return result;
               }
        }



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