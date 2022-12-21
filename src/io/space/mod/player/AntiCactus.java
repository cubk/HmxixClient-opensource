package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventCollideWithBlock;
import io.space.mod.Mod;
import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;

public final class AntiCactus extends Mod {
    public AntiCactus() {
        super("AntiCactus", Category.PLAYER);
    }

    @EventTarget
    public void onBoundingBox(EventCollideWithBlock event) {
        if (event.getBlock() instanceof BlockCactus) event.setBoundingBox(new AxisAlignedBB(event.getBlockPos().getX(),event.getBlockPos().getY(),event.getBlockPos().getZ(),event.getBlockPos().getX() + 1,event.getBlockPos().getY() + 1,event.getBlockPos().getZ() + 1));
    }
}

