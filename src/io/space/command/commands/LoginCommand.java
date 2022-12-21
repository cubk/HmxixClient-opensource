package io.space.command.commands;

import io.space.command.Command;
import net.minecraft.client.Minecraft;

public final class LoginCommand extends Command {
    public LoginCommand() {
        super("login");
        setHelp("Auto login");
    }

    @Override
    public void execute(String[] args) {
        Minecraft.getMinecraft().player.sendChatMessage("/login f1414577F");
        super.execute(args);
    }
}
