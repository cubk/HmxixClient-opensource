package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.designer.designerimpl.RadarDesigner;
import io.space.designer.designerimpl.TargetHUDDesigner;
import io.space.events.Event2D;
import io.space.mod.Mod;
import io.space.mod.fight.KillAura;
import io.space.renderer.font.FontManager;
import io.space.utils.RenderUtils;
import io.space.value.values.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.MathUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public final class TargetHUD extends Mod {
    public static final ModeValue mode = new ModeValue("Mode", "Astolfo", new String[]{"Space", "Minecraft","Astolfo"});

    private static EntityLivingBase space_lastEntity;
    private static double space_animationX;

    public TargetHUD() {
        super("TargetHUD", Category.VISUAL);
        registerValues(mode);
    }

    @EventTarget
    public void on2D(Event2D e) {
        TargetHUDDesigner.Instance.draw(e.getPartialTicks(), Wrapper.Instance.getMouseX(),Wrapper.Instance.getMouseY());
    }

    public static void renderAstolfo(ScaledResolution scaledResolution,EntityLivingBase entity){
        GL11.glPushMatrix();
        Color color;
        float width2 = Math.max(75, mc.fontRenderer.getStringWidth(entity.getName()) + 20);
        String healthStr2 = Math.round(entity.getHealth() * 10) / 10d + " ❤";
        GL11.glTranslatef(TargetHUDDesigner.Instance.getX(), TargetHUDDesigner.Instance.getY(), 0);
        RenderUtils.drawRect(0, 0, 55 + width2, 47, new Color(0, 0, 0, 55).getRGB());

        mc.fontRenderer.drawStringWithShadow(entity.getName(), 35, 3f, -1);

        boolean isNaN = Float.isNaN(entity.getHealth());
        float health = isNaN ? 20 : entity.getHealth();
        float maxHealth = isNaN ? 20 : entity.getMaxHealth();
        float healthPercent = MathUtils.clampValue((int) (health / maxHealth), 0, 1);

        int hue = (int) (healthPercent * 120);
        color = Color.getHSBColor(hue / 360f, 0.7f, 1f);

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0, 2.0, 2.0);
        mc.fontRenderer.drawStringWithShadow(healthStr2, 18, 7.5f, color.getRGB());
        GlStateManager.popMatrix();

        RenderUtils.drawRect(36, 36.5f, 45 + width2, 44.5f, RenderUtils.reAlpha(color.darker().darker().getRGB(), 100));

        float barWidth = (43 + width2 - 2) - 37;
        float drawPercent = 43 + (barWidth / 100) * (healthPercent * 100);

/*            RenderUtil.drawRect(36, 36.5f, this.animation + 6, 44.5f, color.darker().darker().getRGB());
            RenderUtil.drawRect(36, 36.5f, this.animation, 44.5f, color.getRGB());*/
        if (!(drawPercent + entity.hurtTime > (int) (55 + width2)))
            RenderUtils.drawRect(36, 36.5f, drawPercent + entity.hurtTime, 44.5f, color.getRGB());
        RenderUtils.drawRect(36, 36.5f, drawPercent, 44.5f, color.getRGB());

        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();

        GlStateManager.resetColor();
        GlStateManager.disableBlend();
        GlStateManager.color(1, 1, 1, 1);
        GuiInventory.drawEntityOnScreen(17, 46, (int) (42 / entity.height), 0, 0, entity);
        GL11.glPopMatrix();
    }

    public static void renderSpace(ScaledResolution scaledResolution,EntityLivingBase entity) {
        final double percentageOfHealth = Math.min(80,((entity.getHealth() + entity.getAbsorptionAmount()) / (entity.getMaxHealth() + entity.getAbsorptionAmount())) * 80);

        if (space_lastEntity != entity) {
            space_animationX = percentageOfHealth;
        } else {
            space_animationX = RenderUtils.getAnimationStateEasing(space_animationX,percentageOfHealth,8);
        }

        String renderName = entity.getDisplayName().getFormattedText();

        if (renderName.isEmpty()) {
            renderName = entity.getName();
        }

        RenderUtils.drawRect(TargetHUDDesigner.Instance.getX(),TargetHUDDesigner.Instance.getY(),TargetHUDDesigner.Instance.getX() + 120,TargetHUDDesigner.Instance.getY() + 27.5,RenderUtils.getRGB(66,66,66));

        RenderUtils.drawBorderedRect(TargetHUDDesigner.Instance.getX() + 2,TargetHUDDesigner.Instance.getY() + 2,TargetHUDDesigner.Instance.getX() + 26,TargetHUDDesigner.Instance.getY() + 26,1, RenderUtils.blend(new Color(255,0,0),new Color(255,255,0),entity.hurtResistantTime / 20.0).getRGB(), 0);

        if (entity instanceof EntityPlayer) {
            RenderUtils.glColor(RenderUtils.blend(new Color(255,0,0),new Color(255,255,255),entity.hurtResistantTime / 20.0).getRGB());
            GlStateManager.enableBlend();
            final NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(entity.getUniqueID());

            if (playerInfo != null && playerInfo.hasLocationSkin()) {
                mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
                Gui.drawScaledCustomSizeModalRect((int) (TargetHUDDesigner.Instance.getX() + 3), (int) (TargetHUDDesigner.Instance.getY() + 3), 8.0f, 8.0f, 8, 8, 22, 22, 64.0f, 64.0f);

                if (((EntityPlayer) entity).isWearing(EnumPlayerModelParts.HAT)) {
                    Gui.drawScaledCustomSizeModalRect((int) (TargetHUDDesigner.Instance.getX() + 3), (int) (TargetHUDDesigner.Instance.getY() + 3),40.0F,8.0f,8,8,22, 22,64.0F,64.0F);
                }
            } else {
                mc.customFontRenderer.drawStringWithOutline("?",(float) (TargetHUDDesigner.Instance.getX() + 11),(float) (TargetHUDDesigner.Instance.getY() + 10),-1);
            }
        } else {
            mc.customFontRenderer.drawStringWithOutline("?",(float) (TargetHUDDesigner.Instance.getX() + 11),(float) (TargetHUDDesigner.Instance.getY() + 10),-1);
        }

        FontManager.default16.drawStringWithShadow(renderName,TargetHUDDesigner.Instance.getX() + 28,TargetHUDDesigner.Instance.getY()+ 1,-1);
        FontManager.default16.drawStringWithOutline("♥",TargetHUDDesigner.Instance.getX() + 28,TargetHUDDesigner.Instance.getY() + 11,RenderUtils.getRGB(255,0,0));
        RenderUtils.drawRect(TargetHUDDesigner.Instance.getX() + 38,TargetHUDDesigner.Instance.getY() + 10,TargetHUDDesigner.Instance.getX() + 118,TargetHUDDesigner.Instance.getY() + 20,RenderUtils.getRGB(0,0,0,80));
        RenderUtils.drawRect(TargetHUDDesigner.Instance.getX() + 38,TargetHUDDesigner.Instance.getY()+ 10,TargetHUDDesigner.Instance.getX()  + 38 + space_animationX,TargetHUDDesigner.Instance.getY() + 20,RenderUtils.blend(new Color(0,255,0),new Color(255,0,0),space_animationX / 100.0).getRGB());
        final String healthString = ((int) (entity.getHealth() + entity.getAbsorptionAmount())) + " / " + ((int) (entity.getMaxHealth() + entity.getAbsorptionAmount()));
        mc.customFontRenderer.drawStringWithOutline(healthString,(float) TargetHUDDesigner.Instance.getX() + 59,(float)TargetHUDDesigner.Instance.getY() + 11.5f,-1);
        space_lastEntity = entity;
    }

    public static void renderMinecraft(ScaledResolution scaledResolution,EntityLivingBase entity) {
        RenderUtils.resetColor();
        mc.fontRenderer.drawStringWithShadow(entity.getName(), scaledResolution.getScaledWidth() / 2.0f - mc.fontRenderer.getStringWidth(entity.getName()) / 2.0f, scaledResolution.getScaledHeight() / 2.0f - 30, 16777215);
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/icons.png"));

        int yOffset = 0;

        if (entity.getHealth() < 8) {
            yOffset += ThreadLocalRandom.current().nextInt(0, 4);
        }

        float maxHealth_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
        int maxHealth_Last = 9;
        int maxHealth_Y_Offset = 0;
        for (int i = 0; i < entity.getMaxHealth() / 2; i++) {
            mc.ingameGUI.drawTexturedModalRect(maxHealth_X, (float) (scaledResolution.getScaledHeight() / 2 - 20) + yOffset + maxHealth_Y_Offset, 16, 0, 9, 9);

            maxHealth_X += 10;

            if (i >= maxHealth_Last) {
                maxHealth_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
                maxHealth_Y_Offset += 4;
                maxHealth_Last += 10;
            }
        }

        float health_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
        int health_Last = 9;
        int health_Y_Offset = 0;
        boolean right = false;
        for (float i = 0; i < entity.getHealth() / 2; i += 0.5) {
            mc.ingameGUI.drawTexturedModalRect(health_X, (float) (scaledResolution.getScaledHeight() / 2 - 20) + yOffset + health_Y_Offset, 52 + (right ? 4 : 0), 0, 5, 9);
            health_X += 5;
            right = !right;

            if (i > health_Last) {
                health_X = (float) (scaledResolution.getScaledWidth() / 2) - 50;
                health_Y_Offset += 4;
                health_Last += 10;
                right = false;
            }
        }
    }
}
