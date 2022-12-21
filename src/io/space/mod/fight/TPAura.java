package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.events.EventPostUpdate;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.mod.other.Teams;
import io.space.object.CPSDelay;
import io.space.utils.GLUtils;
import io.space.utils.WorldUtils;
import io.space.utils.pathfinder.PathFinder;
import io.space.utils.pathfinder.SigmaVec3;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class TPAura extends Mod {
    private final ModeValue attackTiming = new ModeValue("AttackTiming","Post",new String[]{"Post","Pre"});
    private final NumberValue range = new NumberValue("Range",50.0,0.0,100.0,0.1);
    private final NumberValue cps = new NumberValue("Cps",8.0,1.0,20.0,1.0);
    private final NumberValue maxTarget = new NumberValue("MaxTarget",1.0,1.0,5.0,1.0);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", false);
    private final BooleanValue pathESP = new BooleanValue("PathESP", true);
    private final BooleanValue player = new BooleanValue("Player", true);
    private final BooleanValue animal = new BooleanValue("Animal", false);
    private final BooleanValue villager = new BooleanValue("Villager", false);
    private final BooleanValue invisibility = new BooleanValue("Invisibility", false);
    private final BooleanValue monster = new BooleanValue("Monster", false);

    public static EntityLivingBase attackingEntity;

    private final GLUtils GL = new GLUtils();
    private final ArrayList<EntityLivingBase> targets = new ArrayList<>();
    private final CPSDelay cpsTimerUtil = new CPSDelay();

    private final ArrayList<SigmaVec3> path = new ArrayList<>();

    private boolean blocking;

    public TPAura() {
        super("TPAura",Category.FIGHT);
        registerValues(attackTiming,range,cps,maxTarget,autoBlock,pathESP,player,animal,villager,invisibility,monster);
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void onPre(EventPreUpdate e) {
        targets.clear();

        if (attackingEntity == null) {
            path.clear();
        }

        attackingEntity = null;
        findTargets();

        if (attackTiming.isCurrentMode("Pre")) {
            if (!targets.isEmpty()) {
                targets.forEach(entityLivingBase -> attackingEntity = entityLivingBase);
                if (cpsTimerUtil.shouldAttack(cps.getValue().intValue())) {
                    if (autoBlock.getValue() && this.canBlock()) {
                        startBlocking();
                    }
                    doAttack();
                }
            } else if (blocking && autoBlock.getValue()) {
                stopBlocking();
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void onPost(EventPostUpdate e) {
        if (attackTiming.isCurrentMode("Post")) {
            if (!targets.isEmpty()) {
                targets.forEach(entityLivingBase -> attackingEntity = entityLivingBase);
                if (cpsTimerUtil.shouldAttack(cps.getValue().intValue())) {
                    if (autoBlock.getValue() && this.canBlock()) {
                        startBlocking();
                    }
                    doAttack();
                }
            } else if (blocking && autoBlock.getValue()) {
                stopBlocking();
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void on3D(Event3D e) {
        if (pathESP.getValue()) {
            drawPath();
        }
    }

    @Override
    protected void onDisable() {
        targets.clear();
        attackingEntity = null;
        path.clear();
        stopBlocking();
        super.onDisable();
    }

    private void drawPath() {
        for (int i = 0;i < path.size();i++) {
            if (i == path.size() - 1) { continue; }
            final SigmaVec3 vec = path.get(i);
            final double x = vec.getX() - mc.getRenderManager().renderPosX;
            final double y = vec.getY() - mc.getRenderManager().renderPosY;
            final double z = vec.getZ() - mc.getRenderManager().renderPosZ;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x + 1.0, y + 2.0, z + 1.0);
            GL11.glBlendFunc(770, 771);
            GL.enable(3042);
            GL.disable(3553, 2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(1,1,1,1);
            GL11.glLineWidth(2.0f);
            GL.enable(2848);
            RenderGlobal.drawSelectionBoundingBox(axisAlignedBB);
            GlStateManager.resetColor();
            GL11.glDepthMask(true);
            GL.toggle();
        }
    }

    private void findTargets() {
        final ArrayList<EntityLivingBase> list = WorldUtils.findLivingEntities(this::isStandardCompliantEntity,(e1,e2) -> (int) ((mc.player.getDistanceToEntity(e1) - mc.player.getDistanceToEntity(e2))));

        for (int i = 0;i < Math.min(list.size(),maxTarget.getValue());i++) {
            targets.add(list.get(i));
        }
    }

    private boolean isStandardCompliantEntity(Entity e) {
        if (e == mc.player) {
            return false;
        }
        if (!e.isEntityAlive()) {
            return false;
        }
        if (mc.player.getDistanceToEntity(e) > range.getValue()) {
            return false;
        }
        if (e instanceof EntityPlayer) {
            boolean flag = invisibility.getValue() || !e.isInvisible();
            return !Teams.isOnSameTeam(e) && player.getValue() && flag && !AntiBot.isNPC((EntityPlayer)e);
        }
        if (e instanceof EntityMob && monster.getValue()) {
            return true;
        }
        if (e instanceof EntityAnimal && animal.getValue()) {
            return true;
        }
        return e instanceof EntityVillager && villager.getValue();
    }

    private void doAttack() {
        for (EntityLivingBase entity : targets) {
            final SigmaVec3 topFrom = new SigmaVec3(mc.player.posX, mc.player.posY, mc.player.posZ);
            final SigmaVec3 to = new SigmaVec3(entity.posX, entity.posY, entity.posZ);

            path.clear();
            path.addAll(PathFinder.computePath(topFrom, to));

            if (autoBlock.getValue()) {
                stopBlocking();
            }

            for (SigmaVec3 vec3 : path) {
                mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true));
            }

            mc.player.swingItem();
            mc.getNetHandler().sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            final List<SigmaVec3> goHome = path;
            Collections.reverse(goHome);

            for (SigmaVec3 vec3 : goHome) {
                mc.getNetHandler().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(vec3.getX(),vec3.getY(),vec3.getZ(),true));
            }

            if (autoBlock.getValue() && this.canBlock()) {
                startBlocking();
            }
        }
    }

    private void startBlocking() {
        if (mc.player.getHeldItem().getItem() instanceof ItemSword) {
            blocking = true;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem());
        }
    }

    private void stopBlocking() {
        if (blocking && !Mouse.isButtonDown(1)) {
            blocking = false;
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            mc.playerController.onStoppedUsingItem(mc.player);
            mc.player.itemInUseCount = 0;
        }
    }

    private boolean canBlock() {
        return mc.player.inventory.getCurrentItem() != null && mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword;
    }
}
