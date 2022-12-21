package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import net.minecraft.item.*;

public final class Sprint extends Mod {
    private final BooleanValue all = new BooleanValue("All",false);
    private final BooleanValue checkCollided = new BooleanValue("CheckCollided",false);

    public Sprint() {
        super("Sprint",Category.MOVE);
        registerValues(all,checkCollided);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (all.getValue() ? mc.player.isMoving() : mc.player.isForwardMoving()) {
            if (mc.player.isUsingItem()) {
                final boolean noSlowEnable = NoSlow.Instance.isEnable();

                if (!noSlowEnable) return;

                if (NoSlow.onlyIncludesSword() && mc.player.getHeldItem() != null) {
                    final Item item = mc.player.getHeldItem().getItem();

                    if (item instanceof ItemBow || item instanceof ItemFood || (item instanceof ItemPotion && !ItemPotion.isSplash(mc.player.getHeldItem().getMetadata()))) {
                        return;
                    }
                }
            }

            if (checkCollided.getValue() && mc.player.isCollidedHorizontally) return;
            if (mc.player.isSneaking()) return;

            mc.player.setSprinting(true);
        }
    }
}
