package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IAEController extends Application {
   // public static List<Configuration> configurationList;
   public static ObservableList<Configuration> configurationList = FXCollections.observableArrayList();
    public static List<Project> projectList;
    public static Project currentProject;

    public static final String CONFIG_PATH = System.getProperty("user.home") + "/Documents/iae-app/configs.json";
    public static final String PROJECT_PATH = System.getProperty("user.home") + "/Documents/iae-app/projects.json";

    public static Stack<Scene> sceneStack = new Stack<>();

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IAEController.class.getResource("entrancePage.fxml"));

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
}
