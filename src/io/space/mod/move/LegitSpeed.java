package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventJump;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import net.minecraft.potion.Potion;

public final class LegitSpeed extends Mod {
    public LegitSpeed() {
        super("LegitSpeed", Category.MOVE);
    }

    @EventTarget
    public void onJump(EventJump event) {
        if (MoveUtils.getNormalSpeedEffect() <= 0 || mc.player.moveForward <= 0.0f || !mc.player.isSprinting() || LegitSpeed.getSpeedDuration() <= 20) {
            return;
        }

        mc.player.motionX *= 1.0f + (float) MoveUtils.getNormalSpeedEffect() * 0.15f;
        mc.player.motionZ *= 1.0f + (float) MoveUtils.getNormalSpeedEffect() * 0.15f;
    }

    public static int getSpeedDuration() {
        if (mc.player.isPotionActive(Potion.moveSpeed)) {
            return mc.player.getActivePotionEffect(Potion.moveSpeed).getDuration();
        }
        return 0;
    }
}