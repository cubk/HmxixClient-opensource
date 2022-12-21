package io.space.mod.fight;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventPreUpdate;
import io.space.mod.Mod;
import io.space.mod.other.Teams;
import io.space.object.RotationObject;
import io.space.utils.RotationUtils;
import io.space.utils.WorldUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public final class AutoAim extends Mod {
    private final NumberValue range = new NumberValue("Range",5,0,10,0.1);
    private final NumberValue rotationSpeed = new NumberValue("RotationSpeed",180,0,180,1);
    private final BooleanValue silence = new BooleanValue("Silence",false);
    private final BooleanValue onMouseClick = new BooleanValue("OnMouseClick",true);
    private final BooleanValue player = new BooleanValue("Player",true);
    private final BooleanValue monster = new BooleanValue("Monster",false);
    private final BooleanValue animal = new BooleanValue("Animal",false);

    private final RotationObject currentRotation = new RotationObject();

    public AutoAim() {
        super("AutoAim",Category.FIGHT);
        registerValues(range,rotationSpeed,silence,onMouseClick,player,monster,animal);
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        if (onMouseClick.getValue() && !mc.gameSettings.keyBindAttack.isKeyDown()) return;

        final ArrayList<EntityLivingBase> entities = WorldUtils.findLivingEntities((o) -> mc.player.getDistanceToEntity(o) <= range.getValue() && shouldAdd(o), (e1, e2) -> (int) ((mc.player.getDistanceToEntity(e1) - mc.player.getDistanceToEntity(e2))));

        if (!entities.isEmpty()) {
            final EntityLivingBase currentEntity = entities.get(0);

            if (Teams.isOnSameTeam(currentEntity)) {
                return;
            }

            final float[] rotationValue;
            final float[] getRotation = RotationUtils.getPredictedRotations(currentEntity);

            if (rotationSpeed.getValue().equals(180.0)) {
                rotationValue = getRotation;
            } else {
                currentRotation.rotationWithSpeed(getRotation[0],getRotation[1],rotationSpeed.getValue());
                rotationValue = currentRotation.getYawPitch();
            }

            if (silence.getValue()) {
                e.setYaw(rotationValue[0]);
                e.setPitch(rotationValue[1]);
                mc.player.rotationYawHead = rotationValue[0];
                mc.player.renderYawOffset = rotationValue[0];
                mc.player.rotationPitchHead = rotationValue[1];
            } else {
                mc.player.rotationYaw = rotationValue[0];
                mc.player.rotationPitch = rotationValue[1];
            }
        } else currentRotation.setYawPitch(mc.player.rotationYaw,mc.player.rotationPitch);
    }

    private boolean shouldAdd(Entity entity) {
        if (entity == mc.player) {
            return false;
        }

        if (!entity.isEntityAlive()) {
            return false;
        }

        if (entity instanceof EntityPlayer && player.getValue()) {
            return true;
        }

        if (entity instanceof EntityMob && monster.getValue()) {
            return true;
        }

        return entity instanceof EntityAnimal && animal.getValue();
    }
}
