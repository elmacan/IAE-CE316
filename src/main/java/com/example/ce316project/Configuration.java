package com.example.ce316project;

public class Configuration {
    private String languageName;    //c
    private String languagePath;    //gcc path
    private String languageParameters;    //-o main      parametre olan [-o]
    private String runCommand;   // ./main [arguments varsa]
    private boolean isCompiled;   //true


    public Configuration(String languagePath, String languageParameters, boolean isCompiled) {
        this.languagePath = languagePath;
        this.languageParameters = languageParameters;
        this.isCompiled = isCompiled;
    }

    public String generateCompileCommand(String sourceFileName) {   //gcc elma.c -o elma.exe
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

        // source file ekleniyor
        if (sourceFileName != null && !sourceFileName.isEmpty()) {
            command.append(" ").append(sourceFileName);
        } else {
            throw new IllegalArgumentException("Source file name cannot be empty!");
        }

        // Derleme parametreleri ekleniyor
        if (languageParameters != null && !languageParameters.isEmpty()) {
            command.append(" ").append(languageParameters);
        }

        return command.toString();

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
}
