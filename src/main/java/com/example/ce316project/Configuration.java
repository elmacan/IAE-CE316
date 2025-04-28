package com.example.ce316project;

public class Configuration {
    private String languageName;    //c
    private String languagePath;    //gcc path
    private String languageParameters;    //-o main.c       parametre olan [-o]
    private String runCommand;   // ./main.c [arguments varsa]
    private boolean isCompiled;   //true

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
