package com.example.ce316project;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;

public class FileAdapter extends TypeAdapter<File> {

    @Override
    public void write(JsonWriter out, File file) throws IOException {
        if (file == null) {
            out.nullValue();
        } else {
            out.value(file.getPath());
        }
    }

    @Override
    public File read(JsonReader in) throws IOException {
        String path = in.nextString();
        return new File(path);
    }
}
