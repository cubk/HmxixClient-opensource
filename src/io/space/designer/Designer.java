package io.space.designer;

import net.minecraft.client.Minecraft;

public abstract class Designer {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    protected final Type type;

    protected float x,y;
    private float dragX,dragY;

    public Designer(Type type) {
        this.type = type;
    }

    public final void doDrag (int mouseX, int mouseY) {
        if (dragX == 0 && dragY == 0) {
            dragX = mouseX - x;
            dragY = mouseY - y;
        } else {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        dragging();
    }

    public final void resetDrag() {
        dragX = 0;
        dragY = 0;
    }

    public abstract void draw(float partialTicks, int mouseX, int mouseY);

    public abstract boolean canDrag (int mouseX, int mouseY);

    protected void dragging() { }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        RADAR("Radar"),
        PACKET_GRAPH("PacketGraph"),
        TARGET_HUD("TargetHUD"),
        NULL("Null");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Type from(String str) {
            for (Type value : values())
                if (str.equals(value.name))
                    return value;

            return NULL;
        }
    }
}
