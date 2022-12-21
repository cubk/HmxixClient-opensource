package io.space.utils;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.BlockData;
import io.space.object.Filter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class WorldUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static ArrayList<Entity> findEntities(Filter<Entity> filter,Comparator<Entity> comparator) {
        final ArrayList<Entity> list = new ArrayList<>();

        for (Entity entity : mc.world.loadedEntityList) {
            if (!filter.check(entity)) continue;

            list.add(entity);
        }

        if (comparator != null) {
            list.sort(comparator);
        }

        return list;
    }

    public static ArrayList<EntityLivingBase> findLivingEntities(Filter<EntityLivingBase> filter,Comparator<EntityLivingBase> comparator) {
        final ArrayList<EntityLivingBase> list = new ArrayList<>();

        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;

            final EntityLivingBase entityLivingBase = ((EntityLivingBase) entity);

            if (!filter.check(entityLivingBase)) continue;

            list.add(entityLivingBase);
        }

        if (comparator != null) {
            list.sort(comparator);
        }

        return list;
    }

    public static ArrayList<EntityPlayer> findPlayers(Filter<EntityPlayer> filter,Comparator<EntityPlayer> comparator) {
        final ArrayList<EntityPlayer> list = new ArrayList<>();

        for (EntityPlayer entity : mc.world.playerEntities) {
            if (!filter.check(entity)) continue;

            list.add(entity);
        }

        if (comparator != null) {
            list.sort(comparator);
        }

        return list;
    }

    public static Entity findEntityFromRotation(double distance,float yaw,float pitch) {
        Entity entity = null;

        final Vec3 positionEyes = mc.player.getPositionEyes(0.0f);

        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        final Vec3 look = new Vec3((f1 * f2),f3,(f * f2));

        final Vec3 addVector = positionEyes.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        Vec3 vec3 = null;
        final List<Entity> entitiesWithinAABBExcludingEntity = mc.world.getEntitiesWithinAABBExcludingEntity(mc.player,mc.player.getEntityBoundingBox().addCoord(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance).expand(1.0, 1.0, 1.0));
        double distance1 = distance;

        for (Entity excludingEntity : entitiesWithinAABBExcludingEntity) {
            if (excludingEntity.canBeCollidedWith()) {
                final double distanceCache;
                final float collisionBorderSize = excludingEntity.getCollisionBorderSize();
                final AxisAlignedBB axisAlignedBB = excludingEntity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(positionEyes, addVector);

                if (axisAlignedBB.isVecInside(positionEyes)) {
                    if (0.0 < distance1 || distance1 == 0.0) {
                        entity = excludingEntity;
                        vec3 = movingObjectPosition == null ? positionEyes : movingObjectPosition.hitVec;
                        distance1 = 0.0;
                    }
                } else if (movingObjectPosition != null && ((distanceCache = positionEyes.distanceTo(movingObjectPosition.hitVec)) < distance1 || distance1 == 0.0)) {
                    if (excludingEntity == mc.player.ridingEntity) {
                        if (distance1 == 0.0) {
                            entity = excludingEntity;
                            vec3 = movingObjectPosition.hitVec;
                        }
                    } else {
                        entity = excludingEntity;
                        vec3 = movingObjectPosition.hitVec;
                        distance1 = distanceCache;
                    }
                }
            }
        }

        if (distance1 < distance && !(entity instanceof EntityLivingBase) && !(entity instanceof EntityItemFrame)) {
            entity = null;
        }

        if (entity == null || vec3 == null) {
            return null;
        }

        return entity;
    }

    public static Entity findEntityFromLook(double distance) {
        return findEntityFromRotation(distance,mc.player.rotationYaw,mc.player.rotationPitch);
    }
}
