package io.space.events;

import com.darkmagician6.eventapi.events.Event;
import io.space.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class Event3D implements Event {
    private final float partialTicks;

    public Event3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void pushMatrix() {
        GL11.glPushMatrix();
    }

    public void popMatrix() {
        GL11.glPopMatrix();
    }

    public void enableDepthTest() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void disableDepthTest() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public void lineWidth(float width) {
        GL11.glLineWidth(width);
    }

    public void color3d(int r,int g,int b) {
        color4d(r,g,b,255);
    }

    public void color4d(int hex) {
        RenderUtils.glColor(hex);
    }

    public void color4d(int r,int g,int b,int a) {
        RenderUtils.glColor(RenderUtils.getRGB(r, g, b, a));
    }

    public void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glColor4f(1,1,1,1);
    }
}
