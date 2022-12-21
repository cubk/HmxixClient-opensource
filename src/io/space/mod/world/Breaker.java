package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public final class Breaker extends Mod {
	private final ModeValue mode = new ModeValue("Mode","Bed",new String[]{"Bed","Cake","Egg"});
	private final NumberValue range = new NumberValue("Range", 4.0, 0.0, 10.0, 1.0);
	private final NumberValue breakSpeed = new NumberValue("BreakSpeed", 2.0, 0.0, 3.0, 0.1);
	private final BooleanValue esp = new BooleanValue("ESP", true);
	private final BooleanValue instant = new BooleanValue("Instant", true);
	private final BooleanValue swing = new BooleanValue("Swing", false);
	private final BooleanValue rotate = new BooleanValue("Rotate", true);

	public int blockHitDelay = 0;
	public double targetPosX, targetPosY, targetPosZ;
	public float currentDamage = 0F;

	public Breaker() {
		super("Breaker", Category.WORLD);
		registerValues(mode,range, breakSpeed, esp, swing, rotate, instant);
	}

	@EventTarget
	public void onUpdate(EventPreUpdate event) {
		int radius = range.getValue().intValue();
		int x = -radius;
		while (x < radius) {
			int y = radius;
			while (y > -radius) {
				int z = -radius;
				while (z < radius) {
					int xPos = (int) mc.player.posX + x;
					int yPos = (int) mc.player.posY + y;
					int zPos = (int) mc.player.posZ + z;
					BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
					Block block = mc.world.getBlockState(blockPos).getBlock();
					boolean can = false;
					if (mode.isCurrentMode("Bed")) {
						if (block.getBlockState().getBlock() == Block.getBlockById(26)) {
							can = true;
						}
					} else if (mode.isCurrentMode("Cake")) {
						if (block.getBlockState().getBlock() == Block.getBlockById(92)) {
							can = true;
						}
					} else if (mode.isCurrentMode("Egg")) {
						if (block.getBlockState().getBlock() == Block.getBlockById(122)) {
							can = true;
						}
					}

					if (can) {
						if (instant.getValue()) {
							mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
							mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
						} else {
							if (blockHitDelay > 0) {
								blockHitDelay--;
								return;
							}
							if (currentDamage == 0F) {
								mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
							}
							currentDamage += block.getPlayerRelativeBlockHardness(mc.player, mc.world, blockPos);
							mc.world.sendBlockBreakProgress(mc.player.getEntityId(), blockPos, (int) ((currentDamage * 10F) - 1));

							if (currentDamage >= breakSpeed.getValue()) {
								mc.getNetHandler().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.DOWN));
								mc.playerController.onPlayerDestroyBlock(blockPos, EnumFacing.DOWN);
								blockHitDelay = 4;
								currentDamage = 0F;
							}
						}
						if (swing.getValue()) {
							mc.player.swingItem();
						} else {
							mc.getNetHandler().sendPacket(new C0APacketAnimation());
						}
						targetPosX = blockPos.getX();
						targetPosY = blockPos.getY();
						targetPosZ = blockPos.getZ();
						if (rotate.getValue()) {
							final float[] rotation = getRotations(blockPos, getClosestEnum(blockPos));
							event.setVisualYaw(rotation[0]);
							event.setVisualPitch(rotation[1]);
						}
					}
					++z;
				}
				--y;
			}
			++x;
		}
	}

	public static float[] getRotations(BlockPos block, EnumFacing face){
		double x = block.getX() + 0.5 - mc.player.posX + (double)face.getFrontOffsetX()/2;
		double z = block.getZ() + 0.5 - mc.player.posZ + (double)face.getFrontOffsetZ()/2;
		double d1 = mc.player.posY + mc.player.getEyeHeight() -(block.getY() + 0.5);
		double d3 = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
		if(yaw < 0.0F){
			yaw += 360f;
		}
		return  new float[]{yaw, pitch};
	}

	private EnumFacing getClosestEnum(BlockPos pos){
		EnumFacing closestEnum = EnumFacing.UP;
		float rotations = MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[0]);
		if(rotations >= 45 && rotations <= 135){
			closestEnum = EnumFacing.EAST;
		}else if((rotations >= 135 && rotations <= 180) ||
				(rotations <= -135 && rotations >= -180)){
			closestEnum = EnumFacing.SOUTH;
		}else if(rotations <= -45 && rotations >= -135){
			closestEnum = EnumFacing.WEST;
		}else if((rotations >= -45 && rotations <= 0) ||
				(rotations <= 45 && rotations >= 0)){
			closestEnum = EnumFacing.NORTH;
		}
		if (MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) > 75 ||
				MathHelper.wrapAngleTo180_float(getRotations(pos, EnumFacing.UP)[1]) < -75){
			closestEnum = EnumFacing.UP;
		}
		return closestEnum;
	}

	@EventTarget
	public void onRender3D(Event3D e) {
		if (esp.getValue()) {
			BlockPos blockPos2 = new BlockPos(targetPosX, targetPosY, targetPosZ);
			Block block = mc.world.getBlockState(blockPos2).getBlock();
			if (mc.player.getDistance(targetPosX, targetPosY, targetPosZ) > range.getValue() || block instanceof BlockAir) return;

			RenderUtils.drawBlockBox(blockPos2,RenderUtils.getRGB(255,0,0,50),true);
		}
	}
}
