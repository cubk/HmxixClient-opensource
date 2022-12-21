package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import com.google.common.collect.Ordering;
import io.space.events.Event3D;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import io.space.value.values.BooleanValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.util.MathUtils;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

public class NameTags extends Mod {
    public static final Ordering<NetworkPlayerInfo> targets = Ordering
            .from(new GuiPlayerTabOverlay.PlayerComparator());
    public BooleanValue armor = new BooleanValue("Armor",  true);
    public BooleanValue player_only = new BooleanValue("Player Only",  false);
    public NumberValue size = new NumberValue("Size", 3.0, 1.0, 5.0, 0.1);

    public NameTags() {
        super("NameTags", Mod.Category.VISUAL);
        registerValues(armor,player_only,size);
    }


    @EventTarget
    public void onRenderWorldLast(Event3D event) {
        for (Object object : mc.world.loadedEntityList) {
            if (object instanceof EntityPlayer || (!player_only.getValue() && object instanceof EntityLivingBase)) {
                if (((EntityLivingBase) object).isInvisible())
                    continue;
                EntityLivingBase entity = (EntityLivingBase) object;

//					if (entity == mc.thePlayer)
//						continue;
                RenderManager renderManager = mc.getRenderManager();
                double renderPosX = renderManager.viewerPosX;
                double renderPosY = renderManager.viewerPosY;
                double renderPosZ = renderManager.viewerPosZ;
                double xPos = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * event.getPartialTicks()) - renderPosX;
                double yPos = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * event.getPartialTicks()) - renderPosY;
                double zPos = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * event.getPartialTicks()) - renderPosZ;

                this.renderNameTag(entity, entity.getDisplayName().getFormattedText(), xPos, yPos, zPos);
            }
        }
    }

    public void renderNameTag(Entity entity, String tag, double x, double y, double z) {
        if (entity instanceof EntityArmorStand) {
            return;
        }

        EntityPlayerSP player = mc.player;
        y += (entity.isSneaking() ? 0.5D : 0.7D);

        float sizes = 5f - size.getValue().floatValue();

        float distance = player.getDistanceToEntity(entity) / sizes;
        if (distance < 1.6F) {
            distance = 1.6F;
        }

        if (entity instanceof EntityLivingBase) {
            int health = (int) ((EntityLivingBase) entity).getHealth();
            String colorText = "";
            if (health <= ((EntityLivingBase) entity).getMaxHealth() * 0.25D) {
                colorText = "\u00a74";
            } else if (health <= ((EntityLivingBase) entity).getMaxHealth() * 0.5D) {
                colorText = "\u00a76";
            } else if (health <= ((EntityLivingBase) entity).getMaxHealth() * 0.75D) {
                colorText = "\u00a7e";
            } else if (health <= ((EntityLivingBase) entity).getMaxHealth()) {
                colorText = "\u00a72";
            }

            String suffix = "";

            tag = EnumChatFormatting.GREEN + "[" + EnumChatFormatting.GOLD + ((int) mc.player.getDistanceToEntity(entity)) + EnumChatFormatting.GREEN + "] " + suffix + tag + EnumChatFormatting.GREEN + " [" + colorText + Math.round(health) + EnumChatFormatting.RED + "❤" + EnumChatFormatting.GREEN + "]";
        }



        RenderManager renderManager = mc.getRenderManager();
        float scale = distance;
        scale /= 30.0F;
        scale = (float) (scale * 0.3D);
        GL11.glPushMatrix();
        //GlStateManager.enableBlend();
        GL11.glTranslatef((float) x, (float) y + 1.4F, (float) z);
        GL11.glNormal3f(1.0F, 1.0F, 1.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);

//		GL11.glDisable(GL_LIGHTING);
        GL11.glDisable(GL_DEPTH_TEST);

        if (entity instanceof EntityPlayer) {
            int width = mc.fontRenderer.getStringWidth(tag) / 2;
            RenderUtils.drawRect(- width - 2, - (mc.fontRenderer.FONT_HEIGHT + 1), width + 2 + mc.fontRenderer.FONT_HEIGHT + 3, 2.0F, new Color(0, 0, 0, 144).getRGB());
            for (NetworkPlayerInfo info : targets.sortedCopy(mc.getNetHandler().getPlayerInfoMap())) {
                if (mc.world.getPlayerEntityByUUID(info.getGameProfile().getId()) == entity) {
                    mc.getTextureManager().bindTexture(info.getLocationSkin());
                    drawScaledCustomSizeModalRect(- width - 2, - (mc.fontRenderer.FONT_HEIGHT + 1), 8.0f, 8.0f, 8, 8, mc.fontRenderer.FONT_HEIGHT + 3, mc.fontRenderer.FONT_HEIGHT + 3, 64.0f, 64.0f);
                    GlStateManager.bindTexture(0);
                    break;
                }
            }
            GlStateManager.resetColor();
            mc.fontRenderer.drawString(tag, MathUtils.getMiddle(- width - 2, width + 2) - width + mc.fontRenderer.FONT_HEIGHT + 4, - (mc.fontRenderer.FONT_HEIGHT - 1), Color.WHITE.getRGB());
        } else {
            int width = mc.fontRenderer.getStringWidth(tag) / 2;
            RenderUtils.drawRect(- width - 2, - (mc.fontRenderer.FONT_HEIGHT + 1), width + 2, 2.0F, new Color(0, 0, 0, 144).getRGB());
            GlStateManager.resetColor();
            mc.fontRenderer.drawString(tag, MathUtils.getMiddle(- width - 2, width + 2) - width, - (mc.fontRenderer.FONT_HEIGHT - 1), Color.WHITE.getRGB());
        }

        if (entity instanceof EntityPlayer && this.armor.getValue()) {
            EntityPlayer entityPlayer = (EntityPlayer) entity;
            GlStateManager.translate(0.0F, 1.0F, 0.0F);
            renderArmor(entityPlayer, 0, - (mc.fontRenderer.FONT_HEIGHT + 1) - 20);
            GlStateManager.translate(0.0F, - 1.0F, 0.0F);
        }
//		GL11.glEnable(GL_LIGHTING);
        GL11.glEnable(GL_DEPTH_TEST);
        //GL11.glDisable(GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        //GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public void drawScaledCustomSizeModalRect (float x, float y, float u, float v, float uWidth, float vHeight, float width, float height, float tileWidth, float tileHeight) {
        float f = 1.0F / tileWidth;
        float f1 = 1.0F / tileHeight;
        GL11.glColor4f(1, 1, 1, 1);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, y + height, 0.0D).tex(u * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y + height, 0.0D).tex((u + uWidth) * f, (v + vHeight) * f1).endVertex();
        bufferbuilder.pos(x + width, y, 0.0D).tex((u + uWidth) * f, v * f1).endVertex();
        bufferbuilder.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
        tessellator.draw();
    }


    public void renderArmor (EntityPlayer player, int x, int y) {
        InventoryPlayer items = player.inventory;
        ItemStack inHand = player.getHeldItem();
        ItemStack boots = items.armorItemInSlot(0);
        ItemStack leggings = items.armorItemInSlot(1);
        ItemStack body = items.armorItemInSlot(2);
        ItemStack helm = items.armorItemInSlot(3);
        ItemStack[] stuff;
        if (inHand != null) {
            stuff = new ItemStack[]{inHand, helm, body, leggings, boots};
        } else {
            stuff = new ItemStack[]{helm, body, leggings, boots};
        }
        List<ItemStack> stacks = new ArrayList<>();
        ItemStack[] array;
        int length = (array = stuff).length;

        for (int j = 0; j < length; j++) {
            ItemStack i = array[j];
            if ((i != null) && (i.getItem() != null)) {
                stacks.add(i);
            }
        }
        int width = 16 * stacks.size() / 2;
        x -= width;
        GlStateManager.disableDepth();
        for (ItemStack stack : stacks) {
            renderItem(stack, x, y);
            x += 16;
        }
        GlStateManager.enableDepth();
    }

    public void renderItem(ItemStack stack, int x, int y) {
        FontRenderer fontRenderer = mc.fontRenderer;
        RenderItem renderItem = mc.getRenderItem();
        EnchantEntry[] enchants = {
                new EnchantEntry(Enchantment.protection, "Pro"),
                new EnchantEntry(Enchantment.thorns, "Th"),
                new EnchantEntry(Enchantment.sharpness, "Shar"),
                new EnchantEntry(Enchantment.fireAspect, "Fire"),
                new EnchantEntry(Enchantment.knockback, "Kb"),
                new EnchantEntry(Enchantment.unbreaking, "Unb"),
                new EnchantEntry(Enchantment.power, "Pow"),
                new EnchantEntry(Enchantment.infinity, "Inf"),
                new EnchantEntry(Enchantment.punch, "Punch")
        };
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - 3, y + 10, 0.0F);
        GlStateManager.scale(0.3F, 0.3F, 0.3F);
        GlStateManager.popMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        renderItem.zLevel = -100.0F;
        GlStateManager.disableDepth();
        renderItem.renderItemIntoGUI(stack, x, y);
        renderItem.renderItemOverlayIntoGUI(fontRenderer, stack, x, y, null);
        GlStateManager.enableDepth();
        EnchantEntry[] array;
        int length = (array = enchants).length;
        for (int i = 0; i < length; i++) {
            EnchantEntry enchant = array[i];
            int level = EnchantmentHelper.getEnchantmentLevel(enchant.getEnchant().effectId, stack);
            String levelDisplay = "" + level;
            if (level > 10) {
                levelDisplay = "10+";
            }
            if (level > 0) {
                GlStateManager.translate(x - 2, y + 1, 0.0F);
                GlStateManager.scale(0.42F, 0.42F, 0.42F);
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                fontRenderer.drawString("\u00a7f" + enchant.getName() + " " + levelDisplay,
                        20 - fontRenderer.getStringWidth("\u00a7f" + enchant.getName() + " " + levelDisplay) / 2, 0, Color.WHITE.getRGB(), true);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.scale(2.42F, 2.42F, 2.42F);
                GlStateManager.translate(-x, -y, 0.0F);
                y += (int) ((fontRenderer.FONT_HEIGHT + 3) * 0.28F);
            }
        }
        renderItem.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    public static class EnchantEntry {
        private final Enchantment enchant;
        private final String name;

        public EnchantEntry(Enchantment enchant, String name) {
            this.enchant = enchant;
            this.name = name;
        }

        public Enchantment getEnchant() {
            return this.enchant;
        }

        public String getName() {
            return this.name;
        }
    }
}
