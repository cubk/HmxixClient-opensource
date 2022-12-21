package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Particle;
import io.space.events.Event3D;
import io.space.events.EventLivingUpdate;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public final class DamageParticle extends Mod {
    private final BooleanValue blood = new BooleanValue("Blood",false);

    private final LinkedList<Particle> particles = new LinkedList<>();
    private final HashMap<EntityLivingBase,Float> healthMap = new HashMap<>();

    public DamageParticle() {
        super("DamageParticle",Category.VISUAL);
        registerValues(blood);
    }

    @EventTarget
    public void onLivingUpdate(EventLivingUpdate e) {
        final EntityLivingBase entity = e.getEntity();

        if (entity == mc.player) return;

        if (!healthMap.containsKey(entity)) {
            healthMap.put(entity, entity.getHealth());
        }

        float mapHealth = healthMap.get(entity);

        if (mapHealth != entity.getHealth()) {
            final double h = entity.getHealth() - mapHealth;

            if (h == 0) return;

            if (blood.getValue()) {
                if (h < 0) {
                    final Block blockById = Block.getBlockById(Block.getStateId(Blocks.redstone_block.getDefaultState()) & 0xFFF);
                    if (blockById.getDefaultState().getBlock().getMaterial() != Material.air) {
                        final Block.SoundType soundType = blockById.stepSound;
                        mc.world.playSoundAtPos(new BlockPos(entity.posX, entity.posY, entity.posZ), soundType.getBreakSound(), (soundType.getVolume() + 1.0f) / 2.0f, soundType.frequency * 0.8f, false);
                    }

                    mc.effectRenderer.addBlockDestroyEffects(new BlockPos(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ), blockById.getStateFromMeta(Block.getStateId(Blocks.redstone_block.getDefaultState()) >> 12 & 0xFF));
                }
            }

            particles.add(new Particle((h < 0 ? String.valueOf(EnumChatFormatting.RED) : String.valueOf(EnumChatFormatting.GREEN)) + (Math.abs(h) < 1.0 ? String.format("%.1f",h) : ((int) h)), entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ));

            healthMap.replace(entity, entity.getHealth());
        }
    }

    @EventTarget
    public void on3D(Event3D e) {
        for (Particle particle : particles) {
            if (particle.ticks > 100) {
                particles.remove(particle);
                continue;
            }

            final double x = particle.getLastX() + (particle.getX() - particle.getLastX()) * e.getPartialTicks() - mc.getRenderManager().renderPosX;
            final double y = particle.getLastY() + (particle.getY() - particle.getLastY()) * e.getPartialTicks() - mc.getRenderManager().renderPosY;
            final double z = particle.getLastZ() + (particle.getZ() - particle.getLastZ()) * e.getPartialTicks() - mc.getRenderManager().renderPosZ;

            GlStateManager.pushMatrix();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
            GlStateManager.translate(x,y,z);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-particle.scale, -particle.scale, particle.scale);
            GL11.glDepthMask(false);
            mc.customFontRenderer.drawStringWithShadow(particle.getText(),-(mc.customFontRenderer.getStringWidth(particle.getText()) / 2.0f),-(mc.customFontRenderer.FONT_HEIGHT - 1),0);
            RenderUtils.resetColor();
            GL11.glDepthMask(true);
            GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
            GlStateManager.disablePolygonOffset();
            GlStateManager.popMatrix();

            particle.ticks++;

            particle.setY(RenderUtils.getAnimationStateEasing(particle.getY(),particle.getY() + 2,1));

            if (particle.scale > 0.01) {
                particle.scale -= 0.001;
            }
        }
    }

    private static final class Particle {
        private final String text;
        private double x,y,z,lastX,lastY,lastZ;
        private int ticks = 0;
        private double scale;

        public Particle(String text,double x, double y, double z) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.z = z;
            this.lastX = x;
            this.lastY = y;
            this.lastZ = z;
            this.scale = 0.05;
        }

        public String getText() {
            return text;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            lastX = this.x;
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            lastY = this.y;
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            lastZ = this.z;
            this.z = z;
        }

        public double getLastX() {
            return lastX;
        }

        public double getLastY() {
            return lastY;
        }

        public double getLastZ() {
            return lastZ;
        }
    }
}
