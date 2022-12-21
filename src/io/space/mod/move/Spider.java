package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventCollideWithBlock;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.util.AxisAlignedBB;

public final class Spider extends Mod {
    private final ModeValue mode = new ModeValue("Mode", "Jump", new String[]{"Motion", "Jump"});
    private final NumberValue speed = new NumberValue("MotionSpeed", 0.1, 0.0, 0.5, 0.01);

    public Spider() {
        super("Spider", Category.MOVE);
        registerValues(mode, speed);
    }

    @EventTarget
    public void onUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("Motion")) {
            if (mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava()) {
                mc.player.motionY = speed.getValue();
            }
        } else if (mode.isCurrentMode("Jump")) {
            if (mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava()) {
                if (mc.player.onGround) {
                    mc.player.motionY = 0.39;
                } else if (mc.player.motionY < 0.0) {
                    mc.player.motionY = -0.24;
                }

                e.setX(e.getX() + MoveUtils.getMotionX(1.0E-10));
                e.setZ(e.getZ() + MoveUtils.getMotionZ(1.0E-10));
            }
        }
    }

    @EventTarget
    public void onBlockCollide(EventCollideWithBlock e) {
        if (mode.isCurrentMode("Jump")) {
            if (mc.player.isCollidedHorizontally && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isInLava()) {
                e.getBoxes().add(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(mc.player.posX, (int) mc.player.posY - 1, mc.player.posZ));
            }
        }
    }
}
