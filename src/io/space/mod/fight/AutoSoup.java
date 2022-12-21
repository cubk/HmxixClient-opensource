package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.utils.InventoryUtils;
import io.space.value.values.NumberValue;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import utils.hodgepodge.object.time.TimerUtils;

public final class AutoSoup extends Mod {
    private final NumberValue health = new NumberValue("Health", 10.0, 1.0, 20.0, 0.5);
    private final NumberValue delay = new NumberValue("Delay", 200.0, 0.0, 8000.0, 1.0);
    private final TimerUtils timerUtil = new TimerUtils(true);

    public AutoSoup() {
        super("AutoSoup",Category.FIGHT);
        registerValues(health,delay);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.player.getHealth() <= health.getValue() && timerUtil.hasReached(delay.getValue())) {
            for (int i = 0;i < mc.player.inventory.mainInventory.length;i++) {
                final ItemStack itemStack = mc.player.inventory.mainInventory[i];

                if (itemStack == null) continue;
                if (!(itemStack.getItem() instanceof ItemSoup)) continue;

                if (i > 8) {
                    InventoryUtils.swap(i,8);
                    i = 8;
                }

                final int oldSlot = mc.player.inventory.currentItem;
                mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(i));
                mc.getNetHandler().sendPacket(new C08PacketPlayerBlockPlacement(mc.player.inventory.getCurrentItem()));
                mc.getNetHandler().sendPacket(new C09PacketHeldItemChange(oldSlot));
                break;
            }
        }
    }
}
