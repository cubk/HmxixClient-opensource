package io.space.mod.item;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import utils.hodgepodge.object.time.TimerUtils;

public final class ChestStealer extends Mod {
    private final NumberValue delay = new NumberValue("Delay",100,0.0,1000.0,1.0);
    private final BooleanValue menuCheck = new BooleanValue("MenuCheck", true);
    private final BooleanValue autoClose = new BooleanValue("AutoClose", true);

    private final TimerUtils timerUtils = new TimerUtils(true);

    public ChestStealer() {
        super("ChestStealer",Category.ITEM);
        registerValues(delay,menuCheck,autoClose);
    }

    @EventTarget
    private void onTick(EventTick e) {
        if (mc.player.openContainer instanceof ContainerChest) {
            final ContainerChest container = (ContainerChest) mc.player.openContainer;

            if (menuCheck.getValue() && !StatCollector.translateToLocal("container.chest").equalsIgnoreCase(container.getLowerChestInventory().getDisplayName().getUnformattedText())) {
                if (!container.getLowerChestInventory().getDisplayName().getFormattedText().contains("étoiles dans ce coffre")) {
                    return;
                }
            }

            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                if (container.getLowerChestInventory().getStackInSlot(i) == null || !timerUtils.hasReached(delay.getValue())) continue;

                mc.playerController.windowClick(container.windowId, i, 0, 1, mc.player);
            }

            if (autoClose.getValue() && isEmpty()) {
                mc.player.closeScreen();
            }
        }
    }

    private boolean isEmpty() {
        if (mc.player.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)mc.player.openContainer;
            for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); ++i) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack == null || itemStack.getItem() == null) continue;
                return false;
            }
        }
        return true;
    }
}
