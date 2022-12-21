package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;

public final class WideAngle extends Mod {
    private final NumberValue distance = new NumberValue("Distance",4.0,0.0,10.0,0.01);

    public WideAngle() {
        super("WideAngle",Category.VISUAL);
        registerValues(distance);
    }

    @EventTarget
    public void onTick(EventTick e) {
        mc.entityRenderer.thirdPersonDistance = distance.getValue().floatValue();
    }

    @Override
    protected void onDisable() {
        mc.entityRenderer.thirdPersonDistance = 4.0f;
        super.onDisable();
    }
}
