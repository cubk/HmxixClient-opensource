package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.events.EventBlockRenderSide;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.block.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedList;

public final class XRay extends Mod {
    private final ModeValue antiFakeMineralsMode = new ModeValue("AntiFakeMineralsMode","Normal",new String[]{"Normal","FoodByte"});
    public static final NumberValue alpha = new NumberValue("Alpha",150.0,0.0,255.0,1.0);
    public final NumberValue range = new NumberValue("Range",50.0,0.0,500.0,1.0);
    private final BooleanValue esp = new BooleanValue("ESP",true);
    private final BooleanValue block = new BooleanValue("Block",false);
    private final BooleanValue coal = new BooleanValue("Coal",false);
    private final BooleanValue iron = new BooleanValue("Iron",true);
    private final BooleanValue gold = new BooleanValue("Gold", true);
    private final BooleanValue lapisLazuli = new BooleanValue("LapisLazuli",false);
    private final BooleanValue redStone = new BooleanValue("RedStone",true);
    private final BooleanValue diamond = new BooleanValue("Diamond",true);
    private final BooleanValue emerald = new BooleanValue("Emerald",true);
    private final BooleanValue quartz = new BooleanValue("Quartz",false);
    private final BooleanValue water = new BooleanValue("Water",false);
    private final BooleanValue lava = new BooleanValue("Lava",false);
    private final BooleanValue antiFakeMinerals = new BooleanValue("AntiFakeMinerals",false);

    public static XRay Instance;

    public static final LinkedList<Integer> antiXRayBlocks = new LinkedList<>();
    private final ArrayList<BlockPos> xRayBlocks = new ArrayList<>();
    private BlockPos[] xRayBlocksCopy = new BlockPos[0];

    public XRay() {
        super("XRay",Category.WORLD);
        registerValues(antiFakeMineralsMode,alpha,range,esp,block,coal,iron,gold,lapisLazuli,redStone,diamond,emerald,quartz,water,lava,antiFakeMinerals);
        Instance = this;
    }

    @EventTarget
    public void onEventBlockRenderSide(EventBlockRenderSide e) {
        for (int id : antiXRayBlocks) {
            if (Block.getIdFromBlock(e.getBlock()) == id) {
                if (antiFakeMinerals.getValue()) {
                    e.setShouldRender(false);

                    if (antiFakeMineralsMode.isCurrentMode("Normal")) {
                        @SuppressWarnings("SimplifiableConditionalExpression")
                        final boolean base = e.getSide() == EnumFacing.DOWN && e.getMinY() > 0.0D ? true : (e.getSide() == EnumFacing.UP && e.getMaxY() < 1.0D ? true : (e.getSide() == EnumFacing.NORTH && e.getMinZ() > 0.0D ? true : (e.getSide() == EnumFacing.SOUTH && e.getMaxZ() < 1.0D ? true : (e.getSide() == EnumFacing.WEST && e.getMinX() > 0.0D ? true : (e.getSide() == EnumFacing.EAST && e.getMaxX() < 1.0D ? true : !e.getBlockAccess().getBlockState(e.getPos()).getBlock().isOpaqueCube())))));

                        e.setShouldRender(base);

                        if (!base) continue;
                    } else if (antiFakeMineralsMode.isCurrentMode("Hypixel")) {
                        boolean needToContinue = true;

                        for (EnumFacing facing : EnumFacing.values()) {
                            for (int i = 1; i < 3; i++) {
                                final Block block = mc.world.getBlock(e.getPos().offset(facing, i));
                                if (block instanceof BlockAir || block instanceof BlockLiquid) {
                                    e.setShouldRender(true);
                                    needToContinue = false;
                                    break;
                                }
                            }
                        }

                        if (needToContinue) continue;
                    } else {
                        e.setShouldRender(true);
                    }
                }

                if (!esp.getValue()) continue;

                double xDiff = mc.player.posX - e.getPos().getX();
                double zDiff = mc.player.posZ - e.getPos().getZ();
                double dis = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

                if (dis > range.getValue()) {
                    continue;
                }

                int x = Math.round(e.getPos().offset(e.getSide(),-1).getX());
                int y = Math.round(e.getPos().offset(e.getSide(),-1).getY());
                int z = Math.round(e.getPos().offset(e.getSide(), -1).getZ());

                if (xRayBlocks.stream().anyMatch((blockPos -> blockPos.getX() == x && blockPos.getY() == y && blockPos.getZ() == z))) {
                    continue;
                }

                xRayBlocks.add(new BlockPos(x, y, z));
                xRayBlocksCopy = xRayBlocks.toArray(xRayBlocksCopy);
            }
        }
    }

    @EventTarget
    public void on3D(Event3D e) {
        if (!esp.getValue()) return;

        if (xRayBlocksCopy == null) return;

        for (BlockPos currentPos : xRayBlocksCopy) {
            final Block block = mc.world.getBlock(currentPos);

            if (!(block instanceof BlockOre || block instanceof BlockRedstoneOre)) {
                continue;
            }

            int color = RenderUtils.getRGB(12, 12, 12);

            switch (block.getUnlocalizedName()) {
                case "tile.blockEmerald":
                case "tile.oreEmerald":
                    color = RenderUtils.getRGB(0, 255, 0);
                    break;
                case "tile.blockGold":
                case "tile.oreGold":
                    color = RenderUtils.getRGB(0xFFFF00);
                    break;
                case "tile.blockIron":
                case "tile.oreIron":
                    color = RenderUtils.getRGB(210, 210, 210);
                    break;
                case "tile.blockLapis":
                case "tile.oreLapis":
                    color = RenderUtils.getRGB(0x0000FF);
                    break;
                case "tile.blockRedstone":
                case "tile.oreRedstone":
                    color = RenderUtils.getRGB(0xFF0000);
                    break;
                case "tile.blockDiamond":
                case "tile.oreDiamond":
                    color = RenderUtils.getRGB(0, 255, 255);
                    break;
                case "tile.netherquartz":
                    color = RenderUtils.getRGB(255, 255, 255);
                    break;
            }

            double x = (currentPos.getX() - mc.getRenderManager().renderPosX);
            double y = (currentPos.getY() - mc.getRenderManager().renderPosY);
            double z = (currentPos.getZ() - mc.getRenderManager().renderPosZ);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(1.0F);
            RenderUtils.glColor(color);
            RenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void onEnable() {
        mc.renderGlobal.loadRenderers();
        int posX = (int)mc.player.posX;
        int posY = (int)mc.player.posY;
        int posZ = (int)mc.player.posZ;
        mc.renderGlobal.markBlockRangeForRenderUpdate(posX - 900,posY - 900,posZ - 900,posX + 900,posY + 900,posZ + 900);
        addAntiXRayBlocks();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.renderGlobal.loadRenderers();
        antiXRayBlocks.clear();
        xRayBlocks.clear();
        xRayBlocksCopy = new BlockPos[0];
        super.onDisable();
    }

    private void addAntiXRayBlocks() {
        if (coal.getValue()) {
            antiXRayBlocks.add(16);
            if (block.getValue()) antiXRayBlocks.add(173);
        }
        if (iron.getValue()) {
            antiXRayBlocks.add(15);
            if (block.getValue()) antiXRayBlocks.add(42);
        }
        if (gold.getValue()) {
            antiXRayBlocks.add(14);
            if (block.getValue()) antiXRayBlocks.add(41);
        }
        if (lapisLazuli.getValue()) {
            antiXRayBlocks.add(21);
            if (block.getValue()) antiXRayBlocks.add(22);
        }
        if (diamond.getValue()) {
            antiXRayBlocks.add(56);
            if (block.getValue()) antiXRayBlocks.add(57);
        }
        if (redStone.getValue()) {
            antiXRayBlocks.add(73);
            antiXRayBlocks.add(74);
            if (block.getValue()) antiXRayBlocks.add(152);
        }
        if (emerald.getValue()) {
            antiXRayBlocks.add(129);
            if (block.getValue()) antiXRayBlocks.add(133);
        }
        if (quartz.getValue()) {
            antiXRayBlocks.add(153);
        }
        if (water.getValue()) {
            antiXRayBlocks.add(8);
            antiXRayBlocks.add(9);
        }
        if (lava.getValue()) {
            antiXRayBlocks.add(10);
            antiXRayBlocks.add(11);
        }
    }
}
