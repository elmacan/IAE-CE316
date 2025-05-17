/*package com.example.ce316project;

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


        stage.setTitle("Integrated Application Environment");
        stage.setScene(scene);
        stage.show();

        // Configuration configuration=new Configuration("c","gcc","-o elma.exe","elma.exe",true);
        // Configuration config2=new Configuration("java","javac","","java Armut",true);






    }

    public static void main(String[] args) {

        launch();


    }
}*/


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
    public static ObservableList<Configuration> configurationList = FXCollections.observableArrayList();
    public static List<Project> projectList;
    public static Project currentProject;

    static String userHome = System.getProperty("user.home");
    public static final String CONFIG_PATH = Paths.get(userHome, ".iae-app", "configs.json").toString();
    public static final String PROJECT_PATH = Paths.get(userHome, ".iae-app", "projects.json").toString();

    // --- SCENE STACK ---
    public static Stack<Scene> sceneStack = new Stack<>();

    public static void pushScene(Scene scene) {
        if (scene != null) {
            // Debugging: Get root ID if available
            String rootId = (scene.getRoot() != null && scene.getRoot().getId() != null) ? scene.getRoot().getId() : "N/A";
            System.out.println("Pushing scene. Root ID: " + rootId + ". Current stack size: " + sceneStack.size());
            sceneStack.push(scene);
            System.out.println("Scene pushed. New stack size: " + sceneStack.size());
        } else {
            System.err.println("Attempted to push a null scene to the stack.");
        }
    }

    public static Scene popScene() {
        if (!sceneStack.isEmpty()) {
            System.out.println("Popping scene. Current stack size: " + sceneStack.size());
            Scene poppedScene = sceneStack.pop();
            String rootId = (poppedScene != null && poppedScene.getRoot() != null && poppedScene.getRoot().getId() != null) ? poppedScene.getRoot().getId() : "N/A";
            System.out.println("Scene popped. Root ID: " + rootId + ". New stack size: " + sceneStack.size());
            return poppedScene;
        }
        System.out.println("Attempted to pop from an empty stack.");
        return null;
    }

    public static void clearStack() {
        System.out.println("Clearing scene stack. Current size: " + sceneStack.size());
        sceneStack.clear();
        System.out.println("Scene stack cleared. New size: " + sceneStack.size());
    }

    public static boolean isStackEmpty() {
        return sceneStack.isEmpty();
    }

    public static Scene peekScene() {
        if (!sceneStack.isEmpty()) {
            return sceneStack.peek();
        }
        return null;
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IAEManager.class.getResource("entrancePage.fxml"));

        // Dosya nesnelerini oluştur
        File configFile = new File(CONFIG_PATH);
        File projectFile = new File(PROJECT_PATH);

        // Klasörü oluştur (Documents/iae-app)
        File parentDir = configFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) { // Null check for parentDir
            parentDir.mkdirs();
        }

        // Eğer dosyalar yoksa, resources içinden kopyala
        if (!configFile.exists()) {
            try (InputStream defaultConfig = getClass().getResourceAsStream("/configs.json")) {
                if (defaultConfig == null) {
                    System.err.println("Default configs.json not found in resources!");
                } else {
                    Files.copy(defaultConfig, configFile.toPath());
                }
            }
        }

        if (!projectFile.exists()) {
            try (InputStream defaultProjects = getClass().getResourceAsStream("/projects.json")) {
                if (defaultProjects == null) {
                    System.err.println("Default projects.json not found in resources!");
                } else {
                    Files.copy(defaultProjects, projectFile.toPath());
                }
            }
        }

        // Dosyalardan yükleme
        configurationList = FXCollections.observableArrayList(FileManager.loadConfigurations(configFile));
        projectList = FileManager.loadProjects(projectFile);
        if (projectList == null) { // Removed isEmpty check as loadProjects might return null
            System.out.println("Project list is null. Initializing with an empty list.");
            projectList = new ArrayList<>();
        }


        System.out.println("Configuration List: " + configurationList);
        System.out.println("Project List: " + projectList);
        System.out.println("\n");

        if (projectList != null && !projectList.isEmpty()) { // Null check for projectList
            System.out.println("First Project Name: " + projectList.get(0).getProjectName());
        } else {
            System.out.println("No projects available in the project list.");
        }
        // System.out.println("Configuration List (again): " + configurationList); // Redundant

        System.out.println("Loaded Projects:");
        if (projectList != null) { // Null check for projectList
            for (Project p : projectList) {
                System.out.println(p);
            }
        }

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        // Uygulama başladığında yığın boş olmalı.
        clearStack(); // Make sure stack is clear on application start

        stage.setTitle("Integrated Application Environment");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // Pass args to launch
    }
}
