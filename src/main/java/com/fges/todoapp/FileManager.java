package com.fges.todoapp;

public abstract class FileManager {
    protected String fileName;

    public FileManager(String fileName) {
        this.fileName = fileName;
    }

    public abstract void insert(String todo) throws Exception;
    public abstract void list() throws Exception;
}
