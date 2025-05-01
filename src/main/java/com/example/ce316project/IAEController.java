package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
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
import java.lang.reflect.Type;
import java.util.List;

public class IAEController extends Application {
    private List<Configuration> configurationList;
    private List<Project> projectList;
    private Project currentProject;

    //@Override
    /*public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IAEController.class.getResource("entrancePage.fxml"));
       *//* configurationList = FileManager.loadConfigurations(new File("C:\\Users\\msi\\IdeaProjects\\IAE-CE316\\configs.json"));
        projectList=FileManager.loadProjects(new File("C:\\Users\\msi\\IdeaProjects\\IAE-CE316\\projects.json"));
        System.out.println("Configuration List: " + configurationList);
        System.out.println("Project List: " + projectList);
        System.out.println("\n");
        //System.out.println(projectList.get(0).getProjectName());
        System.out.println(projectList.get(0).getProjectName());*//*
        File configFile = new File("configs.json");
        File projectFile = new File("projects.json");

        configurationList = FileManager.loadConfigurations(configFile);
        projectList = FileManager.loadProjects(projectFile);



        Scene scene = new Scene(fxmlLoader.load(), 600, 400);




        stage.setTitle("Integrated Application Environment");
        stage.setScene(scene);

        StudentSubmission s = new StudentSubmission();
        FileChooser fileChooser = new FileChooser();
        File selectedzip = fileChooser.showOpenDialog(stage);

        if (selectedzip != null) {
            s.setZipFile(selectedzip);
            if (s.extract()) { // Ensure extraction is successful
                Configuration configuration = new Configuration("c", "gcc", "-o elma.exe", "elma.exe", true);
                Configuration conf = new Configuration("java","javac","","java CE", true);
                s.compile(conf); // Compile only if extraction succeeded
                s.run(conf, ""); // Run only if compilation succeeded
            } else {
                System.out.println("Extraction failed. Cannot proceed.");
            }
        } else {
            System.out.println("No zip file selected.");
        }


       // Configuration configuration=new Configuration("c","gcc","-o elma.exe","elma.exe",true);
       // Configuration config2=new Configuration("java","javac","","java Armut",true);




    }*/

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(IAEController.class.getResource("entrancePage.fxml"));


        Gson gson = new Gson();
        Type listType = new TypeToken<List<Configuration>>(){}.getType();

        try {
            File configFile = new File(getClass().getResource("/com/example/ce316project/configs.json").toURI());
            configurationList = FileManager.loadConfigurations(configFile);
            System.out.println("Loaded " + configurationList.size() + " configurations.");
        } catch (Exception e) {
            e.printStackTrace();
        }



        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Integrated Application Environment");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {

        launch();

        //parse deneme yanılma
        try {
            FileReader reader = new FileReader("src/main/resources/com/example/ce316project/configs.json");
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Configuration>>(){}.getType();
            List<Configuration> configurations = gson.fromJson(reader, listType);
            for (Configuration config : configurations) {
                System.out.println(config);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}