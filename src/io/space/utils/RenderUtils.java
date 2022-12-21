package io.space.utils;

import io.space.Wrapper;
import io.space.renderer.gui.dropdown.模糊渲染程序;
import io.space.renderer.gui.dropdown.点击界面;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("DuplicatedCode")
public final class RenderUtils {
    public static void drawRect(double left, double top, double right, double bottom, int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (float) (color >> 24 & 255) / 255.0f;
        float var6 = (float) (color >> 16 & 255) / 255.0f;
        float var7 = (float) (color >> 8 & 255) / 255.0f;
        float var8 = (float) (color & 255) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        resetColor();
    }

    public static void drawBlurRect(float x1, float y1, float x2, float y2, int color) {
        if (点击界面.模糊.getValue() > 0) {
            模糊渲染程序.blurArea(x1, y1, x2 - x1, y2 - y1);
        } else {
            drawRect(x1, y1, x2, y2, color);
        }
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float)ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        return new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);
    }

    public static void drawBorderedRect(double x, double y, double x1, double y1, double width, int borderColor, int color) {
        drawRect(x + width, y + width, x1 - width, y1 - width, color);
        drawRect(x + width, y, x1 - width, y + width, borderColor);
        drawRect(x, y, x + width, y1, borderColor);
        drawRect(x1 - width, y, x1, y1, borderColor);
        drawRect(x + width, y1 - width, x1 - width, y1, borderColor);
    }

    public static void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1.0f,1.0f,1.0f,1.0f);
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static int rainbow(float rainBowHue,float rainBowSatu,float rainBowBright,int ticks) {
        float var2;
        var2 = (float)(System.currentTimeMillis() % (long)((int) rainBowHue)) + (float)(ticks * 100);
        while (var2 > rainBowHue) {
            var2 -= rainBowHue;
        }

        var2 /= rainBowHue;
        if ((double)var2 > 0.5D) {
            var2 = 0.5F - var2 - 0.5F;
        }

        var2 += 0.5F;
        return Color.HSBtoRGB(var2,rainBowSatu,rainBowBright);
    }

    public static int getRGB(int r, int g, int b) {
        return getRGB(r,g,b,255);
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static int getRGB(int r, int g, int b, int a) {
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
    }

    public static int[] splitRGB(int rgb) {
        final int[] ints = new int[3];

        ints[0] = (rgb >> 16) & 0xFF;
        ints[1] = (rgb >> 8) & 0xFF;
        ints[2] = rgb & 0xFF;

        return ints;
    }

    public static int getRGB(int rgb) {
        return 0xff000000 | rgb;
    }

    public static int reAlpha(int rgb,int alpha) {
        return getRGB(getRed(rgb),getGreen(rgb),getBlue(rgb),alpha);
    }

    public static int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    public static int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    public static int getBlue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }

    public static int getAlpha(int rgb) {
        return (rgb >> 24) & 0xff;
    }

    public static void drawBlockBox(BlockPos blockPos,int color,boolean outline) {
        double x = blockPos.getX() - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double y = blockPos.getY() - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double z = blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().renderPosZ;

        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0);

        final Block block = Minecraft.getMinecraft().world.getBlock(blockPos);

        if (block != null) {
            double posX = Minecraft.getMinecraft().player.lastTickPosX + (Minecraft.getMinecraft().player.posX - Minecraft.getMinecraft().player.lastTickPosX) * Minecraft.getMinecraft().timer.renderPartialTicks;
            double posY = Minecraft.getMinecraft().player.lastTickPosY + (Minecraft.getMinecraft().player.posY - Minecraft.getMinecraft().player.lastTickPosY) * Minecraft.getMinecraft().timer.renderPartialTicks;
            double posZ = Minecraft.getMinecraft().player.lastTickPosZ + (Minecraft.getMinecraft().player.posZ - Minecraft.getMinecraft().player.lastTickPosZ) * Minecraft.getMinecraft().timer.renderPartialTicks;
            axisAlignedBB = block.getSelectedBoundingBox(Minecraft.getMinecraft().world, blockPos).expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026).offset(-posX, -posY, -posZ);
        }

        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        glColor(color);

        drawBoundingBox(axisAlignedBB);

        if (outline) {
            GL11.glLineWidth(2.0f);
            GL11.glEnable(2848);
            glColor(color);
            RenderGlobal.drawSelectionBoundingBox(axisAlignedBB);
        }

        resetColor();
        GL11.glDepthMask(true);

        if (outline) GL11.glDisable(2848);

        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
    }

    public static void drawBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    @SuppressWarnings("DuplicatedCode")
    public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer buffer = tessellator.getWorldRenderer();
        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        tessellator.draw();
        buffer.begin(3, DefaultVertexFormats.POSITION);
        buffer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        tessellator.draw();
        buffer.begin(1, DefaultVertexFormats.POSITION);
        buffer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
        buffer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
        buffer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
        buffer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
        buffer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawImage(double x,double y,double width,double height,int color,BufferedImage image) {
        drawImage(x,y,width,height,color,new DynamicTexture(image));
    }

    public static void drawImage(double x, double y, double width, double height, int color, ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);

        final float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        final float red = (float)(color >> 16 & 0xFF) / 255.0f;
        final float green = (float)(color >> 8 & 0xFF) / 255.0f;
        final float blue = (float)(color & 0xFF) / 255.0f;

        GlStateManager.pushMatrix();
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.depthMask(false);

        final double scaleX = 1.0 / width;
        final double scaleY = 1.0 / height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x,y + height,0).tex(0.0,height * scaleY).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x + width,y + height,0).tex(width * scaleX,height * scaleY).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x + width,y + 0,0).tex(width * scaleX,0.0).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x,y + 0,0).tex(0.0,0.0).color(red,green,blue,alpha).endVertex();
        tessellator.draw();

        GL11.glEnable(2929);
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();

        resetColor();
    }

    public static void drawImage(double x,double y,double width,double height,int color,DynamicTexture texture) {
        GlStateManager.bindTexture(texture.getGlTextureId());

        final float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        final float red = (float)(color >> 16 & 0xFF) / 255.0f;
        final float green = (float)(color >> 8 & 0xFF) / 255.0f;
        final float blue = (float)(color & 0xFF) / 255.0f;

        GL11.glDisable(2929);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        GlStateManager.depthMask(false);

        final double scaleX = 1.0 / width;
        final double scaleY = 1.0 / height;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldrenderer.pos(x,y + height,0).tex(0.0,height * scaleY).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x + width,y + height,0).tex(width * scaleX,height * scaleY).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x + width,y + 0,0).tex(width * scaleX,0.0).color(red,green,blue,alpha).endVertex();
        worldrenderer.pos(x,y + 0,0).tex(0.0,0.0).color(red,green,blue,alpha).endVertex();
        tessellator.draw();

        GL11.glEnable(2929);
        GlStateManager.depthMask(true);

        resetColor();
    }

    public static double delta;

    public static double getAnimationState(double animation, double finalState, double speed) {
        double add = delta * speed;
        animation = animation < finalState ? (Math.min(animation + add, finalState)) : (Math.max(animation - add, finalState));
        return animation;
    }

    public static double getAnimationStateEasing(double animation, double finalState,double speed) {
        double add = delta * AnimationUtils.easing(animation,finalState,speed);
        animation = animation < finalState ? (Math.min(animation + add, finalState)) : (Math.max(animation - add, finalState));
        return animation;
    }

    public static void drawRoundedRect(double x, double y, double x2, double y2, float round, int color) {
        drawRect(x += (round / 2.0 + 0.5), y += (round / 2.0f + 0.5),x2 -= (round / 2.0f + 0.5), y2 -= (round / 2.0f + 0.5), color);
        drawCircle(x2 - round / 2.0, y + round / 2.0, round,1,true,color);
        drawCircle(x + round / 2.0, y2 - round / 2.0, round,1,true, color);
        drawCircle(x + round / 2.0, y + round / 2.0, round,1,true, color);
        drawCircle(x2 - round / 2.0, y2 - round / 2.0, round,1,true, color);
        drawRect(x - round / 2.0f - 0.5f, y + round / 2.0,x2,y2 - round / 2.0, color);
        drawRect(x,y + round / 2.0f,x2 + round / 2.0 + 0.5, y2 - round / 2.0, color);
        drawRect(x + round / 2.0, y - round / 2.0 - 0.5,x2 - round / 2.0,y2 - round / 2.0, color);
        drawRect(x + round / 2.0,y, x2 - round / 2.0,y2 + round / 2.0 + 0.5, color);
    }

    public static void drawCircle(double x, double y, double r, float lineWidth, boolean isFull, int color) {
        RenderUtils.drawCircle(x, y, r, 10, lineWidth, 360, isFull, color);
    }

    public static void drawCircle(double cx, double cy, double r, int segments, float lineWidth, int part, boolean isFull, int color) {
        GL11.glScaled(0.5,0.5,0.5);
        r *= 2.0;
        cx *= 2.0f;
        cy *= 2.0f;
        GL11.glEnable(3042);
        GL11.glLineWidth(lineWidth);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        glColor(color);
        GL11.glBegin(3);

        for (int i = segments - part; i <= segments; ++i) {
            double x = Math.sin(i * Math.PI / 180.0) * r;
            double y = Math.cos(i * Math.PI / 180.0) * r;
            GL11.glVertex2d(cx + x,cy + y);
            if (!isFull) continue;
            GL11.glVertex2d(cx, cy);
        }

        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }

    public static void startGlScissor(int x, int y, int width, int height) {
        int scaleFactor = Wrapper.Instance.getScaledResolution().getScaleFactor();
        GL11.glPushMatrix();
        GL11.glEnable(3089);
        GL11.glScissor(x * scaleFactor,Minecraft.getMinecraft().displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }

    public static void stopGlScissor() {
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }

    public static double getEntityRenderX(Entity entity) {
        return entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosX;
    }

    public static double getEntityRenderY(Entity entity) {
        return entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosY;
    }

    public static double getEntityRenderZ(Entity entity) {
        return entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)Minecraft.getMinecraft().timer.renderPartialTicks - Minecraft.getMinecraft().getRenderManager().renderPosZ;
    }
}
