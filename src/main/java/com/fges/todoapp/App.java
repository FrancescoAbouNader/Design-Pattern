package com.fges.todoapp;

import java.nio.file.*;

public class App {
    public static void main(String[] args) throws Exception {
        System.exit(new TodoApp().exec(args));
    }
}
