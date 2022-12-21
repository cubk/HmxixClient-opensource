package io.space.command;

import by.radioegor146.annotation.Native;
import io.space.Wrapper;
import io.space.command.commands.*;
import io.space.global.GlobalSetting;

import java.util.ArrayList;
import java.util.Arrays;

public final class CommandManager {
    public static CommandManager Instance;
    private final ArrayList<Command> commands = new ArrayList<>();

    @Native
    public void init() {
        Wrapper.Instance.getLogger().info("Initializing command manager");

        registerCommands(new HideCommand(),new HelpCommand(),new ToggleCommand(),new TCommand(),new BindCommand(),new RegisterCommand(),new LoginCommand(),new TeleportCommand(),new OpenClickGuiCommand(),new AutoWalkCommand(),new ChatCommand());
    }

    public void registerCommands(Command... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    public boolean onChat(String str) {
        if (GlobalSetting.Instance.getNoCommand().getValue()) {
            return false;
        }

        if (str.length() > 1 && str.startsWith("-")) {
            block2: {
                String[] args = str.trim().substring(1).split(" ");
                for (Command c : commands) {
                    if (!args[0].equalsIgnoreCase(c.getCommandName())) continue;
                    c.execute(Arrays.copyOfRange(args,1, args.length));
                    break block2;
                }
                Wrapper.sendMessage("未知命令! 可以尝试用\"-help\"获取命令帮助");
            }
            return true;
        }

        return false;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}
