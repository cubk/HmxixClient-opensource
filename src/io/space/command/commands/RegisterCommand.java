package io.space.command.commands;

import io.space.command.Command;
import net.minecraft.client.Minecraft;

public final class RegisterCommand extends Command {
    public RegisterCommand() {
        super("reg");
        setHelp("Auto register");
    }

    @Override
    public void execute(String[] args) {
        Minecraft.getMinecraft().player.sendChatMessage("/register f1414577F f1414577F");
        super.execute(args);
    }
}
