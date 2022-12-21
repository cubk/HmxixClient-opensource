package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event2D;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.ColorValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class Arrow extends Mod {
    private final NumberValue searchRange = new NumberValue("SearchRange",25.0,0.0,100.0,0.1);
    private final NumberValue round = new NumberValue("Round",25.0,1.0,200.0,0.1);
    private final ColorValue color = new ColorValue("Color",1,1,1);

    public Arrow() {
        super("Arrow",Category.VISUAL);
        registerValues(searchRange,round,color);
    }

    @EventTarget
    public void on2D(Event2D e) {
        for (EntityPlayer playerEntity : mc.world.playerEntities) {
            if (playerEntity != mc.player && mc.player.getDistanceToEntity(playerEntity) <= searchRange.getValue()) {
                final double pos1 = (playerEntity.lastTickPosX + (playerEntity.posX - playerEntity.lastTickPosX) * mc.timer.renderPartialTicks) - mc.player.posX;
                final double pos2 = (playerEntity.lastTickPosZ + (playerEntity.posZ - playerEntity.lastTickPosZ) * mc.timer.renderPartialTicks) - mc.player.posZ;
                final double cos = Math.cos(mc.player.rotationYaw * (Math.PI * 2 / 360));
                final double sin = Math.sin(mc.player.rotationYaw * (Math.PI * 2 / 360));
                final double rotY = -(pos2 * cos - pos1 * sin);
                final double rotX = -(pos1 * cos + pos2 * sin);
                final float angle = (float) (Math.atan2(rotY, rotX) * 180 / Math.PI);
                final double x = (round.getValue() * Math.cos(Math.toRadians(angle))) + (e.getScaledResolution().getScaledWidth() / 2.0f - 24.5f) + 25;
                final double y = (round.getValue() * Math.sin(Math.toRadians(angle))) + (e.getScaledResolution().getScaledHeight() / 2.0f - 25.2f) + 25;

                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, 0);
                GlStateManager.rotate(angle, 0, 0, 1);
                GlStateManager.scale(1.0, 1.0, 1.0);
                draw(0, 0, 4.2,3);
                draw(0, 0, 4.0,3);
                draw(0, 0, 3.5,3);
                draw(0, 0, 3.0,3);
                draw(0, 0, 2.5,3);
                draw(0, 0, 2.0,3);
                draw(0, 0, 1.5,3);
                draw(0, 0, 1.0,3);
                draw(0, 0, 0.5,3);
                GlStateManager.popMatrix();
            }
        }
    }

    public void draw(double cx, double cy, double r, double n) {
        GL11.glPushMatrix();
        cx *= 2.0;
        cy *= 2.0;
        double b = 6.2831852 / n;
        double p = Math.cos(b);
        double s = Math.sin(b);
        double x = r * 2.0;
        double y = 0.0;
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glScaled(0.5, 0.5, 0.5);
        GlStateManager.color(0, 0, 0);
        GlStateManager.resetColor();
        RenderUtils.glColor(color.getValue());
        GL11.glBegin(2);
        for (int i = 0; i < n; i++) {
            GL11.glVertex2d(x + cx, y + cy);
            final double t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        GL11.glEnd();
        GL11.glScaled(2.0, 2.0, 2.0);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
        GlStateManager.disableBlend();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1, 1);
        GL11.glPopMatrix();
    }
}
