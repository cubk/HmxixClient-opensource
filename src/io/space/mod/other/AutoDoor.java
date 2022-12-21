package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.mod.Mod;
import io.space.utils.MoveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.util.BlockPos;

import java.awt.*;

public final class AutoDoor extends Mod {
    public AutoDoor() {
        super("AutoDoor", Category.OTHER);
    }

    @EventTarget
    public void onRender3D(Event3D e) {
        final double yaw = MoveUtils.getDirection();
        final double x = mc.player.posX + -Math.sin(yaw) * 1;
        final double z = mc.player.posZ + Math.cos(yaw) * 1;

        final BlockPos pos = new BlockPos(x,mc.player.posY,z);
        final Block b = mc.world.getBlockState(pos).getBlock();
        if (b instanceof BlockDoor) {
            if (!BlockDoor.isOpen(mc.world,pos)) {
                mc.player.swingItem();
                mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getHeldItem(), pos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec);
            }
        }
    }
}
