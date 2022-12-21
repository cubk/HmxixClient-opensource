package io.space.renderer.gui.button;

import io.space.Wrapper;
import io.space.renderer.font.FontDrawer;
import io.space.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;

public abstract class GuiClientButton extends Gui {
    private String text;
    private int backgroundColor;
    private double x,y;
    private double width,height;
    private FontDrawer fontRenderer;

    private boolean keyDown = true;

    public GuiClientButton(String text,double x,double y,double width,double height,int backgroundColor,FontDrawer fontRenderer) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.fontRenderer = fontRenderer;
    }

    public void drawButton(int mouseX,int mouseY) {
        RenderUtils.drawRect(x,y,x + width,y + height,backgroundColor);
        fontRenderer.drawStringWithShadow(text,((x + (width / 2.0)) - (fontRenderer.getStringWidth(text) / 2.0)),y + (height / 2.0) - (fontRenderer.getHeight() / 2.0),-1);

        if (Wrapper.isHovered(x,y,x + width,y + height,mouseX,mouseY)) {
            RenderUtils.drawRect(x,y,x + width,y + height,RenderUtils.getRGB(0,0,0,30));

            if (Mouse.isButtonDown(0)) {
                if (!keyDown) {
                    keyDown = true;
                    onClicked();
                }
            }
        }

        keyDown = Mouse.isButtonDown(0);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setFontRenderer(FontDrawer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public abstract void onClicked();
}
