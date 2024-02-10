package com.fges.todoapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TodoProcessor {
    private final ObjectMapper mapper;

    public TodoProcessor() {
        this.mapper = new ObjectMapper();
    }

    public int process(String command, String fileName, List<String> positionalArgs, String fileContent) throws IOException {
        if (command.equals("insert")) {
            boolean isDone = false;
            processInsertCommand(fileName, positionalArgs, fileContent, isDone);
        } else if (command.equals("list")) {
            boolean showDone = false;
            processListCommand(fileName, fileContent, showDone);
        }
        return 0;
    }

    private void processInsertCommand(String fileName, List<String> positionalArgs, String fileContent, boolean isDone) throws IOException {
        if (positionalArgs.size() < 2) {
            System.err.println("Missing TODO name");
            return;
        }

        String todo = positionalArgs.get(1);

        if (fileName.endsWith(".json")) {
            insertTodoToJsonFile(fileContent, todo, fileName, isDone);
        } else if (fileName.endsWith(".csv")) {
            insertTodoToCsvFile(fileContent, todo, fileName);
        }
    }

    private void insertTodoToJsonFile(String fileContent, String todo, String fileName, boolean isDone) throws IOException {
        JsonNode actualObj = mapper.readTree(fileContent);
        if (actualObj == null || actualObj.isMissingNode()) {
            actualObj = mapper.createArrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            ObjectNode todoNode = mapper.createObjectNode();
            ((ObjectNode) todoNode).put("name", todo);
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
        } else if (fileName.endsWith(".csv")) {
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

        if (actualObj == null || actualObj.isMissingNode()) {
            actualObj = mapper.createArrayNode();
        }

        if (actualObj instanceof ArrayNode arrayNode) {
            arrayNode.forEach(node -> {
                boolean isDone = node.get("done") != null && node.get("done").asBoolean();
                if (!showDone && isDone) {
                    System.out.println((isDone ? "Done: " : "") + "- " + node.get("name").asText());
                } else {
                    System.out.println(node.get("name").asText());
                }
            });
        }
    }

    private static void listTodosFromCsvFile(String fileContent) {
        System.out.println(
                List.of(fileContent.split("\n"))
                        .stream()
                        .map(todo -> "- " + todo)
                        .collect(Collectors.joining("\n"))
        );
    }
}
