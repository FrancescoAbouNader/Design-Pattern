package com.fges.todoapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        System.exit(exec(args));
    }

    public static int exec(String[] args) throws IOException {
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

        processCommand(command, fileName, positionalArgs, fileContent);

        System.err.println("Done.");
        return 0;
    }

    private static Options createCliOptions() {
        Options cliOptions = new Options();
        cliOptions.addRequiredOption("s", "source", true, "File containing the todos");
        return cliOptions;
    }

    private static String getFileContent(Path filePath) throws IOException {
        return Files.exists(filePath) ? Files.readString(filePath) : "";
    }

    private static void processCommand(String command, String fileName, List<String> positionalArgs, String fileContent) throws IOException {
        if (command.equals("insert")) {
            processInsertCommand(fileName, positionalArgs, fileContent);
        }

        if (command.equals("list")) {
            processListCommand(fileName, fileContent);
        }
    }

    private static void processInsertCommand(String fileName, List<String> positionalArgs, String fileContent) throws IOException {
        if (positionalArgs.size() < 2) {
            System.err.println("Missing TODO name");
            return;
        }

        String todo = positionalArgs.get(1);

        if (fileName.endsWith(".json")) {
            insertTodoToJsonFile(fileContent, todo, fileName);
        }

        if (fileName.endsWith(".csv")) {
            insertTodoToCsvFile(fileContent, todo, fileName);
        }
    }

    private static void insertTodoToJsonFile(String fileContent, String todo, String fileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(fileContent);

        if (actualObj instanceof MissingNode) {
            actualObj = JsonNodeFactory.instance.arrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            arrayNode.add(todo);
        }

        Files.writeString(Paths.get(fileName), actualObj.toString());
    }

    private static void insertTodoToCsvFile(String fileContent, String todo, String fileName) throws IOException {
        if (!fileContent.endsWith("\n") && !fileContent.isEmpty()) {
            fileContent += "\n";
        }
        fileContent += todo;

        Files.writeString(Paths.get(fileName), fileContent);
    }

    private static void processListCommand(String fileName, String fileContent) {
        if (fileName.endsWith(".json")) {
            listTodosFromJsonFile(fileContent);
        }

        if (fileName.endsWith(".csv")) {
            listTodosFromCsvFile(fileContent);
        }
    }

    private static void listTodosFromJsonFile(String fileContent) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(fileContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (actualObj instanceof MissingNode) {
            actualObj = JsonNodeFactory.instance.arrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            arrayNode.forEach(node -> System.out.println("- " + node.toString()));
        }
    }

    private static void listTodosFromCsvFile(String fileContent) {
        System.out.println(Arrays.stream(fileContent.split("\n"))
                .map(todo -> "- " + todo)
                .collect(Collectors.joining("\n"))
        );
    }
}
