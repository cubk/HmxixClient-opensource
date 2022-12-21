package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;

public final class AirJump extends Mod {
    public AirJump() {
        super("AirJump",Category.MOVE);
    }

    @EventTarget
    public void onUpdate(EventPreUpdate e) {
        if (!mc.player.onGround) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                e.setOnGround(true);
                mc.player.onGround = true;
            }
        }
    }
}
