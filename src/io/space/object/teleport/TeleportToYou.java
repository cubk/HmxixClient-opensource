package io.space.object.teleport;

import io.space.utils.pathfinder.PathFinder;
import io.space.utils.pathfinder.SigmaVec3;
import net.minecraft.client.Minecraft;

import java.util.List;

public abstract class TeleportToYou {
    protected final Minecraft mc;
    protected final double x;
    protected final double y;
    protected final double z;

    public TeleportToYou(Minecraft mc, double x, double y, double z) {
        this.mc = mc;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected final List<SigmaVec3> findPath() {
        final SigmaVec3 topFrom = new SigmaVec3(mc.player.posX, mc.player.posY, mc.player.posZ);
        final SigmaVec3 to = new SigmaVec3(x, y, z);
        return PathFinder.computePath(topFrom, to);
    }

    public abstract void teleportToXYZ();
}
