package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import net.minecraft.item.*;

public final class FastThrow extends Mod {
    public FastThrow() {
        super("FastThrow", Category.FIGHT);
    }

    @EventTarget
    public void onTick(EventTick e) {
        Item item = mc.player.getHeldItem().getItem();
        if ((item instanceof ItemSnowball || item instanceof ItemPotion || item instanceof ItemEgg || item instanceof ItemExpBottle || item instanceof ItemFishingRod) && FastThrow.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.rightClickDelayTimer = 0;
        }
    }
}
