package com.fges.todoapp;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.stream.Stream;

public class TodoCsvManager extends FileManager {

    public TodoCsvManager(String fileName) {
        super(fileName);
    }

    @Override
    public void insert(String todo) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (Files.exists(Paths.get(fileName))) {
            String existingContent = Files.readString(Paths.get(fileName));
            if (!existingContent.isEmpty()) {
                sb.append(existingContent).append("\n");
            }
        }
        sb.append(todo);
        Files.writeString(Paths.get(fileName), sb.toString());
    }

    @Override
    public void list() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            System.err.println("File does not exist.");
            return;
        }
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> System.out.println("- " + line));
        }
    }
}
