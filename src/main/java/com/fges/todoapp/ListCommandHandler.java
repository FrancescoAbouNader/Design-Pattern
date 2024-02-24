package com.fges.todoapp;

import java.util.List;

public class ListCommandHandler implements CommandHandler {

    private final String fileName;
    private Object onlyDone;

    public ListCommandHandler(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void execute(List<String> positionalArgs) throws Exception {
        FileManager fileManager = FileManagerFactory.getFileManager(fileName);
        fileManager.list(onlyDone);
    }
}
