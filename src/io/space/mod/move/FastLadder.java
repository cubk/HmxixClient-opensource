package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;

public final class FastLadder extends Mod {
    public final NumberValue addend = new NumberValue("Addend",0.0,0.0,1.0,0.01);
    public final NumberValue downAddend = new NumberValue("DownAddend",0.0,0.0,1.0,0.01);

    public FastLadder() {
        super("FastLadder",Category.MOVE);
        registerValues(addend,downAddend);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mc.player.isOnLadder()) {
            if (mc.player.isMoving()) {
                mc.player.motionY += addend.getValue();
            } else {
                mc.player.motionY -= downAddend.getValue();
            }
        }
    }
}
