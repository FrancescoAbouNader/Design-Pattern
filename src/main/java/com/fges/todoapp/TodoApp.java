package com.fges.todoapp;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TodoApp {
    public int exec(String[] args) throws IOException {
        Options cliOptions = createCliOptions();
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd;
        try {
            cmd = parser.parse(cliOptions, args);
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return 1;
        }

        String fileName = cmd.getOptionValue("s");

        List<String> positionalArgs = cmd.getArgList();
        if (positionalArgs.isEmpty()) {
            System.err.println("Missing Command");
            return 1;
        }

        String command = positionalArgs.get(0);

        Path filePath = Paths.get(fileName);

        String fileContent = getFileContent(filePath);

        TodoProcessor todoProcessor = new TodoProcessor();
        return todoProcessor.process(command, fileName, positionalArgs, fileContent);
    }

    private static Options createCliOptions() {
        Options cliOptions = new Options();
        cliOptions.addRequiredOption("s", "source", true, "File containing the todos");
        cliOptions.addOption("d", "done", false, "Mark the todo as done");
        return cliOptions;
    }

    private static String getFileContent(Path filePath) throws IOException {
        return Files.exists(filePath) ? Files.readString(filePath) : "";
    }
}
