package com.fges.todoapp;

import java.util.List;

public interface CommandHandler {
    void execute(List<String> positionalArgs) throws Exception;
}
