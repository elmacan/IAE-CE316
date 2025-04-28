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

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(IAEController.class.getResource("entrancePage.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);




        stage.setTitle("Integrated Application Environment");
        stage.setScene(scene);

        StudentSubmission s=new StudentSubmission(); //deneme
        //s.findSourceFile();
        Configuration configuration=new Configuration("gcc","-o elma.exe",true);
        Configuration config2=new Configuration("javac","",true);
        //configuration.setLanguagePath("C:\\Program Files (x86)\\winlibs-x86_64-posix-seh-gcc-15.1.0-mingw-w64ucrt-12.0.0-r1\\mingw64\\bin\\gcc.exe");
       // s.compile(configuration);
        //tam path girince ya da gcc,javac girince de compile yapıyor
        s.compile(config2);
        //stage.show();



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