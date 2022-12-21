package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdateMovementInput;
import io.space.mod.Mod;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public final class ScreenMove extends Mod {
    private static final int[] keys = new int[]{
            mc.gameSettings.keyBindSprint.getKeyCode(),
            mc.gameSettings.keyBindJump.getKeyCode(),
            mc.gameSettings.keyBindForward.getKeyCode(),
            mc.gameSettings.keyBindBack.getKeyCode(),
            mc.gameSettings.keyBindLeft.getKeyCode(),
            mc.gameSettings.keyBindRight.getKeyCode()
    };

    public ScreenMove() {
        super("ScreenMove",Category.MOVE);
    }

    @EventTarget
    public void onUpdateMovementInput(EventPreUpdateMovementInput e) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            for (int key : keys) {
                KeyBinding.setKeyBindState(key,Keyboard.isKeyDown(key));
            }
        }
    }
}
