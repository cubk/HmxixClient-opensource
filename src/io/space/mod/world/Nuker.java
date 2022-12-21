package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPostUpdate;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.RotationUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import utils.hodgepodge.object.time.TimerUtils;

public final class Nuker extends Mod {
    public NumberValue radius = new NumberValue("Horizontal Radius", 3d, 0d, 50d, 1d);
    public NumberValue height = new NumberValue("Height Radius", 1d, 0d, 50d, 1d);
    public BooleanValue silent = new BooleanValue("Silent", true);
    public BooleanValue autoTool = new BooleanValue("AutoTool", true);
    private boolean isRunning;
    private final TimerUtils timer = new TimerUtils();
    private int posX, posY, posZ;

    public Nuker() {
        super("Nuker", Category.WORLD);
        registerValues(radius, height, silent, autoTool);
    }
    
    @EventTarget
    public void onPre(EventPreUpdate e) {
        this.isRunning = false;
        int radius = this.radius.getValue().intValue();
        int height = this.height.getValue().intValue();

        for (int y = height; y >= -height; --y) {
            for (int x = -radius; x < radius; ++x) {
                for (int z = -radius; z < radius; ++z) {
                    this.posX = (int) Math.floor(mc.player.posX) + x;
                    this.posY = (int) Math.floor(mc.player.posY) + y;
                    this.posZ = (int) Math.floor(mc.player.posZ) + z;
                    if (mc.player.getDistanceSq(mc.player.posX + (double) x, mc.player.posY + (double) y, mc.player.posZ + (double) z) <= 16.0D) {
                        Block block = mc.world.getBlock(this.posX, this.posY, this.posZ);
                        boolean blockChecks = timer.hasReached(50);
                        blockChecks = blockChecks && canSeeBlock(this.posX + 0.5F, this.posY + 0.9f, this.posZ + 0.5F) && !(block instanceof BlockAir);
                        blockChecks = blockChecks && (block.getBlockHardness(mc.world, BlockPos.ORIGIN) != -1.0F || mc.playerController.isInCreativeMode());
                        if (blockChecks) {
                            this.isRunning = true;
                            float[] angles = RotationUtils.getRotationFromPosition(this.posX + 0.5, this.posZ + 0.5, this.posY + 0.5);
                            final float yaw = angles[0];
                            final float pitch = angles[1];
                            if (silent.getValue()) {
                                e.setYaw(yaw);
                                e.setPitch(pitch);

                                mc.player.rotationYawHead = yaw;
                                mc.player.prevRotationYawHead = yaw;
                                mc.player.renderYawOffset = yaw;
                                mc.player.prevRenderYawOffset = yaw;
                                mc.player.rotationPitchHead = pitch;
                                mc.player.prevRotationPitchHead = pitch;
                            } else {
                                mc.player.rotationYaw = yaw;
                                mc.player.rotationPitch = pitch;
                            }
                            return;
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onDisable() {
        super.onDisable();
        isRunning = false;
        posX = posY = posZ = 0;
    }

    @EventTarget
    public void onPost(EventPostUpdate e) {
        if (this.isRunning) {
            final BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
            final Block block = mc.world.getBlock(blockPos);

            if (autoTool.getValue()) {
                float strength = 1.0f;
                int bestItemIndex = -1;
                for (int i = 0; i < 8; i++) {
                    ItemStack itemStack = mc.player.inventory.mainInventory[i];
                    if (itemStack == null) continue;
                    if (!(itemStack.getStrVsBlock(block) > strength)) continue;
                    strength = itemStack.getStrVsBlock(block);
                    bestItemIndex = i;
                }
                if (bestItemIndex != -1 && mc.player.inventory.currentItem != bestItemIndex) {
                    mc.player.inventory.currentItem = bestItemIndex;
                }
            }

            mc.player.swingItem();
            mc.playerController.onPlayerDamageBlock(blockPos, getFacing(blockPos));

            if (mc.playerController.curBlockDamageMP >= 1.0D)
                timer.reset();

        }
    }

    public boolean canSeeBlock(final float n, final float n2, final float n3) {
        return this.getFacing(new BlockPos(n, n2, n3)) != null;
    }

    public EnumFacing getFacing(final BlockPos blockPos) {
        EnumFacing[] array;
        for (int length = (array = new EnumFacing[] { EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.DOWN }).length, i = 0; i < length; ++i) {
            final EnumFacing enumFacing = array[i];
            final EntitySnowball entitySnowball = new EntitySnowball(mc.world);
            entitySnowball.posX = blockPos.getX() + 0.5;
            entitySnowball.posY = blockPos.getY() + 0.5;
            entitySnowball.posZ = blockPos.getZ() + 0.5;
            entitySnowball.posX += enumFacing.getDirectionVec().getX() * 0.5;
            entitySnowball.posY += enumFacing.getDirectionVec().getY() * 0.5;
            entitySnowball.posZ += enumFacing.getDirectionVec().getZ() * 0.5;
            if (mc.player.canEntityBeSeen(entitySnowball)) {
                return enumFacing;
            }
        }
        return null;
    }
}
