package io.space.mod.other;

import io.space.mod.Mod;
import net.minecraft.entity.Entity;

public final class Teams extends Mod {
    public static Teams Instance;

    public Teams() {
        super("Teams", Category.OTHER);
        Instance = this;
    }

    public static boolean isOnSameTeam(Entity entity) {
        if (!Instance.isEnable()) {
            return false;
        }
        if (mc.player.getDisplayName().getUnformattedText().startsWith("§")) {
            if (mc.player.getDisplayName().getUnformattedText().length() <= 2 || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            return mc.player.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2));
        }
        return false;
    }
}
