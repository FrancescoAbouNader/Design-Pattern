package com.fges.todoapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

    

import com.fasterxml.jackson.databind.node.ObjectNode;


public class TodoJsonManager extends FileManager {

    public TodoJsonManager(String fileName) {
        super(fileName);
    }

    @Override
    public void insert(String todo, boolean isDone) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode rootNode;
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            rootNode = (ArrayNode) mapper.readTree(content);
        } catch (IOException e) {
            rootNode = mapper.createArrayNode(); // Créer un nouveau ArrayNode si le fichier est inexistant ou vide
        }

        ObjectNode todoNode = mapper.createObjectNode();
        todoNode.put("task", todo);
        todoNode.put("done", isDone);
        rootNode.add(todoNode);

        Files.writeString(Paths.get(fileName), mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
    }

    @Override
    public void list(boolean onlyDone) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode rootNode;
        try {
            String content = new String(Files.readAllBytes(Paths.get(fileName)));
            rootNode = (ArrayNode) mapper.readTree(content);
        } catch (IOException e) {
            System.err.println("Could not read the file: " + e.getMessage());
            return;
        }

        rootNode.forEach(node -> {
            boolean done = node.get("done").asBoolean();
            String task = node.get("task").asText();
            if (!onlyDone || done) {
                System.out.println("- " + (done ? "Done: " : "") + task);
            }
        });
    }

    @Override
    public void list() throws Exception {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'list'");
    }
}

