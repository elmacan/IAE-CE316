package com.example.ce316project;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    public File getActualOutputFile() {
        return actualOutputFile;
    }

    private File actualOutputFile;


        private String output; // Bu değişken varsa

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }
        
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

                    String canonicalDestination = newFile.getCanonicalPath();
                    String canonicalExtractionRoot = extractedDirectory.getCanonicalPath();
                    if (!canonicalDestination.startsWith(canonicalExtractionRoot)) {
                        System.err.println("Error: Attempted to extract outside of target directory.");
                        result.appendErrorLog("Zip Slip attempt blocked: " + newFile.getAbsolutePath());
                        return;
                    }

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
                result.appendErrorLog("Extraction failed: " + e.getMessage());
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


        File[] topLevel = extractedDirectory.listFiles();
        if (topLevel != null && topLevel.length == 1 && topLevel[0].isDirectory()) {
            extractedDirectory = topLevel[0];
        }

        if (extractedDirectory == null || !extractedDirectory.exists()) {
            System.err.println("Error: Extraction failed, directory not created.");
            return false;
        }

        return true;
    }


   public void compile(Configuration configuration) {
        try {
            List<File> sourceFiles = listAllSourceFiles(extractedDirectory);

            if (sourceFiles.isEmpty()) {
                result.setCompiledSuccessfully(false);
                result.appendErrorLog("No source files found! Compilation is not possible.");
                throw new IllegalStateException("No source files found! Compilation is not possible.");
            }

            if (!configuration.isCompiled()) {
                System.out.println("This language does not require compilation.");
                result.setCompiledSuccessfully(true);
                return;
            }

            List<String> sourceFileNames = new ArrayList<>();
            for (File sourceFile : sourceFiles) {
                sourceFileNames.add(sourceFile.getName());
            }

            String compileCommand = configuration.generateCompileCommand(sourceFileNames);
            String[] compileParts = compileCommand.split(" ");

            ProcessBuilder pb = new ProcessBuilder(compileParts);
            pb.directory(extractedDirectory);
            pb.redirectErrorStream(true);
            Process process = pb.start();


            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                result.setCompiledSuccessfully(false);
                result.appendErrorLog("Compilation failed");
                System.out.println("Compilation failed!");
            } else {
                result.setCompiledSuccessfully(true);
                System.out.println("Compilation successful!");
            }

        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error during compilation:");
            e.printStackTrace();
            result.setCompiledSuccessfully(false);
            result.appendErrorLog("Unexpected error: " + e.toString());
        }
    }

    public List<File> listAllSourceFiles(File dir) {
        List<File> sourceFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    sourceFiles.addAll(listAllSourceFiles(file));
                } else {
                    for (String ext : Configuration.sourceExtensions) {
                        if (file.getName().toLowerCase().endsWith(ext)) {
                            sourceFiles.add(file);
                            break;
                        }
                    }
                }
            }
        }
        return sourceFiles;
    }

    public void run(Configuration configuration, String arguments) {
        File outputFile = new File(extractedDirectory, "student_output.txt");
        try {

            String runCommand = configuration.generateRunCommand(arguments);
            List<String> parts = new ArrayList<>(Arrays.asList(runCommand.split(" ")));

            // 2) extractedDirectory içinden bulmamız gereken parçaları tespit et ve tam yola çevir
            File[] files = extractedDirectory.listFiles();
            if (files == null) files = new File[0];

            for (int i = 0; i < parts.size(); i++) {
                String p = parts.get(i);
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

            if(!configuration.isCompiled()) {
                String interpreter = configuration.getLanguagePath();
                if (interpreter != null && !interpreter.isBlank()) {
                    parts.add(0, interpreter);
                }
            }

            System.out.println("Final RUN command: " + String.join(" ", parts));

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

           /* public boolean compareOutput(File expectedOutput) {
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
            }*/




    /*public boolean compareOutput(String expectedOutputContent) {
        if (this.result == null) {
            this.result = new Result();
        }
        this.result.clearErrorLog();

        // 1. Girdi Kontrolleri
        if (expectedOutputContent == null || expectedOutputContent.trim().isEmpty()) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Beklenen çıktı boş ya da null.");
            return false;
        }

        if (actualOutputFile == null) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("actualOutputFile null.");
            return false;
        }

        Path actualPath = actualOutputFile.toPath();
        if (!Files.exists(actualPath) || !Files.isRegularFile(actualPath) || !Files.isReadable(actualPath)) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Gerçek çıktı dosyası geçersiz veya okunamaz.");
            return false;
        }

        // 2. Gerçekleşen Çıktıyı Oku
        String actualContent;
        try {
            actualContent = Files.readString(actualPath, StandardCharsets.UTF_8);
            if (actualContent.startsWith("\uFEFF")) {
                actualContent = actualContent.substring(1);
            }
        } catch (IOException e) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("actualOutputFile okunamadı: " + e.getMessage());
            return false;
        }

        // 3. Normalleştirme (Whitespace ve BOM temizliği)
        String cleanedExpected = expectedOutputContent.replace("\uFEFF", "").replaceAll("\\s+", "");
        String cleanedActual = actualContent.replaceAll("\\s+", "");

        // 4. Karşılaştır
        if (cleanedExpected.equals(cleanedActual)) {
            this.result.setOutputMatches(true);
            return true;
        } else {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Çıktılar eşleşmiyor.\n--- Beklenen ---\n" + expectedOutputContent +
                    "\n--- Gerçek ---\n" + actualContent);
            return false;
        }
    }*/

    public boolean compareOutput(String expectedOutputContent) {
        if (this.result == null) {
            this.result = new Result();
        }
        this.result.clearErrorLog();

        if (expectedOutputContent == null || expectedOutputContent.trim().isEmpty()) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Beklenen çıktı boş ya da null.");
            return false;
        }

        if (actualOutputFile == null) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("actualOutputFile null.");
            return false;
        }

        Path actualPath = actualOutputFile.toPath();
        if (!Files.exists(actualPath) || !Files.isRegularFile(actualPath) || !Files.isReadable(actualPath)) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Gerçek çıktı dosyası geçersiz veya okunamaz.");
            return false;
        }

        String actualContent;
        try {
            actualContent = Files.readString(actualPath, StandardCharsets.UTF_8);

            if (actualContent.startsWith("\uFEFF")) {
                actualContent = actualContent.substring(1);
            }
        } catch (IOException e) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("actualOutputFile okunamadı: " + e.getMessage());
            return false;
        }


        String cleanedExpected = expectedOutputContent.trim().replace("\r\n", "\n").replaceAll("[ \t]+", "");
        String cleanedActual = actualContent.trim().replace("\r\n", "\n").replaceAll("[ \t]+", "");


        if (cleanedExpected.equals(cleanedActual)) {
            this.result.setOutputMatches(true);
            return true;
        } else {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("Çıktılar eşleşmiyor.\n--- Beklenen ---\n" + expectedOutputContent +
                    "\n--- Gerçek ---\n" + actualContent);
            return false;
        }
    }




    public void setActualOutputFile(File actualOutputFile) { // Örnek setter
        this.actualOutputFile = actualOutputFile;
    }
    // ...




    static class ComparisonResult {
        private boolean outputMatches;
        private StringBuilder errorLog = new StringBuilder();

        public void setOutputMatches(boolean outputMatches) {
            this.outputMatches = outputMatches;
        }
        public boolean isOutputMatches() {
            return outputMatches;
        }
        public void appendErrorLog(String message) {
            errorLog.append(message).append("\n");
        }
        public String getErrorLog() {
            return errorLog.toString();
        }
        public void clearErrorLog() {
            errorLog.setLength(0);
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