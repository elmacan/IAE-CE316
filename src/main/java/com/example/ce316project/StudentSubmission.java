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
               private File actualOutputFile;



            public StudentSubmission(String studentID, File zipFile) {
                      this.studentID = studentID;
                      this.zipFile = zipFile;
                      this.result = new Result();
               }
               public StudentSubmission(){

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



              public File findSourceFile(){
                      //birden fazla varsa main olana bakıyor diğer türlü normal alıyor
                      if (extractedDirectory == null || !extractedDirectory.exists()) {
                     throw new IllegalStateException("Extracted directory is not set or does not exist.");
              }

              String[] sourceExtensions = {".c", ".java", ".cpp", ".py"};
              File mainFileCandidate = null; // Adı 'main' olan dosyayı aramak için

              File[] files = extractedDirectory.listFiles();
              if (files != null) {
                     for (File file : files) {
                            if (file.isFile()) {
                                   for (String extension : sourceExtensions) {
                                          if (file.getName().toLowerCase().endsWith(extension)) {
                                                 String baseName = file.getName().substring(0, file.getName().lastIndexOf('.'));

                                                 if (baseName.equalsIgnoreCase("main")) {
                                                        // Eğer dosya adı 'main' ise doğrudan bunu seç
                                                        System.out.println("Main source file found: " + file.getAbsolutePath());
                                                        return file;
                                                 } else if (mainFileCandidate == null) {
                                                        // Main bulunmadıysa, bulduğumuz ilk uygun dosyayı yedek olarak tut
                                                        mainFileCandidate = file;
                                                 }
                                          }
                                   }
                            }
                     }
              }

              if (mainFileCandidate != null) {
                     System.out.println("Source file (not named main) found: " + mainFileCandidate.getAbsolutePath());
                     return mainFileCandidate;
              }

              throw new IllegalStateException("No source file found in the extracted directory.");

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


       public boolean run(Configuration config, File inputFile, File outputFile) {
                if (config == null || extractedDirectory == null || !extractedDirectory.exists()) {
                    result.setRunSuccessfully(false);
                    result.setErrorLog("Invalid configuration or extraction directory.");
                    return false;
                }

                try {
                    String arguments = "";
                    if (inputFile != null && inputFile.exists()) {
                        arguments = Files.readString(inputFile.toPath()).trim();  //input file'ı okuyor
                    }

                    String runCommand = config.generateRunCommand(arguments);    //komut satırını oluşturuyoruz

                    ProcessBuilder processBuilder = new ProcessBuilder(runCommand.split(" "));
                    processBuilder.directory(extractedDirectory);

                    this.actualOutputFile = outputFile;
                    if (actualOutputFile != null) {
                        processBuilder.redirectOutput(actualOutputFile);   //output u, output file a yönlendirdik
                    }

                    Process process = processBuilder.start();

                    int exitCode = process.waitFor();

                    if (exitCode == 0) {
                        result.setRunSuccessfully(true);
                        return true;
                    } else {
                        result.setRunSuccessfully(false);
                        result.setErrorLog("Program exited with code " + exitCode);
                        return false;
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    result.setRunSuccessfully(false);
                    result.setErrorLog("Exception: " + e.getMessage());
                    return false;
                }
            }


            public boolean compareOutput(File expectedOutput) {
                if (expectedOutput == null || actualOutputFile == null ||
                        !expectedOutput.exists() || !actualOutputFile.exists()) {
                    result.setOutputMatches(false);
                    result.setErrorLog("One or both output files are missing.");
                    return false;
                }

                try (
                        BufferedReader expectedReader = new BufferedReader(new FileReader(expectedOutput));
                        BufferedReader actualReader = new BufferedReader(new FileReader(actualOutputFile))
                ) {
                    String expectedLine;
                    String actualLine;

                    while ((expectedLine = expectedReader.readLine()) != null && (actualLine = actualReader.readLine()) != null) {

                        if (!expectedLine.trim().equals(actualLine.trim())) {
                            result.setOutputMatches(false);
                            result.setErrorLog("Output does not match expected output.");
                            return false;
                        }
                    }


                    if (expectedReader.readLine() != null || actualReader.readLine() != null) {   //eğer dosyaların boyutları birbirinden farklıysa,
                        result.setOutputMatches(false);                                           //yani birinin okuması daha önce biterse
                        result.setErrorLog("Output lengths are different.");
                        return false;
                    }

                    result.setOutputMatches(true);
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    result.setOutputMatches(false);
                    result.setErrorLog("Comparison failed: " + e.getMessage());
                    return false;
                }
            }





            public String getStudentID() {
                      return studentID;
               }

               public void setStudentID(String studentID) {
                      this.studentID = studentID;
               }

               public File getZipFile() {
                      return zipFile;
               }

               public void setZipFile(File zipFile) {
                      this.zipFile = zipFile;
               }

               public File getExtractedDirectory() {
                      return extractedDirectory;
               }

               public void setExtractedDirectory(File extractedDirectory) {
                      this.extractedDirectory = extractedDirectory;
               }

               public void setResult(Result result) {
                      this.result = result;
               }

               public String getStudentOutput() {
                      return studentOutput;
               }

               public void setStudentOutput(String studentOutput) {
                      this.studentOutput = studentOutput;
               }

            public File getOutputFile() {
                return actualOutputFile;
            }

            public void setOutputFile(File outputFile) {
                this.actualOutputFile = outputFile;
            }
        }