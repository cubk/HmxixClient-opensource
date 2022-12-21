package io.space.command.commands;

import io.space.command.Command;
import io.space.renderer.gui.clickgui.ClickGui;
import net.minecraft.client.Minecraft;

public final class OpenClickGuiCommand extends Command {
    public OpenClickGuiCommand() {
        super("openclickgui");
        setHelp("打开ClickGui");
    }

    @Override
    public void execute(String[] args) {
        super.execute(args);

        new Thread("OpenClickGuiThread") {
            @Override
            public void run() {
                try {
                    sleep(1);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }

                Minecraft.getMinecraft().displayGuiScreen(new ClickGui());

                interrupt();
            }
        }.start();
    }
}
