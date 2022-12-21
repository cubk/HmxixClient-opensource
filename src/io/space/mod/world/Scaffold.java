package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.events.*;
import io.space.mod.Mod;
import io.space.object.SmoothRotationObject;
import io.space.renderer.font.FontManager;
import io.space.utils.*;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import utils.hodgepodge.object.time.TimerUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("DuplicatedCode")
public final class Scaffold extends Mod {
    private final ModeValue rotationMode = new ModeValue("RotationMode","Normal",new String[]{"Normal","StaticHead","Head","Facing","NCP","Back","Null"});
    private final ModeValue itemMode = new ModeValue("ItemMode","Spoof",new String[]{"Spoof","Switch"});
    private final ModeValue towerMode = new ModeValue("TowerMode","Vanilla",new String[]{"Vanilla","Hypixel","TP","Low","Slow"});
    private final ModeValue placeTiming = new ModeValue("PlaceTiming","Post",new String[]{"Post","Pre","Tick"});
    private final ModeValue eagleMode = new ModeValue("EagleMode","Key",new String[]{"Key","Packet"});
    private final NumberValue expand = new NumberValue("Expand",0.0,0.0,10.0,0.1);
    private final NumberValue rotationSpeed = new NumberValue("RotationSpeed",180.0,0.0,180.0,1.0);
    private final NumberValue slowMoveValue = new NumberValue("SlowMoveValue",0.2,0.0,0.3,0.01);
    private final NumberValue delayValue = new NumberValue("Delay",0.0,0.0,2000.0,1.0);
    private final BooleanValue tower = new BooleanValue("Tower", true);
    private final BooleanValue moveTower = new BooleanValue("MoveTower", true);
    private final BooleanValue swing = new BooleanValue("Swing", true);
    private final BooleanValue noSprint = new BooleanValue("NoSprint", false);
    private final BooleanValue keepY = new BooleanValue("KeepY", false);
    private final BooleanValue swap = new BooleanValue("Swap", true);
    private final BooleanValue slowMove = new BooleanValue("SlowMove", false);
    public static final BooleanValue safeWalk = new BooleanValue("SafeWalk", false);
    private final BooleanValue eagle = new BooleanValue("Eagle", false);
    private final BooleanValue down = new BooleanValue("Down", false);

    public static Scaffold Instance;

    private final TimerUtils delayTimerUtils = new TimerUtils(true);

    private final SmoothRotationObject rotationObject = new SmoothRotationObject();
    private float saveYaw,savePitch;
    private BlockData blockData;
    private int startSlot;
    private double startY;

    public Scaffold() {
        super("Scaffold",Category.WORLD);
        registerValues(rotationMode,itemMode,towerMode,placeTiming,eagleMode,expand,rotationSpeed,slowMoveValue,delayValue,tower,moveTower,swing,noSprint,keepY,swap, slowMove,safeWalk,eagle,down);
        Instance = this;
    }

    @EventTarget(value = Priority.LOWEST)
    public void onTick(EventTick e) {
        if (noSprint.getValue()) mc.player.setSprinting(false);

        if (placeTiming.isCurrentMode("Tick")) {
            place();
        }
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        blockData = null;

        if (rotationMode.isCurrentMode("StaticHead")) {
            saveYaw = mc.player.rotationYaw;
            savePitch = 79.44f;
        } else if (rotationMode.isCurrentMode("Head")) {
            saveYaw = mc.player.rotationYaw;
        } else if (rotationMode.isCurrentMode("Null")) {
            savePitch = mc.player.rotationPitch;
            saveYaw = mc.player.rotationYaw;
        } else if (rotationMode.isCurrentMode("Back")) {
            saveYaw = mc.player.rotationYaw - 180f;
        }

        if (rotationSpeed.getValue().equals(180.0)) {
            e.setYaw(saveYaw);
            e.setPitch(savePitch);

            mc.player.rotationYawHead = saveYaw;
            mc.player.prevRotationYawHead = saveYaw;
            mc.player.renderYawOffset = saveYaw;
            mc.player.prevRenderYawOffset = saveYaw;
            mc.player.rotationPitchHead = savePitch;
            mc.player.prevRotationPitchHead = savePitch;
        } else {
            rotationObject.setWillYawPitch(saveYaw,savePitch);
            rotationObject.handleRotation(rotationSpeed.getValue());
            rotationObject.setPlayerRotation(e);
        }

        if (rotationMode.isCurrentMode("Head")) {
            savePitch = mc.player.rotationPitch;
        } else if (rotationMode.isCurrentMode("NCP")) {
            savePitch = mc.player.rotationPitch;
            saveYaw = mc.player.rotationYaw;
        }

        double x = mc.player.posX;
        double z = mc.player.posZ;
        double y = mc.player.posY;

        double yOffset = 1;

        final boolean shouldDown = down.getValue() && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

        if (shouldDown) {
            yOffset = 2;
        }

        if (keepY.getValue()) {
            if (mc.player.isMoving()) {
                y = startY;
            } else {
                startY = mc.player.posY;
            }
        }

        if (!expand.getValue().equals(0.0) && !mc.player.isCollidedHorizontally && !shouldDown) {
            double[] coords = getExpandCoords(x,z,mc.player.movementInput.moveForward,mc.player.movementInput.moveStrafe,mc.player.rotationYaw);
            x = coords[0];
            z = coords[1];
        }

        if (isAirBlock(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - yOffset, mc.player.posZ)).getBlock())) {
            x = mc.player.posX;
            z = mc.player.posZ;
        }

        final boolean hasBlock = getInventoryBlockSize() > 0;

        if (hasBlock) {
            BlockPos underPos = new BlockPos(x, y - yOffset, z);
            Block underBlock = mc.world.getBlock(underPos);

            final BlockData data = getBlockData(underPos,shouldDown);

            if (isAirBlock(underBlock) && data != null) {
                if (eagle.getValue() && eagleMode.isCurrentMode("Key")) {
                    mc.player.movementInput.sneak = true;
                    mc.player.setSprinting(false);
                    MoveUtils.setSpeed(0);
                }

                blockData = data;
                final float[] rotations = getRotationsBlock(data.position, data.face);

                if (rotationMode.isCurrentMode("Normal") || rotationMode.isCurrentMode("NCP")) {
                    final float yaw = rotations[0];
                    final float pitch = rotations[1];
                    saveYaw = yaw;
                    savePitch = pitch;
                } else if (rotationMode.isCurrentMode("Head")) {
                    savePitch = 79.44f;
                } else if (rotationMode.isCurrentMode("Facing")) {
                    final float yaw = getYawByFacing(data.face);
                    final float pitch = rotations[1];
                    saveYaw = yaw;
                    savePitch = pitch;
                } else if (rotationMode.isCurrentMode("Back")) {
                    savePitch = rotations[1];
                }
            }
        }

        label:
        {
            if (hasBlock && mc.gameSettings.keyBindJump.isKeyDown() && (tower.getValue() || moveTower.getValue())) {
                if (tower.getValue()) {
                    if (!moveTower.getValue())
                        if (mc.player.isMoving()) break label;
                } else if (moveTower.getValue() && !mc.player.isMoving()) break label;

                if (!mc.player.isMoving()) {
                    MoveUtils.setSpeed(0);
                }

                if (towerMode.isCurrentMode("Hypixel") || (moveTower.getValue() && mc.player.isMoving())) {
                    if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                    {
                        mc.player.motionX = mc.player.motionX * 0.6;
                        mc.player.motionZ = mc.player.motionZ * 0.6;
                    }
                    if (mc.player.onGround) {
                        int posX0 = (int) mc.player.posX;
                        if (mc.player.posX < posX0) {
                            posX0 -= 1;
                        }

                        int posZ0 = (int) mc.player.posZ;
                        if (mc.player.posZ < posZ0) {
                            posZ0 -= 1;
                        }
                        mc.player.setPosition(posX0 + 0.5, mc.player.posY, posZ0 + 0.5);
                    }

                    if (PlayerUtils.isOnGround(0.76D) && !PlayerUtils.isOnGround(0.75D) && mc.player.motionY > 0.23D && mc.player.motionY < 0.25D) {
                        mc.player.motionY = ((double) Math.round(mc.player.posY) - mc.player.posY);
                    }

                    if (PlayerUtils.isOnGround(1.0E-4D)) {
                        mc.player.motionY = 0.41999998688698;
                    } else if (mc.player.posY >= (double) Math.round(mc.player.posY) - 1.0E-4D && mc.player.posY <= (double) Math.round(mc.player.posY) + 1.0E-4D) {
                        mc.player.motionY = 0;
                    }

                    if (towerMode.isCurrentMode("Hypixel") && !mc.player.isMoving()) {
                        if (mc.world.getBlock(new BlockPos(mc.player).add(0, 2, 0)) instanceof BlockAir) {
                            double var3 = e.getY() % 1.0D;
                            double var4 = MathHelper.floor_double(mc.player.posY);
                            double[] offsets = new double[]{0.41999998688698, 0.7531999805212};
                            if (var3 > 0.419D && var3 < 0.753D) {
                                e.setY(var4 + offsets[0]);
                            } else if (var3 > 0.753D) {
                                e.setY(var4 + offsets[1]);
                            } else {
                                e.setY(var4);
                            }

                            e.setX(e.getX() + (mc.player.ticksExisted % 2 == 0 ? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D) : -ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)));
                            e.setZ(e.getZ() + (mc.player.ticksExisted % 2 != 0 ? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D) : -ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)));
                        }
                    }
                } else if (towerMode.isCurrentMode("Vanilla")) {
                    mc.player.motionY = 0.41982;
                    MoveUtils.setSpeed(0);
                } else if (towerMode.isCurrentMode("TP")) {
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1, mc.player.posZ);

                    if (!PlayerUtils.isOnGround(1)) {
                        mc.player.motionY = 0;
                    }

                    MoveUtils.setSpeed(0);
                } else if (towerMode.isCurrentMode("Low")) {
                    if (PlayerUtils.isOnGround(0.99)) {
                        mc.player.motionY = 0.36;
                        MoveUtils.setSpeed(0);
                    }
                } else if (towerMode.isCurrentMode("Slow")) {
                    if (PlayerUtils.isOnGround(0.114514)) {
                        mc.player.jumpNoEvent();
                        MoveUtils.setSpeed(0);
                    }
                }
            }
        }

        if (placeTiming.isCurrentMode("Pre")) {
            place();
        }
    }

    @EventTarget(value = Priority.LOW)
    public void onMove(EventMove e) {
        if (slowMove.getValue()) {
            MoveUtils.setSpeedEvent(e, slowMoveValue.getValue());
        }
    }

    @EventTarget
    public void onPostUpdate(EventPostUpdate e) {
        if (placeTiming.isCurrentMode("Post")) {
            place();
        }
    }

    private void place() {
        if (getInventoryBlockSize() > 0 && blockData != null) {
            if (swap.getValue()) {
                getBlock(getBestSpoofSlot());
            }

            if (delayValue.getValue().intValue() == 0 || delayTimerUtils.hasReached(delayValue.getValue())) {
                final int slot = getBlockFromHotBar();
                if (slot != -1) {
                    final boolean sendSneakPacket = eagle.getValue() && eagleMode.isCurrentMode("Packet");

                    if (sendSneakPacket) {
                        mc.getNetHandler().sendPacket(new C0BPacketEntityAction(mc.player,C0BPacketEntityAction.Action.START_SNEAKING));
                    }

                    final int old = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = slot;

                    if (mc.playerController.onPlayerRightClick(mc.player, mc.world, mc.player.getCurrentEquippedItem(), blockData.position, blockData.face, PositionUtils.getVec3(blockData.position,blockData.face))) {
                        if (swing.getValue()) {
                            mc.player.swingItem();
                        } else mc.getNetHandler().sendPacket(new C0APacketAnimation());
                    }

                    if (itemMode.isCurrentMode("Spoof")) {
                        mc.player.inventory.currentItem = old;
                    }

                    if (sendSneakPacket) {
                        mc.getNetHandler().sendPacket(new C0BPacketEntityAction(mc.player,C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
    }

    @EventTarget
    public void on2D(Event2D e) {
        final int width = e.getScaledResolution().getScaledWidth();
        final int height = e.getScaledResolution().getScaledHeight();
        final int middleX = width / 2 + 15;
        final int middleY = height / 2 - 12;
        final int block = getInventoryBlockSize();
        if (block != 0) {
            final ItemStack stackInSlot = mc.player.inventory.getStackInSlot(getBlockFromHotBar());
            final String displayName;
            if (stackInSlot == null) {
                displayName = "NULL";
            } else displayName = stackInSlot.getDisplayName();
            FontManager.default16.drawStringWithShadow((block >= 64 ? EnumChatFormatting.YELLOW : EnumChatFormatting.RED) + "Blocks:" + block + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GRAY + displayName + EnumChatFormatting.WHITE + ")", middleX - 50, middleY + 20, -1);
        } else {
            FontManager.default16.drawStringWithShadow(EnumChatFormatting.RED + "Blocks:" + block + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GRAY + "NULL" + EnumChatFormatting.WHITE + ")", middleX - 50, middleY + 20, -1);
        }
    }

    @Override
    protected void onEnable() {
        if (itemMode.isCurrentMode("Switch")) {
            startSlot = mc.player.inventory.currentItem;
        }
        startY = mc.player.posY;
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        if (itemMode.isCurrentMode("Switch")) {
            mc.player.inventory.currentItem = startSlot;
        }
        blockData = null;
        super.onDisable();
    }

    private float getYawByFacing(EnumFacing facing) {
        switch (facing) {
            case DOWN:
            case UP:
            case NORTH:
                return 0;
            case SOUTH:
                return 180;
            case WEST:
                return -90;
            case EAST:
                return 90;
        }

        return 0.0f;
    }

    private float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double)block.getX() + 0.5 - mc.player.posX + (double)face.getFrontOffsetX() / 2.0;
        double z = (double)block.getZ() + 0.5 - mc.player.posZ + (double)face.getFrontOffsetZ() / 2.0;
        double y = (double)block.getY() + 0.5;
        double d1 = mc.player.posY + (double)mc.player.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

    private int getBlockFromHotBar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack == null) continue;
            if (!isValidItem(itemStack.getItem())) continue;
            slot = i;
            break;
        }

        return slot;
    }

    private void getBlock(int hotBarSlot) {
        if (getBlockFromHotBar() == -1) {
            for (int i = 0; i < 36; ++i) {
                if (!mc.player.inventoryContainer.getSlot(i).getHasStack() || mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory))
                    continue;
                ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
                if (!(is.getItem() instanceof ItemBlock) || !isValidItem(is.getItem())) continue;
                if (36 + hotBarSlot == i) break;

                swap(i, hotBarSlot);

                break;
            }
        }
    }

    private int getInventoryBlockSize() {
        int final_ = 0;

        for (ItemStack stack : mc.player.inventory.mainInventory) {
            if (stack == null) continue;
            if (!(stack.getItem() instanceof ItemBlock) || !isValidItem(stack.getItem())) continue;
            final_ += stack.stackSize;
        }

        return final_;
    }

    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW){
        BlockPos underPos = new BlockPos(x, mc.player.posY - 1, z);
        Block underBlock = mc.world.getBlockState(underPos).getBlock();
        double xCalc = -999, zCalc = -999;
        double dist = 0;
        double expandDist = expand.getValue() * 2;
        while(!isAirBlock(underBlock)){
            xCalc = x;
            zCalc = z;
            dist ++;
            if(dist > expandDist){
                dist = expandDist;
            }
            final double cos = Math.cos(Math.toRadians(YAW + 90.0f));
            final double sin = Math.sin(Math.toRadians(YAW + 90.0f));
            xCalc += (forward * 0.45 * cos + strafe * 0.45 * sin) * dist;
            zCalc += (forward * 0.45 * sin - strafe * 0.45 * cos) * dist;
            if(dist == expandDist){
                break;
            }
            underPos = new BlockPos(xCalc, mc.player.posY - 1, zCalc);
            underBlock = mc.world.getBlockState(underPos).getBlock();
        }
        return new double[]{xCalc,zCalc};
    }

    private boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow);
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidItem(Item item) {
        if (item instanceof ItemBlock) {
            ItemBlock iBlock = (ItemBlock)item;
            Block block = iBlock.getBlock();
            return !invalidBlocks.contains(block);
        }
        return false;
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot1, hotbarSlot,2, mc.player);
    }

    private int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (mc.player.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }

    private BlockData getBlockData(BlockPos pos,boolean down) {
        if (down) {
            if (isPosSolid(pos.add(0, 1, 0))) {
                return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
            }
        }

        if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (isPosSolid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (isPosSolid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (isPosSolid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (isPosSolid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || (block.getMaterial().blocksMovement() && block.isFullCube()) || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }

    private static class BlockData {
        private final BlockPos position;
        private final EnumFacing face;

        private BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table,
            Blocks.furnace,
            Blocks.carpet,
            Blocks.command_block,
            Blocks.crafting_table,
            Blocks.chest,
            Blocks.trapped_chest,
            Blocks.ender_chest,
            Blocks.dispenser,
            Blocks.air,
            Blocks.water,
            Blocks.flowing_water,
            Blocks.lava,
            Blocks.flowing_lava,
            Blocks.sand,
            Blocks.snow_layer,
            Blocks.torch,
            Blocks.anvil,
            Blocks.jukebox,
            Blocks.wooden_button,
            Blocks.stone_button,
            Blocks.lever,
            Blocks.noteblock,
            Blocks.wooden_pressure_plate,
            Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate,
            Blocks.heavy_weighted_pressure_plate,
            Blocks.red_mushroom,
            Blocks.brown_mushroom,
            Blocks.red_flower,
            Blocks.yellow_flower,
            Blocks.ladder,
            Blocks.web,
            Blocks.beacon
    );
}
