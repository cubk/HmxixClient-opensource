package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.Priority;
import io.space.events.Event3D;
import io.space.events.EventMove;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.mod.fight.KillAura;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;
import utils.hodgepodge.object.time.TimerUtils;

public final class TargetStrafe extends Mod {
    private final NumberValue range = new NumberValue("Range",1.0,0.1,10.0,0.01);
    private final BooleanValue onlyAtPressingKey = new BooleanValue("OnlyAtKey",true);
    private final BooleanValue check = new BooleanValue("Check",true);
    private final BooleanValue draw = new BooleanValue("Draw", true);

    private final TimerUtils changeTimerUtils = new TimerUtils(true);
    private boolean leftMoving;
    private double degree = 0.0;

    public TargetStrafe() {
        super("TargetStrafe",Category.MOVE);
        registerValues(range,onlyAtPressingKey,check,draw);
    }

    @Override
    protected void onDisable() {
        degree = 0.0;
        super.onDisable();
    }

    @EventTarget
    private void on3D(Event3D e) {
        if (!draw.getValue()) return;
        if (KillAura.target == null) return;

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2884);
        GL11.glDisable(2929);
        GL11.glPushMatrix();
        GL11.glTranslated(RenderUtils.getEntityRenderX(KillAura.target), RenderUtils.getEntityRenderY(KillAura.target), RenderUtils.getEntityRenderZ(KillAura.target));
        GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
        RenderUtils.drawCircle(-0.175, 0.35, this.range.getValue().floatValue(), 1.0f, false, -1);
        GL11.glPopMatrix();
        GL11.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    @EventTarget(value = Priority.LOW)
    public void onMove(EventMove e) {
        final EntityLivingBase target = KillAura.target;
        int state = -1;

        if (ModManager.Instance.getModEnable("Speed")) {
            state = 0;
        } else if (ModManager.Instance.getModEnable("Fly")) {
            state = 1;
        }

        if (target != null && state != -1) {
            if (onlyAtPressingKey.getValue() && !mc.gameSettings.keyBindJump.isKeyDown()) return;

            final double moveSpeed = getMoveSpeed(state == 1);
            degree = Math.atan2(mc.player.posZ - target.posZ, mc.player.posX - target.posX);
            degree += leftMoving ? moveSpeed / (double) mc.player.getDistanceToEntity(target) : -(moveSpeed / (double) mc.player.getDistanceToEntity(target));
            double x = target.posX + range.getValue() * Math.cos(degree);
            double z = target.posZ + range.getValue() * Math.sin(degree);
            if (check.getValue() && needToChange(x,z, state == 1)) {
                leftMoving = !leftMoving;

                degree += 2.0 * (leftMoving ? moveSpeed / (double) mc.player.getDistanceToEntity(target) : -(moveSpeed / (double) mc.player.getDistanceToEntity(target)));

                x = target.posX + range.getValue() * Math.cos(degree);
                z = target.posZ + range.getValue() * Math.sin(degree);
            }

            final double sin = -Math.sin(Math.toRadians(toDegree(x, z)));
            final double cos = Math.cos(Math.toRadians(toDegree(x, z)));

            final double motionX = moveSpeed * sin;
            final double motionZ = moveSpeed * cos;

            e.setX(mc.player.motionX = motionX);
            e.setZ(mc.player.motionZ = motionZ);
        }
    }

    private boolean needToChange(double x, double z,boolean flyStrafe) {
        if (mc.player.isCollidedHorizontally) {
            if (changeTimerUtils.hasReached(100.0)) {
                return true;
            }
        }
        for (int i = (int)(mc.player.posY + 4.0); i >= 0; --i) {
            BlockPos playerPos;
            block7: {
                block6: {
                    playerPos = new BlockPos(x,i, z);
                    if (mc.world.getBlockState(playerPos).getBlock().equals(Blocks.lava)) break block6;
                    if (!mc.world.getBlockState(playerPos).getBlock().equals(Blocks.fire)) break block7;
                }
                return true;
            }

            if (flyStrafe) {
                if (mc.world.isAirBlock(playerPos)) {
                    return false;
                }
            } else {
                if (mc.world.isAirBlock(playerPos)) continue;
                return false;
            }
        }
        return true;
    }

    private double toDegree(double x, double z) {
        return (Math.atan2(z - mc.player.posZ, x - mc.player.posX) * 180.0 / Math.PI) - 90.0;
    }

    private double getMoveSpeed(boolean flyStrafe) {
        return flyStrafe ? Fly.moveSpeed : Speed.movementSpeed;
    }
}
