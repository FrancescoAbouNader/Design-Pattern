package com.fges.todoapp;

public class App {
    public static void main(String[] args) throws Exception {
        CLIOptions cliOptions = new CLIOptions(args);
        if (!cliOptions.parse()) {
            System.exit(1);
        }

        CommandHandler handler = CommandHandlerFactory.getHandler(cliOptions.getCommand(), cliOptions.getFileName());
        if (handler == null) {
            System.err.println("Unsupported command");
            System.exit(1);
        }

        handler.execute(cliOptions.getPositionalArgs());
    }

    public static int exec(String[] array) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exec'");
    }
}
