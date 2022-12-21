package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.command.CommandManager;

public final class HelpCommand extends Command {
    public HelpCommand() {
        super("help");
        setHelp("Show command help list");
    }

    @Override
    public void execute(String[] args) {
        Wrapper.sendMessage("Command help list:");
        for (Command command : CommandManager.Instance.getCommands()) {
            if (command.hideHelp) continue;
            Wrapper.sendMessage("-" + command.getCommandName() + " -> " + command.getHelp());
        }
    }
}
