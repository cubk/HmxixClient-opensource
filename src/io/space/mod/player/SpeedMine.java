package io.space.mod.player;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPacket;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class SpeedMine extends Mod {
    private boolean ready = false;
    private float blockHardness = 0.0f;
    public BlockPos blockPos;
    public EnumFacing facing;

    public SpeedMine() {
        super("SpeedMine", Category.PLAYER);
    }

    @EventTarget
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof C07PacketPlayerDigging && !mc.playerController.extendedReach()
                && mc.playerController != null) {
            C07PacketPlayerDigging packet = (C07PacketPlayerDigging) event.getPacket();
            if (packet.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                this.ready = true;
                this.blockPos = packet.getPosition();
                this.facing = packet.getFacing();
                this.blockHardness = 0.0f;
            } else if (packet.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK
                    || packet.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                this.ready = false;
                this.blockPos = null;
                this.facing = null;
            }
        }
    }

    @EventTarget
    private void onUpdate(EventPreUpdate e) {
        if(mc.playerController.isInCreativeMode() && !mc.player.isServerWorld()){
            return;
        }
        if (mc.playerController.extendedReach()) {
            mc.playerController.blockHitDelay = 0;
        } else if (this.ready) {
            Block block = mc.world.getBlockState(this.blockPos).getBlock();
            this.blockHardness += (float) ((double) block.getPlayerRelativeBlockHardness(mc.player, mc.world,
                    this.blockPos) * 1.4);
            if (this.blockHardness >= 1.0f) {
                mc.world.setBlockState(this.blockPos, Blocks.air.getDefaultState(), 11);
                mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(
                        C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockPos, this.facing));
                this.blockHardness = 0.0f;
                this.ready = false;
            }
        }
    }
}
