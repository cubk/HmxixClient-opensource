package io.space.cape;

import net.minecraft.util.ResourceLocation;

public final class CapeObject {
    private final String name;
    private final ResourceLocation resourceLocation;

    public CapeObject(String name, ResourceLocation resourceLocation) {
        this.name = name;
        this.resourceLocation = resourceLocation;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getResourceLocation() {
        return resourceLocation;
    }
}
