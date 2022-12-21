package io.space.value.values;

import io.space.utils.RenderUtils;
import io.space.value.Value;
import io.space.value.ValueType;

import java.awt.*;

public class ColorValue extends Value<Integer> {
    private float hue;
    private float saturation;
    private float brightness;

    public ColorValue(String valueName,int r,int g,int b) {
        this(valueName,RenderUtils.getRGB(r,g,b));
    }

    public ColorValue(String valueName,int rgb) {
        super(valueName,ValueType.COLOR_VALUE);

        final int[] ints = RenderUtils.splitRGB(rgb);
        final float[] hsb = Color.RGBtoHSB(ints[0],ints[1],ints[2], null);

        this.hue = hsb[0];
        this.saturation = hsb[1];
        this.brightness = hsb[2];

        setValueDirect(rgb);
    }

    boolean exp;
    public void setExpanded(boolean exp) {
        this.exp = exp;
    }

    public boolean isExpanded() {
        return exp;
    }
    public ColorValue(String valueName,float hue,float saturation,float brightness) {
        super(valueName,ValueType.COLOR_VALUE);
        this.hue = hue;
        this.saturation = saturation;
        this.brightness = brightness;
        setValueDirect(RenderUtils.getRGB(Color.HSBtoRGB(hue,saturation,brightness)));
    }

    public int getRed() {
        return RenderUtils.getRed(getValue());
    }

    public int getGreen() {
        return RenderUtils.getGreen(getValue());
    }

    public int getBlue() {
        return RenderUtils.getBlue(getValue());
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }
}
