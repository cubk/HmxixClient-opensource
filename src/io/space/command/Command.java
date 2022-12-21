package io.space.command;

public abstract class Command {
    private final String commandName;
    private String help = "No help...";
    public boolean hideHelp;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public void execute(String[] args) {}

    public final String getCommandName() {
        return commandName;
    }

    public final String getHelp() {
        return help;
    }

    public final void setHelp(String help) {
        this.help = help;
    }
}
