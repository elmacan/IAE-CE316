package com.example.ce316project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final Gson gson = new Gson();
    private static final Gson gson2 = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileAdapter())
            .setPrettyPrinting()
            .create();


    // YENİ: InputStream üzerinden configurations yükle
    public static List<Configuration> loadConfigurations(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return gson2.fromJson(reader, new TypeToken<List<Configuration>>() {}.getType());
        } catch (IOException e) {
            System.out.println("Error loading configurations from stream: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // YENİ: InputStream üzerinden projects yükle
    public static List<Project> loadProjects(InputStream inputStream) {
        if (inputStream == null) {
            System.out.println("Error: Input stream for projects is null.");
            return new ArrayList<>(); // Return an empty list if the stream is null
        }
        try (InputStreamReader reader = new InputStreamReader(inputStream)) {
            return gson2.fromJson(reader, new TypeToken<List<Project>>() {}.getType());
        } catch (IOException e) {
            System.out.println("Error loading projects from stream: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


    public static void saveConfigurations(List<Configuration> newConfigs, File file) {
        try (Writer writer = new FileWriter(file)) {
            gson2.toJson(newConfigs, writer); // ✅ SADECE yeni listeyi yaz
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
            return gson2.fromJson(reader, new TypeToken<List<Configuration>>() {}.getType());
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
                importedConfigs = gson2.fromJson(jsonElement, new TypeToken<List<Configuration>>() {}.getType());
            } else if (jsonElement.isJsonObject()) {
                Configuration single = gson2.fromJson(jsonElement, Configuration.class);
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
            gson2.toJson(config, writer);
        } catch (IOException e) {
            System.out.println("Error exporting configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static List<Project> loadProjects(File file) {
        if (!file.exists()) {
            System.out.println("projects.json bulunamadı, boş liste oluşturuluyor...");
            return new ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            return gson2.fromJson(reader, new TypeToken<List<Project>>() {}.getType());
        } catch (IOException e) {
            System.out.println("Error loading projects: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static List<Project> saveProjectIfUnique(Project newProject, File file) {
        List<Project> existingProjects = loadProjects(file);
        if (existingProjects == null) {
            existingProjects = new ArrayList<>();
        }

        boolean nameExists = existingProjects.stream()
                .anyMatch(p -> p.getProjectName().equalsIgnoreCase(newProject.getProjectName()));

        if (nameExists) {
            return null; // Aynı isim varsa ekleme
        }

        existingProjects.add(newProject);

        try (Writer writer = new FileWriter(file)) {
            gson2.toJson(existingProjects, writer); // ✅ Pretty-print JSON
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return existingProjects; // Güncellenmiş listeyi döndür
    }



    //burdaki metodların hepsi static olcak
}
