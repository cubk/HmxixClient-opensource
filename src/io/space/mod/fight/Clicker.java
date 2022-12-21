package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.object.CPSDelay;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemSword;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

public final class Clicker extends Mod {
    private final NumberValue maxCps = new NumberValue("MaxCPS",6,1,20,1) {
        @Override
        protected boolean onChangeValue(Double preValue, Double postValue) {
            return postValue >= minCps.getValue();
        }
    };
    private final NumberValue minCps = new NumberValue("MinCPS",6,1,20,1) {
        @Override
        protected boolean onChangeValue(Double preValue, Double postValue) {
            return postValue <= maxCps.getValue();
        }
    };
    private final BooleanValue blockHit = new BooleanValue("BlockHit",false);
    private final BooleanValue autoUnBlock = new BooleanValue("AutoUnblock",false);

    private final CPSDelay cpsDelay = new CPSDelay();

    public Clicker() {
        super("Clicker",Category.FIGHT);
        registerValues(maxCps,minCps,blockHit,autoUnBlock);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.currentScreen == null && Mouse.isButtonDown(0)) {
            if (!blockHit.getValue() && mc.player.isUsingItem()) return;

            if (cpsDelay.shouldAttack(minCps.getValue().intValue() == maxCps.getValue().intValue() ? maxCps.getValue().intValue() : ThreadLocalRandom.current().nextInt(minCps.getValue().intValue(),maxCps.getValue().intValue()))) {
                mc.leftClickCounter = 0;
                mc.clickMouse();

                if (autoUnBlock.getValue()) {
                    if (Mouse.isButtonDown(1)) {
                        if (mc.player.getHeldItem() != null && mc.player.getHeldItem().getItem() != null && mc.player.getHeldItem().getItem() instanceof ItemSword) {
                            if (mc.player.isBlocking()) {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                                mc.playerController.onStoppedUsingItem(mc.player);
                                mc.player.itemInUseCount = 0;
                            } else {
                                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                                mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected String getModTag() {
        return minCps.getValue() + " - " + maxCps.getValue();
    }
}
