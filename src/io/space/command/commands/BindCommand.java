package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import org.lwjgl.input.Keyboard;

public final class BindCommand extends Command {
    public BindCommand() {
        super("bind");
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            Wrapper.sendMessage("-bind <ModName> <KeyName>");
            return;
        }

        final Mod mod = ModManager.Instance.getModFromName(args[0]);

        if (mod != null) {
            final int keyCode = Keyboard.getKeyIndex(args[1].toUpperCase());
            mod.setKeyCode(keyCode);
            Wrapper.sendMessage("将" + mod.getModName() + "的Key设置为" + Keyboard.getKeyName(keyCode));
        } else Wrapper.sendMessage("找不到Mod:" + args[0]);
    }
}
