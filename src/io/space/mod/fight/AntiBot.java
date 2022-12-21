package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public final class AntiBot extends Mod {
    private final ModeValue mode = new ModeValue("Mode","None",new String[]{"Watchdog","Matrix","MineLand","MinePlex","Syuu","None"});
    public static final BooleanValue remove = new BooleanValue("Remove",true);
    private static final BooleanValue antiNPC = new BooleanValue("AntiNPC",false);

    public static final LinkedList<EntityLivingBase> bots = new LinkedList<>();

    public AntiBot() {
        super("AntiBot",Category.FIGHT);
        registerValues(mode,remove,antiNPC);
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void onTick(EventTick e) {
        if (mode.isCurrentMode("Watchdog")) {
            int killedBots = 0;
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity != mc.player && entity instanceof EntityPlayer && entity.onGround && entity.isInvisible() && (int) entity.posX == (int) mc.player.posX && (int) entity.posZ == (int) mc.player.posZ && !getTabPlayerList().contains(entity) && (int) entity.posY != (int) mc.player.posY && entity.ticksExisted < 100) {
                    if (!bots.contains(entity)) {
                        if (remove.getValue()) {
                            mc.world.removeEntity(entity);
                            Wrapper.sendMessage("Kill 1 watchdog");
                        } else {
                            Wrapper.sendMessage("Detected 1 watchdog");
                        }

                        bots.add((EntityLivingBase) entity);
                    }
                }

                if (entity instanceof EntityPlayer) {
                    if (entity != mc.player && entity.isInvisible() && entity.ticksExisted < 25) {
                        if (!getTabPlayerList().contains(entity)) {
                            if (!bots.contains(entity)) {
                                killedBots++;

                                if (remove.getValue()) {
                                    mc.world.removeEntity(entity);
                                }

                                bots.add(((EntityLivingBase) entity));
                            }
                        }
                    }
                }

                final String displayName = entity.getDisplayName().getFormattedText();
                if (entity != mc.player && entity instanceof EntityPlayer && !entity.isInvisible() && displayName.startsWith("§r§c") && displayName.endsWith("§r") && mc.getNetHandler().getPlayerInfo(entity.getUniqueID()).getResponseTime() != 1) {
                    if (entity.posY > mc.player.posY && (double)mc.player.getDistanceToEntity(entity) <= 6.0D && !displayName.startsWith("§r§c[§fYOUTUBE§c]") && !displayName.startsWith("§c[ADMIN]")) {
                        if (!bots.contains(entity)) {
                            if (remove.getValue()) {
                                mc.world.removeEntity(entity);
                                Wrapper.sendMessage("Kill 1 mod bot!!");
                            } else {
                                Wrapper.sendMessage("Detected 1 mod bot!!");
                            }

                            bots.add((EntityLivingBase) entity);
                        }
                    }
                }
            }

            if (killedBots != 0) {
                if (remove.getValue()) {
                    Wrapper.sendMessage("Kill " + killedBots + " Bot!!");
                } else {
                    Wrapper.sendMessage("Detected " + killedBots + " Bot!!");
                }
            }
        } else if (mode.isCurrentMode("Matrix")) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if (Wrapper.getEntitySpeed(entity) > 25 &&
                            entityPlayer.hurtTime >= 7 &&
                            entityPlayer.inventory.armorInventory[0] != null &&
                            entityPlayer.inventory.armorInventory[1] != null &&
                            entityPlayer.inventory.armorInventory[2] != null &&
                            entityPlayer.inventory.armorInventory[3] != null &&
                            entityPlayer.inventory.getCurrentItem() != null &&
                            entityPlayer.stepHeight == 0.0 &&
                            !entityPlayer.isAirBorne &&
                            !entityPlayer.velocityChanged &&
                            !entityPlayer.isCollidedHorizontally &&
                            entityPlayer.moveForward == 0.0f &&
                            entityPlayer.moveStrafing == 0.0f &&
                            entityPlayer.fallDistance == 0.0 &&
                            entityPlayer.ticksExisted < 25) {
                        if (remove.getValue()) {
                            mc.world.removeEntity(entityPlayer);
                            Wrapper.sendMessage("Kill 1 matrix bot!!");
                        } else {
                            Wrapper.sendMessage("Detected 1 matrix bot!!");
                        }

                        bots.add(entityPlayer);
                    }
                }
            }
        } else if (mode.isCurrentMode("MineLand")) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;

                if (entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if (entityPlayer.inventory.armorInventory[0] != null &&
                            entityPlayer.inventory.armorInventory[1] != null &&
                            entityPlayer.inventory.armorInventory[2] != null &&
                            entityPlayer.inventory.armorInventory[3] != null &&
                            entityPlayer.inventory.getCurrentItem() != null &&
                            mc.player.getDistanceToEntity(entityPlayer) <= 10.0 &&
                            entityPlayer.ticksExisted <= 5) {
                        final boolean contains = bots.contains(entityPlayer);

                        if (remove.getValue()) {
                            mc.world.removeEntity(entityPlayer);
                            Wrapper.sendMessage("Kill 1 MineLand bot!!");
                        } else if (!contains) {
                            Wrapper.sendMessage("Detected 1 MineLand bot!!");
                        }

                        if (!contains) bots.add(entityPlayer);
                    }
                }
            }
        } else if (mode.isCurrentMode("MinePlex")) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;

                if (entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if (entityPlayer.isInvisible() &&
                            mc.player.getDistanceToEntity(entityPlayer) <= 6.0 &&
                            entityPlayer.ticksExisted <= 25 &&
                            Wrapper.getEntitySpeed(entityPlayer) > 20) {
                        final boolean contains = bots.contains(entityPlayer);

                        if (remove.getValue()) {
                            mc.world.removeEntity(entityPlayer);
                            Wrapper.sendMessage("Kill 1 MinePlex bot!!");
                        } else if (!contains) {
                            Wrapper.sendMessage("Detected 1 MinePlex bot!!");
                        }

                        if (!contains) bots.add(entityPlayer);
                    }
                }
            }
        } else if (mode.isCurrentMode("Syuu")) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player) continue;
                if (entity instanceof EntityPlayer) {
                    final EntityPlayer entityPlayer = (EntityPlayer)entity;
                    if (entityPlayer.isInvisible() && entityPlayer.getHealth() > 1000.0f && Wrapper.getEntitySpeed(entityPlayer) > 20) {
                        mc.world.removeEntity(entity);
                        Wrapper.sendMessage("Kill 1 syuu bot!!");
                    }
                }
            }
        }
    }

    private List<EntityPlayer> getTabPlayerList() {
        ArrayList<EntityPlayer> list = new ArrayList<>();
        List<NetworkPlayerInfo> players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(mc.getNetHandler().getPlayerInfoMap());

        for (NetworkPlayerInfo o : players) {
            if (o != null) {
                list.add(mc.world.getPlayerEntityByName(o.getGameProfile().getName()));
            }
        }

        return list;
    }

    public static boolean isNPC(EntityLivingBase e) {
        if (!ModManager.Instance.getModFromName("AntiBot").isEnable()) {
            return false;
        }

        if (antiNPC.getValue()) {
            final String formattedText = e.getDisplayName().getFormattedText();
            return formattedText.contains("[NPC]") || formattedText.contains("CIT-") || formattedText.startsWith("§7§8NPC §8| ");
        }

        return false;
    }

    @Override
    protected void onEnable() {
        bots.clear();
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        bots.clear();
        super.onDisable();
    }

    @Override
    protected String getModTag() {
        return mode.getValue();
    }
}
