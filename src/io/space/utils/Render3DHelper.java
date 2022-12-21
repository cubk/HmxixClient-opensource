package io.space.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.lang.reflect.Field;

public class Render3DHelper {
    static Minecraft mc = Minecraft.getMinecraft();

    public static void drawBlockESP(final BlockPos targetBlockPos, final int color) {
        if (targetBlockPos == null) {
            return;
        }
        final double x = targetBlockPos.getX() - mc.getRenderManager().viewerPosX;
        final double y = targetBlockPos.getY() - mc.getRenderManager().viewerPosY;
        final double z = targetBlockPos.getZ() - mc.getRenderManager().viewerPosZ;
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        GL11.glColor4d(r, g, b, a);
        RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
        drawAABB(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), r, g, b);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
    }

    public static Timer getTimer() {
        try {
            final Class<Minecraft> c = Minecraft.class;
            final Field f = c.getDeclaredField(new String(new char[]{'t', 'i', 'm', 'e', 'r'}));
            f.setAccessible(true);
            return (Timer) f.get(mc);
        } catch (Exception er) {
            try {
                final Class<Minecraft> c2 = Minecraft.class;
                final Field f2 = c2.getDeclaredField(new String(new char[]{'f', 'i', 'e', 'l', 'd', '_', '7', '1', '4', '2', '8', '_', 'T'}));
                f2.setAccessible(true);
                return (Timer) f2.get(mc);
            } catch (Exception er2) {
                return null;
            }
        }
    }



    public static void dGR(int left, int top, int right, int bottom, int startColor, int endColor) {
        int j;
        if (left < right) {
            j = left;
            left = right;
            right = j;
        }

        if (top < bottom) {
            j = top;
            top = bottom;
            bottom = j;
        }

        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 0.0D).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawXrayBlock (BlockPos bp, int color) {
        if (bp != null) {
            double x = (double) bp.getX() - mc.getRenderManager().viewerPosX;
            double y = (double) bp.getY() - mc.getRenderManager().viewerPosY;
            double z = (double) bp.getZ() - mc.getRenderManager().viewerPosZ;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            float a = (float) (color >> 24 & 255) / 255.0F;
            float r = (float) (color >> 16 & 255) / 255.0F;
            float g = (float) (color >> 8 & 255) / 255.0F;
            float b = (float) (color & 255) / 255.0F;
            GL11.glColor4d(r, g, b, a);
            RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D));

            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }

    public static void drawESP(final Entity e, int color, final boolean damage, final int type) {
        if (e == null) {
            return;
        }
        final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY;
        final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;
        if (e instanceof EntityPlayer && damage && ((EntityPlayer) e).hurtTime != 0) {
            color = Color.RED.getRGB();
        }
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        if (type == 1) {
            GlStateManager.pushMatrix();
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(3.0f);
            GL11.glColor4f(r, g, b, a);
            RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(e.getEntityBoundingBox().minX - 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().minY - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().minZ - 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ), e.getEntityBoundingBox().maxX + 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().maxY + 0.1 - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().maxZ + 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ)));
            drawAABB(new AxisAlignedBB(e.getEntityBoundingBox().minX - 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().minY - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().minZ - 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ), e.getEntityBoundingBox().maxX + 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().maxY + 0.1 - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().maxZ + 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ)), r, g, b);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GlStateManager.popMatrix();
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (type == 2 || type == 3) {
            final boolean mode = type == 2;
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glLineWidth(3.0f);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4d(r, g, b, a);
            if (mode) {
                RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(e.getEntityBoundingBox().minX - 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().minY - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().minZ - 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ), e.getEntityBoundingBox().maxX + 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().maxY + 0.1 - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().maxZ + 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ)));
            } else {
                drawAABB(new AxisAlignedBB(e.getEntityBoundingBox().minX - 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().minY - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().minZ - 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ), e.getEntityBoundingBox().maxX + 0.05 - e.posX + (e.posX - mc.getRenderManager().viewerPosX), e.getEntityBoundingBox().maxY + 0.1 - e.posY + (e.posY - mc.getRenderManager().viewerPosY), e.getEntityBoundingBox().maxZ + 0.05 - e.posZ + (e.posZ - mc.getRenderManager().viewerPosZ)), r, g, b);
            }
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        } else if (type == 4) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y - 0.2, z);
            GL11.glScalef(0.03f, 0.03f, 0.03f);
            GL11.glRotated(- mc.getRenderManager().playerViewY, 0.0, 1.0, 0.0);
            GlStateManager.disableDepth();
            Gui.drawRect(- 20, - 1, - 26, 25, Color.black.getRGB());
            Gui.drawRect(- 21, 0, - 25, 24, color);
            Gui.drawRect(20, - 1, 26, 25, Color.black.getRGB());
            Gui.drawRect(21, 0, 25, 24, color);
            Gui.drawRect(- 20, - 1, 21, 5, Color.black.getRGB());
            Gui.drawRect(- 21, 0, 24, 4, color);
            Gui.drawRect(- 20, 20, 21, 25, Color.black.getRGB());
            Gui.drawRect(- 21, 21, 25, 24, color);
            GlStateManager.enableDepth();
            GL11.glPopMatrix();
        }
    }

    public static void drawAABB(final AxisAlignedBB aabb, final float r, final float g, final float b) {
        final float a = 0.25f;
        final Tessellator ts = Tessellator.getInstance();
        final WorldRenderer vb = ts.getWorldRenderer();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        ts.draw();
        vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.minX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.minZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(r, g, b, a).endVertex();
        vb.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(r, g, b, a).endVertex();
        ts.draw();
    }

    public static void drawTracers(final Entity e, final int color, final float lw) {
        if (e == null) {
            return;
        }
        final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = e.getEyeHeight() + e.lastTickPosY + (e.posY - e.lastTickPosY) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY;
        final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(lw);
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(2);
        GL11.glVertex3d(0.0, 0.0 + mc.player.getEyeHeight(), 0.0);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawTracers(final BlockPos e, final int color, final float lw) {
        if (e == null) {
            return;
        }
        final float a = (color >> 24 & 0xFF) / 255.0f;
        final float r = (color >> 16 & 0xFF) / 255.0f;
        final float g = (color >> 8 & 0xFF) / 255.0f;
        final float b = (color & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glLineWidth(lw);
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(2);
        GL11.glVertex3d(0.0, 0.0 + mc.player.getEyeHeight(), 0.0);
        GL11.glVertex3d(e.getX(), e.getY(), e.getZ());
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
        drawImage(image, x, y, width, height, 1.0f);
    }

    public static void drawImage(ResourceLocation image, float x, float y, float width, float height) {
        drawImage(image, (int) x, (int) y, (int) width, (int) height, 1.0f);
    }

    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, float alpha) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }


    public enum PositionMode {
        UPLEFT,
        UPRIGHT,
        DOWNLEFT,
        DOWNRIGHT
    }
}
