package io.space.mod.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.Wrapper;
import io.space.events.Event3D;
import io.space.events.EventPacket;
import io.space.mod.Mod;
import io.space.value.values.BooleanValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.*;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class UHCFind extends Mod {
    public BooleanValue Field2280 = new BooleanValue("MobSpawner", true);
    public BooleanValue Field2281 = new BooleanValue("ZombiePlayer", true);
    public BooleanValue Field2282 = new BooleanValue("EnderMan", true);
    public BooleanValue Field2283 = new BooleanValue("Creeper", true);
    public BooleanValue Field2284 = new BooleanValue("Blaze", true);
    public BooleanValue Field2285 = new BooleanValue("Slime", true);
    public BooleanValue Field2286 = new BooleanValue("MagmaCube", true);

    public UHCFind() {
        super("UHCFind", Category.WORLD);
    }

    @EventTarget(value = Priority.HIGHEST)
    public void Method802(Event3D a) {
        List<Entity> var3 = mc.world.getLoadedEntityList();
        var3.sort(Comparator.comparingDouble(UHCFind::Method2246));
        int var4 = 0;

        for (Entity var6 : var3) {
            if (this.Field2282.getValue() && var6 instanceof EntityEnderman) {
                this.Method2245(var6, new Color(143, 0, 226), a.getPartialTicks());
            }

            if (this.Field2284.getValue() && var6 instanceof EntityBlaze) {
                this.Method2245(var6, new Color(239, 128, 2), a.getPartialTicks());
            }

            if (this.Field2285.getValue() && var6 instanceof EntitySlime) {
                this.Method2245(var6, new Color(41, 255, 0), a.getPartialTicks());
            }

            if (this.Field2286.getValue() && var6 instanceof EntityMagmaCube) {
                this.Method2245(var6, new Color(177, 22, 53), a.getPartialTicks());
            }

            if (this.Field2283.getValue() && var6 instanceof EntityCreeper && var4 < 2) {
                this.Method2245(var6, new Color(29, 156, 7), a.getPartialTicks());
                ++var4;
            }

            if (this.Field2281.getValue() && var6 instanceof EntityZombie && !(var6 instanceof EntityPigZombie) && var6.getDisplayName() != null && Objects.nonNull(((EntityZombie) var6).getEquipmentInSlot(4)) && ((EntityZombie) var6).getEquipmentInSlot(4).getItem() == Items.skull) {
                this.Method2245(var6, new Color(255, 0, 0, 255), a.getPartialTicks());
            }
        }

        if (this.Field2280.getValue()) {
            for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
                if (tileEntity instanceof TileEntityMobSpawner) {
                    GL11.glPushMatrix();
                    Method1126(2.0F);
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, a.getPartialTicks(), -1);
                    Method1129();
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, a.getPartialTicks(), -1);
                    Method1130();
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, a.getPartialTicks(), -1);
                    Method1131((new Color(0, 86, 255)).getRGB());
                    TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, a.getPartialTicks(), -1);
                    Method1132();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    @EventTarget
    public void Method273(EventPacket a) {
        if (a.getPacket() instanceof S0FPacketSpawnMob) {
            S0FPacketSpawnMob var3 = (S0FPacketSpawnMob) a.getPacket();
            EntityLivingBase var4 = (EntityLivingBase) EntityList.createEntityByID(var3.getEntityType(), mc.world);
            double var5 = (double) var3.getX() / 32.0;
            double var7 = (double) var3.getY() / 32.0;
            double var9 = (double) var3.getZ() / 32.0;
            float var11 = (float) (var3.getYaw() * 360) / 256.0F;
            float var12 = (float) (var3.getPitch() * 360) / 256.0F;
            var4.serverPosX = var3.getX();
            var4.serverPosY = var3.getY();
            var4.serverPosZ = var3.getZ();
            var4.renderYawOffset = var4.rotationYawHead = (float) (var3.getHeadPitch() * 360) / 256.0F;
            Entity[] var13 = var4.getParts();
            int var14 = var3.getEntityID() - var4.getEntityId();
            int var15 = 0;
            if (var15 < var13.length) {
                var13[var15].setEntityId(var13[var15].getEntityId() + var14);
                ++var15;
            }

            var4.setEntityId(var3.getEntityID());
            var4.setPositionAndRotation(var5, var7, var9, var11, var12);
            var4.motionX = (float) var3.getVelocityX() / 8000.0F;
            var4.motionY = (float) var3.getVelocityY() / 8000.0F;
            var4.motionZ = (float) var3.getVelocityZ() / 8000.0F;
            List<DataWatcher.WatchableObject> var22 = var3.func_149027_c();
            var4.getDataWatcher().updateWatchedObjectsFromList(var22);
            var15 = var3.getX() / 32;
            int var16 = var3.getY() / 32;
            int var17 = var3.getZ() / 32;
            float var18 = (float) (mc.player.posX - (double) var15);
            float var19 = (float) (mc.player.posY - (double) var16);
            float var20 = (float) (mc.player.posZ - (double) var17);
            float var21 = MathHelper.sqrt_float(var18 * var18 + var19 * var19 + var20 * var20);
            if (this.Field2282.getValue() && var4 instanceof EntityEnderman) {
                Wrapper.sendMessage("§3Find §9" + var4.getName() + "§e " + (int) var21 + "§7m §BX: §r" + var15 + " §BY: §r" + var16 + " §BZ: §r" + var17);
            }

            if (this.Field2286.getValue() && var4 instanceof EntityMagmaCube) {
                Wrapper.sendMessage("§3Find §9" + var4.getName() + "§e " + (int) var21 + "§7m §BX: §r" + var15 + " §BY: §r" + var16 + " §BZ: §r" + var17);
            }

            if (this.Field2284.getValue() && var4 instanceof EntityBlaze) {
                Wrapper.sendMessage("§3Find §9" + var4.getName() + "§e " + (int) var21 + "§7m §BX: §r" + var15 + " §BY: §r" + var16 + " §BZ: §r" + var17);
            }

            if (this.Field2281.getValue() && var4 instanceof EntityZombie && !(var4 instanceof EntityPigZombie) && var4.getDisplayName() != null && !var4.getDisplayName().getUnformattedText().equalsIgnoreCase("Zombie") && !var4.getDisplayName().getUnformattedText().equalsIgnoreCase("僵尸") && !var4.getDisplayName().getFormattedText().startsWith("§")) {
                Wrapper.sendMessage("§3Find §9" + var4.getDisplayName().getFormattedText() + " Exit! §e " + (int) var21 + "§7m §BX: §r" + var15 + " §BY: §r" + var16 + " §BZ: §r" + var17);
            }
        }
    }

    public void Method2245(Entity a, Color color, float partialTicks) {
        double var4 = a.lastTickPosX + (a.posX - a.lastTickPosX) * (double)partialTicks - mc.getRenderManager().renderPosX;
        double var6 = a.lastTickPosY + (a.posY - a.lastTickPosY) * (double)partialTicks - mc.getRenderManager().renderPosY;
        double var8 = a.lastTickPosZ + (a.posZ - a.lastTickPosZ) * (double)partialTicks - mc.getRenderManager().renderPosZ;
        double var10 = (double)a.width / 1.5;
        double var12 = a.getEntityBoundingBox().maxY - a.getEntityBoundingBox().minY;
        GL11.glPushMatrix();
        Method1126(2.0F);
        GL11.glDisable(2848);
        Method1124(new AxisAlignedBB(var4 - var10, var6, var8 - var10, var4 + var10, var6 + var12, var8 + var10));
        Method1129();
        Method1124(new AxisAlignedBB(var4 - var10, var6, var8 - var10, var4 + var10, var6 + var12, var8 + var10));
        Method1130();
        Method1124(new AxisAlignedBB(var4 - var10, var6, var8 - var10, var4 + var10, var6 + var12, var8 + var10));
        Method1131(color.getRGB());
        Method1124(new AxisAlignedBB(var4 - var10, var6, var8 - var10, var4 + var10, var6 + var12, var8 + var10));
        Method1132();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public static void Method1126(float a) {
        Method1127();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(a);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void Method1127() {
        Framebuffer var0 = Minecraft.getMinecraft().getFramebuffer();
        if (var0.depthBuffer > -1) {
            Method1128(var0);
            var0.depthBuffer = -1;
        }
    }

    public static void Method1128(Framebuffer a) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(a.depthBuffer);
        int var1 = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, var1);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, var1);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, var1);
    }

    public static void Method1132() {
        GL11.glPolygonOffset(1.0F, 2000000.0F);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public static void Method1124(AxisAlignedBB a) {
        Tessellator var1 = Tessellator.getInstance();
        WorldRenderer var2 = var1.getWorldRenderer();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var1.draw();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var1.draw();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var1.draw();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var1.draw();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var1.draw();
        var2.begin(7, DefaultVertexFormats.POSITION);
        var2.pos(a.minX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.minX, a.minY, a.maxZ).endVertex();
        var2.pos(a.minX, a.maxY, a.minZ).endVertex();
        var2.pos(a.minX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.minZ).endVertex();
        var2.pos(a.maxX, a.minY, a.minZ).endVertex();
        var2.pos(a.maxX, a.maxY, a.maxZ).endVertex();
        var2.pos(a.maxX, a.minY, a.maxZ).endVertex();
        var1.draw();
    }

    public static void Method1131(int a) {
        float var1 = (float)(a >> 24 & 255) / 255.0F;
        float var2 = (float)(a >> 16 & 255) / 255.0F;
        float var3 = (float)(a >> 8 & 255) / 255.0F;
        float var4 = (float)(a & 255) / 255.0F;
        GL11.glColor4f(var2, var3, var4, var1 == 0.0F ? 1.0F : var1);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0F, -2000000.0F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static void Method1130() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void Method1129() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    private static double Method2246(Entity a) {
        return mc.player.getDistanceToEntity(a);
    }
}
