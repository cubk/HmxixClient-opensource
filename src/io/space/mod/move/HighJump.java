package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventJump;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;

public class    HighJump extends Mod {
    private final NumberValue motionY = new NumberValue("MotionY",1.0,0.0,10.0,0.01);

    public HighJump() {
        super("HighJump",Category.MOVE);
        registerValues(motionY);
    }

    @EventTarget
    public void onJump(EventJump e) {
        e.cancelEvent();

        mc.player.motionY = motionY.getValue();
    }
}
