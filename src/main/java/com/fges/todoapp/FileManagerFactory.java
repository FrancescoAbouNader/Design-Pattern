package com.fges.todoapp;

public class FileManagerFactory {

    public static FileManager getFileManager(String fileName) {
        if (fileName.endsWith(".json")) {
            return new TodoJsonManager(fileName);
        } else if (fileName.endsWith(".csv")) {
            return new TodoCsvManager(fileName);
        } else {
            throw new IllegalArgumentException("Unsupported file format");
        }
    }
}
