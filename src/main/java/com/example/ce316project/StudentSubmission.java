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
                String extractedFolderName = zipFile.getName().replaceAll("(?i)\\.zip$", "");
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

            /*if (!configuration.isCompiled()) {
                System.out.println("This language does not require compilation.");
                result.setCompiledSuccessfully(true);
                return;
            }*/

            if (!configuration.isCompiled()) {
                // Eğer yorumlanan bir config seçildiyse ama kaynak dosyalar derleyiciye aitse → uyar
                List<String> compiledExtensions = List.of(".c", ".cpp", ".java", ".rs", ".go", ".kt", ".ts", ".pas", ".scala");

                boolean hasCompiledFile = sourceFiles.stream()
                        .anyMatch(file -> compiledExtensions.stream()
                                .anyMatch(ext -> file.getName().toLowerCase().endsWith(ext)));

                if (hasCompiledFile) {
                    result.setCompiledSuccessfully(false);
                    result.appendErrorLog("Config mismatch: Interpreted config selected, but compiled language files found.");
                    System.out.println("Compile blocked: mismatched configuration (interpreted config + compiled files)");
                    return;
                }

                // Gerçekten yorumlanan bir projeyse
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

    public void run(Configuration configuration, String arguments, String expectedOutputContent) {
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

            if (!configuration.isCompiled()) {
                String interpreter = configuration.getLanguagePath();
                List<String> paramList = new ArrayList<>();

                if (interpreter != null && !interpreter.isBlank()) {
                    paramList.add(interpreter);
                }

                if (configuration.getLanguageParameters() != null && !configuration.getLanguageParameters().isBlank()) {
                    String[] flags = configuration.getLanguageParameters().trim().split("\\s+");
                    paramList.addAll(Arrays.asList(flags));
                }

                if (runCommand != null && !runCommand.isBlank()) {
                    paramList.add(runCommand.trim());
                }

                if (arguments != null && !arguments.isBlank()) {
                    paramList.addAll(Arrays.asList(arguments.trim().split("\\s+")));
                }

                parts = paramList;
            }

            System.out.println("Final RUN command: " + String.join(" ", parts));

            ProcessBuilder pb = new ProcessBuilder(parts);
            pb.directory(extractedDirectory);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outputBuilder.append(line).append(System.lineSeparator());
            }

            int exitCode = process.waitFor();
            studentOutput = outputBuilder.toString();

            try (FileWriter writer = new FileWriter(outputFile, StandardCharsets.UTF_8)) {
                writer.write(studentOutput);
            }
            this.actualOutputFile = outputFile;

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

        String expected = expectedOutputContent != null ? expectedOutputContent.trim() : "";
        String actual = studentOutput != null ? studentOutput.trim() : "";

        boolean match = expected.equals(actual);
        result.setOutputMatches(match);
    }


    public boolean compareOutput(String expectedOutputContent) {
        if (this.result == null) {
            this.result = new Result();
        }
        this.result.clearErrorLog();

        if (expectedOutputContent == null || expectedOutputContent.trim().isEmpty()) {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("The expected output is empty or null.");
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
            this.result.appendErrorLog("The actual output file is invalid or unreadable.");
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
            this.result.appendErrorLog("actualOutputFile can not read: " + e.getMessage());
            return false;
        }


        String cleanedExpected = expectedOutputContent.trim().replace("\r\n", "\n").replaceAll("[ \t]+", "");
        String cleanedActual = actualContent.trim().replace("\r\n", "\n").replaceAll("[ \t]+", "");


        if (cleanedExpected.equals(cleanedActual)) {
            this.result.setOutputMatches(true);
            return true;
        } else {
            this.result.setOutputMatches(false);
            this.result.appendErrorLog("The outputs did not match");
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