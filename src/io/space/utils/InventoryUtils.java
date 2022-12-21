package io.space.utils;

import io.space.object.Filter;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class InventoryUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void swap(int slot, int currentSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, currentSlot, 2, mc.player);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 1, 4, mc.player);
    }

    public static List<ItemStack> searchItems(Filter<ItemStack> filter) {
        return Arrays.stream(mc.player.inventory.mainInventory).filter(filter::check).collect(Collectors.toList());
    }
}
