package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;

public final class NoJumpDelay extends Mod {
    public NoJumpDelay () {
        super("NoJumpDelay",Category.MOVE);
    }

    @EventTarget
    public void onTick(EventTick e) {
        mc.player.jumpTicks = 0;
    }
}
