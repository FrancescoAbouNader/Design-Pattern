package com.fges.todoapp;

import java.util.List;

public class InsertCommandHandler implements CommandHandler {

    private final String fileName;
    private boolean isDone;

    public InsertCommandHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void execute(List<String> positionalArgs) throws Exception {
        FileManager fileManager = FileManagerFactory.getFileManager(fileName);
        if (positionalArgs.size() < 2) {
            System.err.println("Missing TODO name");
            return;
        }
        String todo = positionalArgs.get(1);
        fileManager.insert(todo, isDone);
    }
}
