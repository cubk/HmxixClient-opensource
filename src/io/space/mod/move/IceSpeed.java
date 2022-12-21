package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.value.values.NumberValue;

public final class IceSpeed extends Mod {
    private final NumberValue speed = new NumberValue("Speed",0.5,0.0,10.0,0.1);

    public IceSpeed() {
        super("IceSpeed",Category.MOVE);
        registerValues(speed);
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        if (MoveUtils.isOnIce() && mc.player.isMoving()) {
            MoveUtils.setSpeed(speed.getValue());
        }
    }
}