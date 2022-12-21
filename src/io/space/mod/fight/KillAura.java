package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.*;
import io.space.mod.Mod;
import io.space.mod.other.Teams;
import io.space.mod.world.Scaffold;
import io.space.object.CPSDelay;
import io.space.utils.Render3DHelper;
import io.space.utils.RotationUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.ColorValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.*;
import net.optifine.util.MathUtils;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * 杀人气质
 *
 * @author cubk
 * @date 2022/11/02
 */

@SuppressWarnings("unused")
public final class KillAura extends Mod {

    public static final ModeValue rotation = new ModeValue("Rotation Mode", "Normal",new String[]{"Normal","Static","sb"});
    public static final ModeValue attackMode = new ModeValue("Attack Mode","Single",new String[]{"Single","Switch","Multi"});
    public static final ModeValue mark = new ModeValue("Attack ESP Mark", "None",new String[]{"None","Box"});
    public static final ModeValue blockMode = new ModeValue("Block Mode","a",new String[]{"a","b"});
    public static final ColorValue espColor = new ColorValue("Mark Color",Color.GREEN.getRGB());
    private static final NumberValue crack = new NumberValue("CrackSize", 1.0, 0.0, 5.0, 0.1);
    private static final NumberValue aps = new NumberValue("Minimum Attack Pre Seconds", 10.0, 1.0, 20.0, 0.5);
    private static final NumberValue maxaps = new NumberValue("Maximum Attack Pre Seconds", 10.0, 1.0, 20.0, 0.5);
    private static final NumberValue angle = new NumberValue("Max Angle", 360.0, 10.0, 360.0, 10D);
    private static final NumberValue reach = new NumberValue("Attack Range", 4.5, 1.0, 6.0, 0.1);
    private static final NumberValue swing_range = new NumberValue("Swing Range", 4.5, 1.0, 8.0, 0.1);
    private static final NumberValue block_range = new NumberValue("Block Range", 3.0, 1.0, 6.0, 0.1);
    private static final BooleanValue autoBlock = new BooleanValue("AutoBlock", true);
    private static final BooleanValue hurt = new BooleanValue("Change ESP Color when hit", true);
    private static final BooleanValue lock_view = new BooleanValue("Lock View", false);
    private static final BooleanValue autoDisable = new BooleanValue("Disable on Death", false);
    private static final BooleanValue no_rotation = new BooleanValue("No Rotation", false);
    private static final BooleanValue gui_check = new BooleanValue("GUI Check", false);
    private static final BooleanValue close_gui = new BooleanValue("Close Inventory", false);
    private static final BooleanValue players = new BooleanValue("Players", true);
    private static final BooleanValue animals = new BooleanValue("Animals", true);
    private static final BooleanValue mobs = new BooleanValue("Mobs", true);
    private static final BooleanValue invisible = new BooleanValue("Invisible", false);
    private static final BooleanValue killeffect = new BooleanValue("Kill Effect", false);

    public static final List<EntityLivingBase> targets = new ArrayList<>();
    public static CPSDelay cpsDelay = new CPSDelay();
    public static float[] rotations;
    public static boolean blocking;
    public static boolean attacking;

    public static volatile EntityLivingBase target;

    public KillAura () {
        super("KillAura", Category.FIGHT);
        registerValues(rotation,attackMode,mark,blockMode,espColor,crack,aps,maxaps,angle,reach,swing_range,block_range,autoBlock,hurt,lock_view,autoDisable,no_rotation,gui_check,close_gui,players,animals,mobs,invisible,killeffect);
    }

    public double randomInRange (double min, double max) {
        if (min > max) {
            return min;
        }
        max -= min;
        return (Math.random() * (max)) + min;
    }

    public static boolean isFovInRange (final Entity entity, float fov) {
        fov *= 0.5;
        final double v = ((mc.player.rotationYaw - getRotation(entity)) % 360.0 + 540.0) % 360.0 - 180.0;
        return (v > 0.0 && v < fov) || (- fov < v && v < 0.0);
    }

    public static float getRotation (final Entity ent) {
        final double x = ent.posX - mc.player.posX;
        final double z = ent.posZ - mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        yaw = - yaw;
        return (float) yaw;
    }

    @EventTarget
    public void onWorld(EventWorldLoad e){
        if(autoDisable.getValue())
            this.setEnable(false);
    };

    @EventTarget
    public void onPre(EventPreUpdate e){
        if(mc.player.isBlocking() && ! Mouse.isButtonDown(1) && target == null)
            unBlock();


        if (gui_check.getValue() && mc.currentScreen != null)
            return;


        if (mc.player.isDead || mc.player.isSpectator())
            return;
        if (check())
            return;
        if (targets.isEmpty())
            return;
        int crackSize = crack.getValue().intValue();



        if (attackMode.isCurrentMode("Switch")) {
            if (targets.size() > 1) {
                for (EntityLivingBase entityLivingBase : targets) {
                    if (canHit(entityLivingBase))
                        target = entityLivingBase;
                }

                if (target == null)
                    target = targets.get(0);

            } else {
                target = targets.get(0);
            }
        } else {
            target = targets.get(0);
        }

        if (target == null && autoBlock.getValue() && mc.player.isBlocking()) {
            if (hasSword()) {
                unBlock();
            }
        }
        if (hasSword() && target != null && autoBlock.getValue() && !blocking) {
            this.block();
        }

        if (! no_rotation.getValue()) {
            rotations = getRotationsToEnt(target);
            if (rotation.isCurrentMode("Normal")) {
                rotations[ 0 ] += MathUtils.getRandomInRange(1, 5);
                rotations[ 1 ] += MathUtils.getRandomInRange(1, 5);
            }
            if (rotation.isCurrentMode("Static")) {
                rotations[ 0 ] = (float) (rotations[ 0 ] + ((Math.abs(target.posX - target.lastTickPosX) - Math.abs(target.posZ - target.lastTickPosZ)) * (2 / 3)) * 2);
                rotations[ 1 ] = (float) (rotations[ 1 ] + ((Math.abs(target.posY - target.lastTickPosY) - Math.abs(target.getEntityBoundingBox().minY - target.lastTickPosY)) * (2 / 3)) * 2);
            }

            if (rotation.isCurrentMode("sb")) {
                if (target.posY < 0) {
                    rotations[ 1 ] = 1;
                } else if (target.posY > 255) {
                    rotations[ 1 ] = 90;
                }

                if (Math.abs(target.posX - target.lastTickPosX) > 0.50 || Math.abs(target.posZ - target.lastTickPosZ) > 0.50) {
                    target.setEntityBoundingBox(new AxisAlignedBB(target.posX, target.posY, target.posZ, target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ));
                }
            }
            
            if (lock_view.getValue()) {
                if (target != null) {
                    mc.player.rotationYawHead = rotations[ 0 ];
                    mc.player.rotationYaw = rotations[ 0 ];
                }
            } else {
                e.setYaw(rotations[ 0 ]);
                e.setPitch(rotations[ 1 ]);
                mc.player.rotationYawHead = rotations[ 0 ];
            }
        }

        attacking = true;
        int pre = (int) randomInRange(aps.getValue(), maxaps.getValue());
        if (cpsDelay.shouldAttack(pre)) {
            if (attackMode.isCurrentMode("Multi")) {
                if (close_gui.getValue() && mc.currentScreen != null)
                    mc.displayGuiScreen(null);
                for (EntityLivingBase entity : targets) {
                    if (mc.player.getDistanceToEntity(entity) <= swing_range.getValue())
                        mc.player.swingItem();
                    if (this.hasSword() && mc.player.isBlocking() && isValid(target)) {
                        unBlock();
                    }
                    mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    if (!mc.player.isBlocking() && this.hasSword() && autoBlock.getValue()) {
                        this.block();
                    }
                }
            } else if (target.getHealth() > 0) {
                if (close_gui.getValue() && mc.currentScreen != null)
                    mc.displayGuiScreen(null);
                if (mc.player.getDistanceToEntity(target) <= swing_range.getValue())
                    mc.player.swingItem();
                if (this.hasSword() && mc.player.isBlocking() && isValid(target)) {
                    unBlock();
                }
                mc.getNetHandler().getNetworkManager().sendPacket(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                if (!mc.player.isBlocking() && this.hasSword() && autoBlock.getValue()) {
                    this.block();
                }
                int i2 = 0;
                while (i2 < crackSize && target != null) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                    i2++;
                }
                if (target != null) {
                    if (target.getHealth() <= 0 || target.isDead) {
                        sortTargets();
                        target = targets.get(0);
                    }
                } else {
                    sortTargets();
                    target = targets.get(0);
                }
            }
        }
    };

    @EventTarget
    public void on3D(Event3D e){
        if(attackMode.isCurrentMode("Multi") || attackMode.isCurrentMode("Switch")){
            for(EntityLivingBase target : targets){
                draw(target);
            }
        }else {
            draw(target);
        }
    };

    private boolean hasSword() {
        if (mc.player.inventory.getCurrentItem() != null) {
            return mc.player.inventory.getCurrentItem().getItem() instanceof ItemSword;
        } else {
            return false;
        }
    }

    public void draw(EntityLivingBase t){
        int color = espColor.getValue();
        if(hurt.getValue() && t.hurtTime > 0)
            color = Color.RED.getRGB();
        switch(mark.getValue()){
            case "None":
                break;
            case "Box":
                Render3DHelper.drawESP(t,color,false,3);
                break;
        }
    }

    private static boolean canHit (EntityLivingBase e) {
        return e.hurtTime == 0;
    }

//    public final IEventListener<PostUpdateEvent> onPacket = (e) -> {
////        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(-1,-1,-1), EnumFacing.DOWN));
//        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1,-1,-1),255, mc.player.inventory.getCurrentItem(), 0f, 0f, 0f));
//
//    }
//            ;

    //@EventTarget
    //private void onPost(PostUpdateEvent e) {
    @EventTarget
    private void onPost(EventPostUpdate e) {
        sortTargets();
        if (mc.player.isDead || mc.player.isSpectator())
            return;
        if(check())
            return;
        if (!targets.isEmpty() && blockMode.isCurrentMode("b")) {
            if (autoBlock.getValue() && mc.player.getItemInUse() == null) {
                if(mc.player.getHeldItem() != null) {
                    if (mc.player.getHeldItem().getItem() instanceof ItemSword) {
                        if (target != null) {
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                            if (mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem())) {
                                mc.getItemRenderer().resetEquippedProgress();
                            }
                            blocking = true;
                        } else {
                            blocking = false;
                            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                            mc.playerController.onStoppedUsingItem(mc.player);
                        }
                    }
                }
            }

        }

        if (targets.isEmpty()) {
            if (blocking) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
            attacking = false;
            blocking = false;
            target = null;
        }
    };

    private void block() {
        if(blockMode.isCurrentMode("a"))
            return;
        if(mc.player.getHeldItem() == null)
            return;
        if (mc.player.getHeldItem().getItem() instanceof ItemSword) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
            if (mc.playerController.sendUseItem(mc.player, mc.world, mc.player.inventory.getCurrentItem())) {
                mc.getItemRenderer().resetEquippedProgress();
            }
        }
    }

    private static void unBlock () {
        if(blockMode.isCurrentMode("a"))
            return;
        if(mc.player.getHeldItem() == null)
            return;
        if (mc.player.getHeldItem().getItem() instanceof ItemSword) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            mc.playerController.onStoppedUsingItem(mc.player);
        }
    }

    @Override
    public final void onDisable() {
        unBlock();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        mc.playerController.onStoppedUsingItem(mc.player);
        targets.clear();
        blocking = false;
        target = null;
        attacking = false;
    }

    public static final void sortTargets() {
        targets.clear();
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entLiving = (EntityLivingBase) entity;
                if (mc.player.getDistanceToEntity(entLiving) < reach.getValue() && entLiving != mc.player && !entLiving.isDead && isValid(entLiving)) {
                    targets.add(entLiving);
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.player::getDistanceToEntity));
    }

    private static final boolean check(){
        if(Scaffold.Instance.isEnable())
            return true;
        return false;
    }

    public static final boolean isValid(EntityLivingBase ent) {
        if (ent instanceof EntityPlayer && !players.getValue())
            return false;
        if (ent instanceof EntityMob && !mobs.getValue())
            return false;
        if (ent instanceof EntityAnimal && !animals.getValue())
            return false;
        if (ent.isInvisible() && ! invisible.getValue())
            return false;
        if(AntiBot.bots.contains(ent))
            return false;
        if(Teams.isOnSameTeam(ent))
            return false;
        if(!isFovInRange(ent,angle.getValue().floatValue()))
            return false;
        if (ent.getHealth() <= 0)
            return false;
        if (ent.isDead) {
            target = null;
            return false;
        }
        return true;
    }

    @EventTarget
    public void onTick(EventTick e){
        if(target.getHealth() <= 0) {
            if(!killeffect.getValue())
                return;
            mc.player.playSound("ambient.weather.thunder", 0.3f, 0.5f);
            mc.world.addEntityToWorld(new Random().nextInt(114514), new EntityLightningBolt(mc.world, target.posX, target.posY, target.posZ));
        }
    }

    private static final float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.player.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.player.posY + mc.player.height) - 0.5;
        final double differenceZ = ent.posZ - mc.player.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.player.getDistanceToEntity(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.player.rotationYaw
                + MathHelper.wrapAngleTo180_float(rotationYaw - mc.player.rotationYaw);
        final float finishedPitch = mc.player.rotationPitch
                + MathHelper.wrapAngleTo180_float(rotationPitch - mc.player.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90, 90)};
    }
}

