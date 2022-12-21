package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.BlockSlime;

public final class SlimeJump extends Mod {
    private final ModeValue mode = new ModeValue("Mode","Add",new String[]{"Add","Set"});
	private final NumberValue motion = new NumberValue("Motion",0.5, 0.0, 5.0, 0.1);

    public SlimeJump() {
        super("SlimeJump",Category.MOVE);
        registerValues(mode,motion);
    }

    @EventTarget
    public void onTick(EventTick e) {
        if (mc.world.getBlock(mc.player.getPosition().down()) instanceof BlockSlime) {
            if (mode.isCurrentMode("Add")) {
                mc.player.motionY += motion.getValue();
            } else if (mode.isCurrentMode("Set")) {
                mc.player.motionY = motion.getValue();
            }
        }
    }
}
