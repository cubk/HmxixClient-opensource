package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.Wrapper;
import io.space.events.EventUpdateTimer;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;

public final class Timer extends Mod {
    private final NumberValue timerSpeed = new NumberValue("TimerSpeed",1.0,0.01,10.0,0.01);

    public Timer() {
        super("Timer",Category.WORLD);
        registerValues(timerSpeed);
    }

    @EventTarget(value = Priority.LOWEST)
    public void onUpdate(EventUpdateTimer e) {
        mc.timer.timerSpeed = timerSpeed.getValue().floatValue();
    }

    @Override
    protected void onDisable() {
        Wrapper.resetTimerSpeed();
        super.onDisable();
    }
}
