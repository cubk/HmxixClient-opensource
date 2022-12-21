package io.space.mod.item;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import utils.hodgepodge.object.time.TimerUtils;

public final class FastDrop extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Fast",new String[]{"Fast","Delay"});
    private final NumberValue delay = new NumberValue("Delay",1.0,0.0,1000.0,1.0);
    private final NumberValue clicks = new NumberValue("FastClicks",64.0,1.0,64.0,1.0);

    private final TimerUtils timerUtils = new TimerUtils(true);

    public FastDrop() {
        super("FastDrop",Category.ITEM);
        registerValues(mode,delay,clicks);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.gameSettings.keyBindDrop.isKeyDown()) {
            if (mc.player.getHeldItem() != null) {
                if (mode.isCurrentMode("Fast")) {
                    for (int i = 0; i < clicks.getValue(); i++) {
                        mc.player.dropOneItem(false);
                    }
                } else if (mode.isCurrentMode("Delay")) {
                    if (timerUtils.hasReached(delay.getValue())) {
                        mc.player.dropOneItem(false);
                    }
                }
            }
        }
    }
}
