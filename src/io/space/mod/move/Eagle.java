package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public final class Eagle extends Mod {
    public Eagle() {
        super("Eagle",Category.MOVE);
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        final double y = mc.player.posY - 1.0;
        final BlockPos bp = new BlockPos(mc.player.posX,y,mc.player.posZ);

        if (mc.player.fallDistance <= 2.0f) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), mc.world.getBlockState(bp).getBlock() == Blocks.air);
        }
    }
}
