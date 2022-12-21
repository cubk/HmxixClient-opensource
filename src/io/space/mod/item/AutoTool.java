package io.space.mod.item;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MovingObjectPosition;

public final class AutoTool extends Mod {
    private final BooleanValue sword = new BooleanValue("SwordCheck", false);
    private final BooleanValue autoBack = new BooleanValue("AutoSwitchBack", false);
    private int oldSlot = -1;
    private boolean pressing = false;

    public AutoTool() {
        super("AutoTool", Category.ITEM);
        registerValues(sword, autoBack);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver != null) {
            if (mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return;

            if (sword.getValue() && mc.player.getHeldItem().getItem() instanceof ItemSword) {
                return;
            }

            final Block block = mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();

            if (block instanceof BlockAir) {
                return;
            }

            float strength = 1.0f;
            int bestItemIndex = -1;
            for (int i = 0; i < 8; i++) {
                ItemStack itemStack = mc.player.inventory.mainInventory[i];
                if (itemStack == null) continue;
                if (!(itemStack.getStrVsBlock(block) > strength)) continue;
                strength = itemStack.getStrVsBlock(block);
                bestItemIndex = i;
            }
            if (bestItemIndex != -1) {
                if (mc.player.inventory.currentItem != oldSlot && !pressing && autoBack.getValue()) {
                    oldSlot = mc.player.inventory.currentItem;
                    pressing = true;
                }
                mc.player.inventory.currentItem = bestItemIndex;
            }
        }
        if (autoBack.getValue() && !mc.gameSettings.keyBindAttack.isKeyDown() && pressing && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
            pressing = false;
        }
    }

    public static void switchToBest() {

    }
}
