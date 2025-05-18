package com.example.ce316project;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private String languageName;    //c
    private String languagePath;    //gcc path
    private String languageParameters;    //-o main      parametre olan [-o]
    private String runCommand;   // ./main [arguments varsa]
    private boolean isCompiled;   //true
    private String fileExtansion;
    public static List<String> sourceExtensions = new ArrayList<>(List.of(
            ".c",     // C
            ".cpp",   // C++
            ".java",  // Java
            ".py",    // Python
            ".js",    // JavaScript
            ".cs",    // C#
            ".go",    // Go
            ".rb",    // Ruby
            ".php",   // PHP
            ".ts",    // TypeScript
            ".kt",    // Kotlin
            ".swift", // Swift
            ".rs",    // Rust
            ".r",     // R
            ".sh",    // Shell/Bash
            ".pl",    // Perl
            ".m",     // MATLAB
            ".sql"    // SQL
    ));



    public Configuration() {}


    public Configuration(String languageName, String languagePath, String languageParameters, String runCommand, boolean isCompiled) {
        this.languageName = languageName;
        this.languagePath = languagePath;
        this.languageParameters = languageParameters;
        this.runCommand = runCommand;
        this.isCompiled = isCompiled;

    }


    public String generateCompileCommand(List<String> sourceFileNames) {   //gcc elma.c -o elma.exe
        if (!isCompiled) return "";

        StringBuilder command = new StringBuilder();

        if (languagePath != null && !languagePath.isEmpty()) {
            // Eğer path içinde boşluk varsa, güvenlik için tırnak içine alıyoruz     gcc değil de / C:\Program Files\GCC\gcc.exe olursa
            if (languagePath.contains(" ")) {
                command.append("\"").append(languagePath).append("\"");
            } else {
                command.append(languagePath);
            }
        } else {
            throw new IllegalStateException("languagePath not defined!");
        }

        // Derleme parametreleri ekleniyor (önce ekleniyor bazı diller için kritik)
        if (languageParameters != null && !languageParameters.isEmpty()) {
            command.append(" ").append(languageParameters);
        }

        // source file ekleniyor
        if (sourceFileNames != null && !sourceFileNames.isEmpty()) {
            for (String sourceFileName : sourceFileNames) {
                if (sourceFileName != null && !sourceFileName.isEmpty()) {
                    command.append(" ").append(sourceFileName);
                } else {
                    throw new IllegalArgumentException("Source file name cannot be empty!");
                }
            }
        } else {
            throw new IllegalArgumentException("Source file list cannot be empty!");
        }



        return command.toString();

    }



    public String generateRunCommand(String arguments) {
        if (runCommand == null || runCommand.isEmpty()) {
            throw new IllegalStateException("Run command is not defined!");
        }

        StringBuilder rCommand = new StringBuilder();

       rCommand.append(runCommand.trim());

        if (arguments != null && !arguments.isBlank()) {   // girilen inputları da ekliyoruz, mesela sayı, string gibi.
            rCommand.append(" ").append(arguments);
        }

        return rCommand.toString();
    }


    public static List<String> getSourceExtensions() {
        return sourceExtensions;
    }

    public static void setSourceExtensions(List<String> sourceExtensions) {
        Configuration.sourceExtensions = sourceExtensions;
    }

    public boolean isCompiled() {
        return isCompiled;
    }

    public void setCompiled(boolean compiled) {
        isCompiled = compiled;
    }

    public String getRunCommand() {
        return runCommand;
    }

    public void setRunCommand(String runCommand) {
        this.runCommand = runCommand;
    }

    public String getLanguageParameters() {
        return languageParameters;
    }

    public void setLanguageParameters(String languageParameters) {
        this.languageParameters = languageParameters;
    }

    public String getLanguagePath() {
        return languagePath;
    }

    public void setLanguagePath(String languagePath) {
        this.languagePath = languagePath;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    @Override

    public String toString() {
        return "Configuration{" +
                "languageName='" + languageName + '\'' +
                ", language path='" + languagePath + '\'' +
                ", language parameter ='" +languageParameters + '\'' +
                ", runCommand='" + runCommand + '\'' +
                '}';
    }

}
