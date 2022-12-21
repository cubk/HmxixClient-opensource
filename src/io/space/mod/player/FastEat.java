package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class FastEat extends Mod {
    public FastEat() {
        super("FastEat",Category.PLAYER);
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mc.player.getHeldItem() != null) {
            final Item item = mc.player.getHeldItem().getItem();
            if (mc.player.isUsingItem() && ((item instanceof ItemFood) || (item instanceof ItemPotion))) {
                for (int i = 0; i < 3; i++) {
                    mc.getNetHandler().sendPacket(new C03PacketPlayer(e.isOnGround()));
                }
            }
        }
    }
}
