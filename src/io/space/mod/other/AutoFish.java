package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public final class AutoFish extends Mod {
    public AutoFish() {
        super("AutoFish", Category.OTHER);
    }

    @EventTarget
    public void onUpdate(EventPacket e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity) {
            final S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
            final Entity ent = mc.world.getEntityByID(packet.getEntityID());
            if (ent instanceof EntityFishHook) {
                final EntityFishHook fishHook = (EntityFishHook) ent;
                final int entityId = fishHook.angler.getEntityId();
                if (entityId == mc.player.getEntityId()) {
                    if (mc.player.inventory.currentItem != grabRodSlot()) {
                        return;
                    }

                    if (packet.getMotionX() == 0 && packet.getMotionY() != 0 && packet.getMotionZ() == 0) {
                        mc.rightClickMouse();
                        mc.rightClickMouse();
                    }
                }
            }
        }
    }
    
    private int grabRodSlot() {
        for (int i2 = 0; i2 < 9; ++i2) {
             ItemStack itemStack = mc.player.inventory.mainInventory[i2];
            if (itemStack != null && itemStack.getItem() instanceof ItemFishingRod) {
                return i2;
            }
        }
        return -1;
    }
}
