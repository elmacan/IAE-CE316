package com.example.ce316project;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        this.result=new Result();
    }

    public boolean extract() {
        if (zipFile == null || !zipFile.exists()) {
            result.appendErrorLog("Error: Zip file is null or does not exist.");
            System.err.println("Error: Zip file is null or does not exist.");
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
                System.err.println("Error during extraction: " + e.getMessage());
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
        if (extractedDirectory == null || !extractedDirectory.exists()) {
            System.err.println("Error: Extraction failed, directory not created.");
            return false;
        }


        return true;
    }

    public void compile(Configuration configuration) {

        try {

            File[] sourceFiles = extractedDirectory.listFiles((dir, name) -> {
                for (String extension : Configuration.getSourceExtensions()) {
                    if (name.toLowerCase().endsWith(extension)) {
                        return true;
                    }
                }
                return false;
            });

            if (sourceFiles == null || sourceFiles.length == 0) {
                result.setCompiledSuccessfully(false);
                result.appendErrorLog("No source files found! Compilation is not possible");
                throw new IllegalStateException("No source files found! Compilation is not possible.");

            }

            List<String> sourceFileNames = new ArrayList<>();
            for (File sourceFile : sourceFiles) {
                sourceFileNames.add(sourceFile.getName());
            }

            String compileCommand = configuration.generateCompileCommand(sourceFileNames);
            String[] compileParts = compileCommand.split(" ");

            // ProcessBuilder ile derleme başlatıyoruz
            ProcessBuilder pb = new ProcessBuilder(compileParts);
            pb.directory(extractedDirectory); // Doğru klasörde çalıştırıyoruz
            pb.redirectErrorStream(true); // stdout ve stderr akışlarını birleştir
            Process process = pb.start(); // CMD açılır ve komut çalıştırılır

            // Çıktıyı okuyup ekrana bastırıyoruz
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor(); // Derleme bitene kadar bekliyoruz


            if (exitCode != 0) {
                result.setCompiledSuccessfully(false);
                result.appendErrorLog("Compilation failed");
                System.out.println("Compilation failed!");
                System.out.println("Uncompiled source files:");
                for (String fileName : sourceFileNames) {
                    System.out.println(" - " + fileName);
                }
            } else {
                result.setCompiledSuccessfully(true);
                System.out.println("Compilation successful!");
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println("hata");
            e.printStackTrace();

        }
    }



    public void run(Configuration configuration, String arguments) {
        File outputFile = new File(extractedDirectory, "student_output.txt");
        try {
            // 1) Kullanıcıdan gelen run komutunu böl
            String runCommand = configuration.generateRunCommand(arguments);
            List<String> parts = new ArrayList<>(Arrays.asList(runCommand.split(" ")));

            // 2) extractedDirectory içinden bulmamız gereken parçaları tespit et ve tam yola çevir
            File[] files = extractedDirectory.listFiles();
            if (files == null) files = new File[0];

            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
                // eğer nokta içeriyor ve yol ayracı içermiyorsa => dosya adı
                if (p.contains(".") && !p.contains(File.separator)) {
                    File matched = null;
                    for (File f : files) {
                        if (f.getName().equalsIgnoreCase(p)) {
                            matched = f;
                            break;
                        }
                    }
                    if (matched == null) {
                        throw new IllegalStateException(
                                "Cannot find file '" + p + "' in " + extractedDirectory.getAbsolutePath()
                        );
                    }
                    parts.set(i, matched.getAbsolutePath());
                }
            }

            // 3) ProcessBuilder ile çalıştır
            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.directory(extractedDirectory);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 4) Çıktıyı oku
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outputBuilder.append(line).append(System.lineSeparator());
            }
            int exitCode = process.waitFor();
            studentOutput = outputBuilder.toString();

            // 5) student_output.txt dosyasına yaz
            try (FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
                writer.write(studentOutput);
            }

            // 6) Sonucu güncelle
            if (exitCode != 0) {
                result.setRunSuccessfully(false);
                result.appendErrorLog("Execution failed with exit code " + exitCode);
                System.out.println("Execution failed (exit code " + exitCode + ")");
            } else {
                result.setRunSuccessfully(true);
                System.out.println("Execution successful!");
            }

        } catch (IllegalStateException e) {
            System.out.println("Run Error: " + e.getMessage());
            result.setRunSuccessfully(false);
            result.appendErrorLog(e.getMessage());

        } catch (Exception e) {
            System.out.println("An error occurred during execution:");
            e.printStackTrace();
            result.setRunSuccessfully(false);
            result.appendErrorLog(e.toString());
        }
    }

            public boolean compareOutput(File expectedOutput) {
                if (expectedOutput == null || actualOutputFile == null ||
                        !expectedOutput.exists() || !actualOutputFile.exists()) {
                    result.setOutputMatches(false);
                    result.appendErrorLog("One or both output files are missing.");
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
                            result.appendErrorLog("Output does not match expected output.");
                            return false;
                        }
                    }


                    if (expectedReader.readLine() != null || actualReader.readLine() != null) {   //eğer dosyaların boyutları birbirinden farklıysa,
                        result.setOutputMatches(false);                                           //yani birinin okuması daha önce biterse
                        result.appendErrorLog("Output lengths are different.");
                        return false;
                    }

                    result.setOutputMatches(true);
                    return true;

                } catch (IOException e) {
                    e.printStackTrace();
                    result.setOutputMatches(false);
                    result.appendErrorLog("Comparison failed: " + e.getMessage());
                    return false;
                }
            }



    public Result getResult() {
        return result;
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

          /* public File getOutputFile() {
                return actualOutputFile;
            }

            public void setOutputFile(File outputFile) {
                this.actualOutputFile = outputFile;
            }*/

}