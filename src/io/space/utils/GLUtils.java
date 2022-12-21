package io.space.utils;

import io.space.object.MethodExecution;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public final class GLUtils {
    private final LinkedList<Integer> enableToggleList = new LinkedList<>();
    private final LinkedList<Integer> disableToggleList = new LinkedList<>();

    public void enable(int... caps) {
        for (int cap : caps) {
            enable(cap);
        }
    }

    public void enable(int cap) {
        glEnable(cap);
        enableToggleList.add(cap);
    }

    public void disable(int... caps) {
        for (int cap : caps) {
            disable(cap);
        }
    }

    public void disable(int cap) {
        glDisable(cap);
        disableToggleList.add(cap);
    }

    public void enableNoToggle(int... cap) {
        for (int i : cap) {
            enableNoToggle(i);
        }
    }

    public void enableNoToggle(int cap) {
        glEnable(cap);
    }

    public void disableNoToggle(int... cap) {
        for (int i : cap) {
            disableNoToggle(i);
        }
    }

    public void disableNoToggle(int cap) {
        glDisable(cap);
    }

    public void toggle() {
        int cap;

        while (!enableToggleList.isEmpty()) {
            cap = enableToggleList.poll();

            glDisable(cap);
        }

        while (!disableToggleList.isEmpty()) {
            cap = disableToggleList.poll();

            glEnable(cap);
        }
    }

    public static void pushMatrix() {
        glPushMatrix();
    }

    public static void popMatrix() {
        glPopMatrix();
    }

    public static void blendFunc(int sFactor,int dFactor) {
        glBlendFunc(sFactor,dFactor);
    }

    public static void translated(double x,double y,double z) {
        glTranslated(x,y,z);
    }

    public static void rotated(double angle,double x,double y,double z) {
        glRotated(angle,x,y,z);
    }

    public static void depthMask(boolean flag) {
        glDepthMask(flag);
    }

    public static void color(int r,int g,int b) {
        color(r,g,b,255);
    }

    public static void color(int r,int g,int b,int a) {
        GlStateManager.color(r / 255f,g / 255f,b / 255f,a / 255f);
    }

    public static void color(int hex) {
        GlStateManager.color(
                (hex >> 16 & 0xFF) / 255.0f,
                (hex >> 8 & 0xFF) / 255.0f,
                (hex & 0xFF) / 255.0f,
                (hex >> 24 & 0xFF) / 255.0f);
    }

    public static void resetColor() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void scale(double x,double y,double z) {
        glScaled(x,y,z);
    }

    public static void pushScale(double x, double y, double z, MethodExecution execution) {
        glPushMatrix();
        glScaled(x,y,z);
        execution.execute();
        glPopMatrix();
    }
}
