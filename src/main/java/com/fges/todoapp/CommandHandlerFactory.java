package com.fges.todoapp;

public class CommandHandlerFactory {

    public static CommandHandler getHandler(String command, String fileName) {
        switch (command) {
            case "insert":
                return new InsertCommandHandler(fileName);
            case "list":
                return new ListCommandHandler(fileName);
            default:
                return null;
        }
    }
}
