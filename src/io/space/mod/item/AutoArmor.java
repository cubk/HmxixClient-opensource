package io.space.mod.item;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import utils.hodgepodge.object.time.TimerUtils;

public final class AutoArmor extends Mod {
    public final static NumberValue DELAY = new NumberValue("Delay", 1.0D, 0.0D, 10.0D, 1.0D);
    private final BooleanValue onlyInventory = new BooleanValue("OnlyInventory",false);
    private final TimerUtils timerUtils = new TimerUtils();

    public AutoArmor() {
        super("AutoArmor", Category.ITEM);
        registerValues(DELAY,onlyInventory);
    }

    @EventTarget
    public void onEvent(EventTick event) {
        final long delay = DELAY.getValue().longValue() * 50L;

        if (onlyInventory.getValue() && !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiContainerCreative)) {
            return;
        }

        if ((mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) && timerUtils.hasReached((double)delay)) {
            getBestArmor();
        }
    }

    public void getBestArmor() {
        for(int type = 1; type < 5; ++type) {
            if (mc.player.inventoryContainer.getSlot(4 + type).getHasStack()) {
                ItemStack is = mc.player.inventoryContainer.getSlot(4 + type).getStack();
                if (isBestArmor(is, type)) {
                    continue;
                }

                drop(4 + type);
            }

            for(int i = 9; i < 45; ++i) {
                if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (isBestArmor(is, type) && getProtection(is) > 0.0F) {
                        this.shiftClick(i);
                        this.timerUtils.reset();
                        if (DELAY.getValue().longValue() > 0L) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }

        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        } else {
            for(int i = 5; i < 45; ++i) {
                if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                    ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (getProtection(is) > prot && is.getUnlocalizedName().contains(strType)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public void shiftClick(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, 1, mc.player);
    }

    public void drop(int slot) {
        ItemStack stack = mc.player.inventoryContainer.inventorySlots.get(slot).getStack();
        if (stack != null) {
            if (!(stack.getItem() instanceof ItemArmor)) {
                return;
            }
        }
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, 4, mc.player);
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0.0F;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot = (float)((double)prot + (double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075D);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0D);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0D);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0D);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0D);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100.0D);
        }

        return prot;
    }
}
