package com.example.ce316project;
import java.io.*;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.File;

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
        }

