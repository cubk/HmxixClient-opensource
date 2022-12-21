package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.NumberValue;
import net.minecraft.item.ItemBlock;

public final class FastPlace extends Mod {
    private final NumberValue speed = new NumberValue("Speed",0,0,4,1);

    public FastPlace() {
        super("FastPlace",Category.PLAYER);
        registerValues(speed);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.player.getHeldItem() != null) {
            if (mc.rightClickDelayTimer <= speed.getValue().intValue() && mc.player.getHeldItem().getItem() instanceof ItemBlock) {
                mc.rightClickDelayTimer = 0;
            }
        }
    }
}
