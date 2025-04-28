package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final Gson gson = new Gson();

    public static void saveConfigurations(List<Configuration> configs, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(configs, writer);
        } catch (IOException e) {
            System.out.println("Error saving configurations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Configuration> loadConfigurations(File file) {
        if (!file.exists()) {
            System.out.println("configs.json bulunamadı, boş liste oluşturuluyor...");
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, new TypeToken<List<Configuration>>() {}.getType());
        } catch (IOException e) {
            System.out.println("Error loading configurations: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    //burdaki metodların hepsi static olcak
}
