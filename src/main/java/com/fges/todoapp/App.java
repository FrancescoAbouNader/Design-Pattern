package com.fges.todoapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

        TodoProcessor todoProcessor = new TodoProcessor();

        todoProcessor.process(command, fileName, positionalArgs, fileContent);

        System.err.println("Done.");
        return 0;
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

class TodoProcessor {
    private final ObjectMapper mapper;

    public TodoProcessor() {
        this.mapper = new ObjectMapper();
    }

    public void process(String command, String fileName, List<String> positionalArgs, String fileContent) throws IOException {
        if (command.equals("insert")) {
            boolean isDone = false;
            processInsertCommand(fileName, positionalArgs, fileContent, isDone);
        }

        if (command.equals("list")) {
            boolean showDone = false;
            processListCommand(fileName, fileContent, showDone);
        }
    }

    private void processInsertCommand(String fileName, List<String> positionalArgs, String fileContent, boolean isDone) throws IOException {
        if (positionalArgs.size() < 2) {
            System.err.println("Missing TODO name");
            return;
        }

        String todo = positionalArgs.get(1);

        if (fileName.endsWith(".json")) {
            insertTodoToJsonFile(fileContent, todo, fileName, isDone);
        }

        if (fileName.endsWith(".csv")) {
            insertTodoToCsvFile(fileContent, todo, fileName);
        }
    }

    private void insertTodoToJsonFile(String fileContent, String todo, String fileName, boolean isDone) throws IOException {
        JsonNode actualObj = mapper.readTree(fileContent);

        if (actualObj instanceof MissingNode) {
            actualObj = JsonNodeFactory.instance.arrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            ObjectNode todoNode = mapper.createObjectNode();
            todoNode.put("name", todo);
            todoNode.put("done", isDone);
            arrayNode.add(todoNode);
        }

        Files.writeString(Paths.get(fileName), actualObj.toString());
    }

    private void insertTodoToCsvFile(String fileContent, String todo, String fileName) throws IOException {
        if (!fileContent.endsWith("\n") && !fileContent.isEmpty()) {
            fileContent += "\n";
        }
        fileContent += todo;

        Files.writeString(Paths.get(fileName), fileContent);
    }

    private void processListCommand(String fileName, String fileContent, boolean showDone) {
        if (fileName.endsWith(".json")) {
            listTodosFromJsonFile(fileContent, showDone);
        }

        if (fileName.endsWith(".csv")) {
            listTodosFromCsvFile(fileContent);
        }
    }

    private void listTodosFromJsonFile(String fileContent, boolean showDone) {
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(fileContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (actualObj instanceof MissingNode) {
            actualObj = JsonNodeFactory.instance.arrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            arrayNode.forEach(node -> {
                boolean isDone = node.get("done") != null && node.get("done").asBoolean();
                if (!showDone && isDone) {
                    return;
                }
                System.out.println((isDone ? "Done: " : "") + "- " + node.get("name").asText());
            });
        }
    }

    private static void listTodosFromCsvFile(String fileContent) {
        System.out.println(Arrays.stream(fileContent.split("\n"))
                .map(todo -> "- " + todo)
                .collect(Collectors.joining("\n"))
        );
    }
}
