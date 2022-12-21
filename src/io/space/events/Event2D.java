package io.space.events;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.client.gui.ScaledResolution;

public final class Event2D implements Event {
    private final ScaledResolution scaledResolution;
    private final float partialTicks;

    public Event2D(ScaledResolution scaledResolution, float partialTicks) {
        this.scaledResolution = scaledResolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }

    public int getWidth() {
        return scaledResolution.getScaledWidth();
    }

    public int getHeight() {
        return scaledResolution.getScaledHeight();
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
