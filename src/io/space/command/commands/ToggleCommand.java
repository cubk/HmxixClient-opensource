package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.mod.Mod;
import io.space.mod.ModManager;

public final class ToggleCommand extends Command {
    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            Wrapper.sendMessage("-toggle <ModName>");
            return;
        }

        Mod mod = ModManager.Instance.getModFromName(args[0]);

        if (mod != null) {
            mod.toggle();
            Wrapper.sendMessage("Toggle mod " + mod.getModName());
        } else {
            Wrapper.sendMessage("找不到Mod" + args[0]);
        }
    }
}
