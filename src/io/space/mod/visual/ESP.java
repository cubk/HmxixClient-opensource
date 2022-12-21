package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public final class ESP extends Mod {
    private final BooleanValue self = new BooleanValue("Self", false);

    public ESP() {
        super("ESP", Category.VISUAL);
        registerValues(self);
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void on3D(Event3D e) {
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (entity == mc.player && mc.gameSettings.thirdPersonView == 0 || !isValid(entity) || !entity.isEntityAlive())
                continue;

            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);

            GlStateManager.translate(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.getPartialTicks() - mc.getRenderManager().viewerPosX,
                    (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.getPartialTicks() - mc.getRenderManager().viewerPosY) + entity.height + 0.5 - (entity.isChild() ? entity.height / 2.0 : 0.0),
                    entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.getPartialTicks() - mc.getRenderManager().viewerPosZ);

            GL11.glNormal3d(0.0, 1.0, 0.0);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);

            double scale = 0.035f;
            GL11.glScaled(-(scale /= 2.0f), -scale, -scale);

            double xLeft = -20.0;
            double yUp = 27.0;
            double yDown = 130.0;
            float distance = mc.player.getDistanceToEntity(entity);

            float health = entity.getHealth() + entity.getAbsorptionAmount();

            int color = health < 5.0 ? RenderUtils.getRGB(255, 20, 10) : (health < 12.5 ? RenderUtils.getRGB(16774441) : RenderUtils.getRGB(0, 255, 0));

            RenderUtils.drawBorderedRect(xLeft - 3.0 - distance * 0.2,yDown - (yDown - yUp),xLeft - 2.0,yDown, 0.15,RenderUtils.getRGB(0,0,0),RenderUtils.getRGB(100, 100, 100));
            RenderUtils.drawBorderedRect(xLeft - 3.0 - distance * 0.2,yDown - (yDown - yUp) * Math.min(1.0, health / 20.0),xLeft - 2.0,yDown, 0.15,RenderUtils.getRGB(0,0,0),color);

            final String healthString = String.valueOf((int) health) + EnumChatFormatting.RED + " ♥";
            mc.customFontRenderer.drawStringWithOutline(healthString, (float) xLeft - mc.customFontRenderer.getStringWidth(healthString) - 10, (float) yDown / 2,color);

            final String itemName = entity.getHeldItem() == null ? "" : entity.getHeldItem().getDisplayName();
            final float itemNameWidth = mc.customFontRenderer.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes(itemName));
            RenderUtils.drawRect((xLeft / 2.0) - (itemNameWidth / 2.0) - 1,yDown + 9,(xLeft / 2.0) + (itemNameWidth / 2.0) + 1,yDown + 19,RenderUtils.getRGB(0,0,0,100));
            mc.customFontRenderer.drawStringWithShadow(itemName, (float) (xLeft / 2.0 - (itemNameWidth / 2)), (float) yDown + 10, RenderUtils.getRGB(0, 255, 0));

            int potionY = 0;
            for (PotionEffect effect : entity.getActivePotionEffects()) {

                final Potion potion = Potion.potionTypes[effect.getPotionID()];

                String PType = I18n.format(potion.getName());

                switch (effect.getAmplifier()) {
                    case 1: {
                        PType = PType + " II";
                        break;
                    }
                    case 2: {
                        PType = PType + " III";
                        break;
                    }
                    case 3: {
                        PType = PType + " IV";
                    }
                }

                if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                    PType = PType + "§7:§6 " + Potion.getDurationString(effect);
                } else if (effect.getDuration() < 300) {
                    PType = PType + "§7:§c " + Potion.getDurationString(effect);
                } else if (effect.getDuration() > 600) {
                    PType = PType + "§7:§7 " + Potion.getDurationString(effect);
                }

                mc.customFontRenderer.drawStringWithOutline(PType, (float) xLeft - mc.customFontRenderer.getStringWidth(PType) - 5.0f, (float) yUp - mc.customFontRenderer.FONT_HEIGHT + potionY + 20.0f, potion.getLiquidColor());
                potionY += 10;
            }

            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    public boolean isValid(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && entity.getHealth() >= 0.0f) {
            return entity != mc.player || self.getValue();
        }
        return false;
    }
}
