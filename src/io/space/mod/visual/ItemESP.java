package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.Event3D;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;

public final class ItemESP extends Mod {
    public ItemESP() {
        super("ItemESP",Category.VISUAL);
    }

    @EventTarget
    public void on3D(Event3D e) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityItem)) continue;
            final EntityItem entityItem = (EntityItem) entity;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            double scale = 0.035;
            final double x = entityItem.lastTickPosX + (entityItem.posX - entityItem.lastTickPosX) * e.getPartialTicks() - mc.getRenderManager().viewerPosX;
            final double y = entityItem.lastTickPosY + (entityItem.posY - entityItem.lastTickPosY) * e.getPartialTicks() - mc.getRenderManager().viewerPosY;
            final double z = entityItem.lastTickPosZ + (entityItem.posZ - entityItem.lastTickPosZ) * e.getPartialTicks() - mc.getRenderManager().viewerPosZ;
            GL11.glTranslated(x, y + entityItem.height + 0.5, z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScaled(-(scale /= 2.0), -scale, -scale);
            final double xLeft = -15.0;
            final double xRight = 15.0;
            final double yUp = 10.0;
            final double yDown = 40;
            RenderUtils.drawBorderedRect(xLeft, yUp, xRight, yDown, 0.5, -1, 0);
            final ItemStack stack = entityItem.getEntityItem();
            final Item item = stack.getItem();
            if (item instanceof ItemAppleGold) {
                if (stack.hasEffect()) {
                    mc.customFontRenderer.drawStringWithOutline("GodApple", 0 - mc.customFontRenderer.getStringWidth("GodApple") / 2.0f, 0, RenderUtils.getRGB(255, 0, 0));
                } else {
                    mc.customFontRenderer.drawStringWithOutline("GoldApple", 0 - mc.customFontRenderer.getStringWidth("GoldApple") / 2.0f, 0, RenderUtils.getRGB(255, 221, 0));
                }
            } else if (item instanceof ItemSkull) {
                mc.customFontRenderer.drawStringWithOutline("Head", 0 - mc.customFontRenderer.getStringWidth("Head") / 2.0f, 0, RenderUtils.getRGB(0, 255, 204));
            } else if (item instanceof ItemEnchantedBook) {
                final ItemEnchantedBook itemEnchantedBook = (ItemEnchantedBook) item;
                mc.customFontRenderer.drawStringWithOutline("EnchantedBook", 0 - mc.customFontRenderer.getStringWidth("EnchantedBook") / 2.0f, 0, RenderUtils.getRGB(106, 0, 255));
                final NBTTagList nbtTagList = itemEnchantedBook.getEnchantments(stack);
                if (nbtTagList != null) {
                    final StringBuilder sb = new StringBuilder();
                    final Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(stack);
                    map.forEach((integer, integer2) -> sb.append(StatCollector.translateToLocal(Enchantment.getEnchantmentById(integer).getName())).append(" ").append(" Level:").append(integer2));
                    mc.customFontRenderer.drawStringWithOutline(sb.toString(), 0 - mc.customFontRenderer.getStringWidth(sb.toString()) / 2.0f, (float) yDown, RenderUtils.getRGB(255, 0, 213));
                }
            } else if (item instanceof ItemPotion) {
                final ItemPotion itemPotion = (ItemPotion) item;
                if (ItemPotion.isSplash(stack.getMetadata())) {
                    mc.customFontRenderer.drawStringWithOutline("SplashPotion", 0 - mc.customFontRenderer.getStringWidth("SplashPotion") / 2.0f, 0, RenderUtils.getRGB(255, 0, 213));
                } else {
                    mc.customFontRenderer.drawStringWithOutline("DrinkPotion", 0 - mc.customFontRenderer.getStringWidth("DrinkPotion") / 2.0f, 0, RenderUtils.getRGB(255, 0, 213));
                }
                for (PotionEffect effect : itemPotion.getEffects(stack)) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];
                    StringBuilder level = new StringBuilder();
                    for (int i = -1; i < effect.getAmplifier(); i++) {
                        level.append("I");
                    }
                    StringBuilder potionName = new StringBuilder(I18n.format(effect.getEffectName())).append(" ").append(level);
                    if (!(effect.getDuration() == 1)) {
                        potionName.append(" ").append((effect.getDuration() / 2) / 10).append("s");
                    }
                    mc.customFontRenderer.drawStringWithOutline(potionName.toString(), 0 - mc.customFontRenderer.getStringWidth(potionName.toString()) / 2.0f, (float) yDown, potion.getLiquidColor());
                }
            } else if (item instanceof ItemEnderPearl) {
                mc.customFontRenderer.drawStringWithOutline("EnderPearl", 0 - mc.customFontRenderer.getStringWidth("EnderPearl") / 2.0f, 0, RenderUtils.getRGB(157, 0, 255));
            } else if (item.getUnlocalizedName().equals("item.apple")) {
                mc.customFontRenderer.drawStringWithOutline("Apple", 0 - mc.customFontRenderer.getStringWidth("Apple") / 2.0f, 0, -1);
            } else if (item.getUnlocalizedName().equals(Items.diamond.getUnlocalizedName())) {
                final String displayName = "Diamond";
                mc.customFontRenderer.drawStringWithOutline(EnumChatFormatting.AQUA + displayName, 0 - mc.customFontRenderer.getStringWidth(displayName) / 2.0f, 0, -1);
            } else if (item.getUnlocalizedName().equals(Items.gold_ingot.getUnlocalizedName())) {
                final String displayName = "Gold-Ingot";
                mc.customFontRenderer.drawStringWithOutline(EnumChatFormatting.GOLD + displayName, 0 - mc.customFontRenderer.getStringWidth(displayName) / 2.0f, 0, -1);
            } else {
                final String displayName = stack.getDisplayName();
                mc.customFontRenderer.drawStringWithOutline(displayName, 0 - mc.customFontRenderer.getStringWidth(displayName) / 2.0f, 0, -1);

                if (stack.hasDisplayName()) {
                    mc.customFontRenderer.drawStringWithOutline(stack.getDisplayName(), 0 - mc.customFontRenderer.getStringWidth(stack.getDisplayName()) / 2.0f, 0, RenderUtils.getRGB(255, 0, 0));
                }
                if (stack.hasEffect()) {
                    final NBTTagList nbtTagList = stack.getEnchantmentTagList();
                    if (nbtTagList != null) {
                        final Map<Integer, Integer> map = EnchantmentHelper.getEnchantments(stack);
                        final int[] yEffect = {(int) yDown};
                        map.forEach((integer, integer2) -> {
                            StringBuilder sb = new StringBuilder();
                            sb.append(StatCollector.translateToLocal(Enchantment.getEnchantmentById(integer).getName())).append(" ").append(" Level:").append(integer2);
                            mc.customFontRenderer.drawStringWithOutline(sb.toString(), 0 - mc.customFontRenderer.getStringWidth(sb.toString()) / 2.0f, yEffect[0], RenderUtils.getRGB(255, 0, 213));
                            yEffect[0] += 10;
                        });
                    }
                }
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
}
