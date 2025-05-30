package com.example.ce316project;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IAEManager extends Application {
   // public static List<Configuration> configurationList;
   public static ObservableList<Configuration> configurationList = FXCollections.observableArrayList();
    public static List<Project> projectList;
    public static Project currentProject;

    static String userHome = System.getProperty("user.home");
    public static final String CONFIG_PATH = Paths.get(userHome, ".iae-app", "configs.json").toString();
    public static final String PROJECT_PATH = Paths.get(userHome, ".iae-app", "projects.json").toString();

    public static Stack<Scene> sceneStack = new Stack<>();

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IAEManager.class.getResource("entrancePage.fxml"));

        // Dosya nesnelerini oluştur
        File configFile = new File(CONFIG_PATH);
        File projectFile = new File(PROJECT_PATH);

        // Klasörü oluştur (Documents/iae-app)
        File parentDir = configFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Eğer dosyalar yoksa, resources içinden kopyala
        if (!configFile.exists()) {
            try (InputStream defaultConfig = getClass().getResourceAsStream("/configs.json")) {
                Files.copy(defaultConfig, configFile.toPath());
            }
        }

        if (!projectFile.exists()) {
            try (InputStream defaultProjects = getClass().getResourceAsStream("/projects.json")) {
                Files.copy(defaultProjects, projectFile.toPath());
            }
        }

        // Dosyalardan yükleme
      //  configurationList = FileManager.loadConfigurations(configFile);
        configurationList = FXCollections.observableArrayList(FileManager.loadConfigurations(configFile));
        projectList = FileManager.loadProjects(projectFile);
        if (projectList == null || projectList.isEmpty()) {
            System.out.println("Project list is null or empty. Initializing with an empty list.");
            projectList = new ArrayList<>();
        }


        System.out.println("Configuration List: " + configurationList);
        System.out.println("Project List: " + projectList);
        System.out.println("\n");

        if (!projectList.isEmpty()) {
            System.out.println(projectList.get(0).getProjectName());
        } else {
            System.out.println("No projects available in the project list.");
        }
        System.out.println("Configuration List: " + configurationList);

        System.out.println("Loaded Projects:");
        for (Project p : projectList) {
            System.out.println(p);
        }


        Scene scene = new Scene(fxmlLoader.load(), 600, 400);


        stage.setTitle("The Integrated Assignment Environment");
        stage.setScene(scene);
        stage.show();




    }

    public static void main(String[] args) {

        launch();


    }
}
