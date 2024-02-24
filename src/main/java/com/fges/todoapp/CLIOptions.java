package com.fges.todoapp;

import java.util.List;

import org.apache.commons.cli.*;

public class CLIOptions {
    private final String[] args;
    private CommandLine cmd;
    private boolean isDone = false; // Pour stocker l'état de l'option --done
    private String command;
    private String fileName;

    public CLIOptions(String[] args) {
        this.args = args;
    }

    public boolean parse() {
        Options cliOptions = new Options();
        cliOptions.addRequiredOption("s", "source", true, "File containing the todos");
        cliOptions.addOption("d", "done", false, "Mark todo as done or list only done todos");

        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(cliOptions, args);
            if (cmd.hasOption("d")) {
                isDone = true; // Définir isDone à true si l'option --done est présente
            }
            return true;
        } catch (ParseException ex) {
            System.err.println("Fail to parse arguments: " + ex.getMessage());
            return false;
        }
    }

    public String getCommand() {
        return command;
    }
    public String getFileName() {
        return cmd.getOptionValue("s");
    }

    public boolean isDone() {
        return isDone;
    }

    public List<String> getPositionalArgs() {
        return cmd.getArgList();
    }
}
