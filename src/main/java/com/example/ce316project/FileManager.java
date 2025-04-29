package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final Gson gson = new Gson();


    public static void saveConfigurations(List<Configuration> newConfigs, File file) {
        List<Configuration> existingConfigs = loadConfigurations(file);
        if (existingConfigs == null) {
            existingConfigs = new ArrayList<>(); // null'sa boş liste ile başlat
        }
        existingConfigs.addAll(newConfigs);

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(existingConfigs, writer);
        } catch (IOException e) {
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
    public static List<Configuration> importConfigurations(File file) {
        List<Configuration> importedConfigs = new ArrayList<>();
        try (FileReader reader = new FileReader(file)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);

            if (jsonElement.isJsonArray()) {
                importedConfigs = gson.fromJson(jsonElement, new TypeToken<List<Configuration>>() {}.getType());
            } else if (jsonElement.isJsonObject()) {
                Configuration single = gson.fromJson(jsonElement, Configuration.class);
                importedConfigs.add(single);
            } else {
                System.out.println("Unsupported JSON format.");
            }
        } catch (IOException e) {
            System.out.println("Error importing configurations: " + e.getMessage());
            e.printStackTrace();
        }
        return importedConfigs;
    }

    public static void exportConfiguration(Configuration config, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            System.out.println("Error exporting configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    //burdaki metodların hepsi static olcak
}
