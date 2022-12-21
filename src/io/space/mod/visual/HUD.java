package io.space.mod.visual;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.chatcode.ChatCodeManager;
import io.space.events.Event2D;
import io.space.events.EventKey;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.network.NetworkClient;
import io.space.renderer.font.FontManager;
import io.space.renderer.gui.GuiLogin;
import io.space.renderer.gui.dropdown.模糊渲染程序;
import io.space.renderer.gui.dropdown.点击界面;
import io.space.utils.AnimationUtils;
import io.space.utils.ColorManager;
import io.space.utils.RenderUtils;
import io.space.value.values.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class HUD extends Mod {
    private final ModeValue arrayListFontMode = new ModeValue("ArrayListFontMode","Client",new String[]{"Client","Minecraft"});
    private final ModeValue arrayListColorMode = new ModeValue("ArrayListColorMode","Static",new String[]{"Static","Fade","Rainbow","DoubleFade"});
    private final ModeValue logoFontMode = new ModeValue("LogoFontMode","Client",new String[]{"Client","Minecraft"});
    private final ModeValue logoColorMode = new ModeValue("LogoColorMode","Static",new String[]{"Static","Rainbow"});
    private final ModeValue potionMode = new ModeValue("PotionMode","Text",new String[]{"Text","None"});
    private final NumberValue arrayListBackgroundAlpha = new NumberValue("ArrayListBackgroundAlpha",0,0,255,1);
    private final NumberValue logoBackgroundAlpha = new NumberValue("LogoBackgroundAlpha",0,0,255,1);
    private final TextValue clientName = new TextValue("ClientName","HmXixClient");
    private final ColorValue arrayListColor = new ColorValue("ArrayListColor",1,0,1);
    public final ColorValue logoColor = new ColorValue("LogoColor",1,0,1);
    private final BooleanValue tabGui = new BooleanValue("TabGui",false);
    private final BooleanValue info = new BooleanValue("Info",true);
    private final BooleanValue bg = new BooleanValue("BlurGround",true);
    public static final BooleanValue betterTooltip = new BooleanValue("BetterTooltip",false);
    public static final BooleanValue arrayListRect = new BooleanValue("ArrayListRect",false);

    public static HUD Instance;

    public HUD() {
        super("HUD",Category.VISUAL);
        registerValues(bg,arrayListFontMode,arrayListColorMode,logoFontMode,logoColorMode,potionMode,arrayListBackgroundAlpha,logoBackgroundAlpha,clientName,arrayListColor,logoColor,tabGui,info,betterTooltip,arrayListRect);

        Instance = this;
    }

    @SuppressWarnings("DuplicatedCode")
    @EventTarget
    public void on2D(Event2D e) {
        if (!mc.gameSettings.showDebugInfo) {
            final StringBuilder sb = new StringBuilder();
            sb.append(clientName.getValue()).append(" | ").append(NetworkClient.Instance.userName).append(" | ").append(Minecraft.getDebugFPS()).append("FPS").append(" | ").append(new SimpleDateFormat("hh:mm:ss").format(new Date()));
            String text = sb.toString();
            模糊渲染程序.blurArea(3, 3, FontManager.default18.getStringWidth(text) + 42, 12.5F);
            FontManager.default22.drawString(text, 5.5f,  2, 点击界面.颜色);
            FontManager.default22.drawString(text, 6,  2, -1);
            String r = String.format(EnumChatFormatting.GRAY + "Build - " + EnumChatFormatting.WHITE + "%s " + EnumChatFormatting.GRAY +"- User - " + EnumChatFormatting.WHITE + "%s", Wrapper.Instance.getClientVersion(), NetworkClient.Instance.userName);
            mc.fontRenderer.drawStringWithShadow(r, new ScaledResolution(mc).getScaledWidth() - mc.fontRenderer.getStringWidth(r) - 1, new ScaledResolution(mc).getScaledHeight() - FontManager.default18.getHeight() - 2, -1);
            if (tabGui.getValue()) {
                TabGui.render();
            }

            final ArrayList<Mod> mods = new ArrayList<>();

            final boolean useClientFont = arrayListFontMode.isCurrentMode("Client");

            for (Mod mod : ModManager.Instance.getModMap().values()) {
                if(mod.hide)
                    continue;
                if (mod.isEnable()) {
                    final int stringWidth = getStringWidth(mod.getModRenderNameWithTagNoColor());
                    mod.animationX = RenderUtils.getAnimationState(mod.animationX, stringWidth, Math.max(1, AnimationUtils.easing(mod.animationX, stringWidth, 10)));
                } else {
                    mod.animationX = RenderUtils.getAnimationState(mod.animationX, -4, AnimationUtils.easing(mod.animationX, -5, 10));
                }

                if (mod.animationX != -4) {
                    mods.add(mod);
                }

                if (!mod.isEnable() && mod.animationX == -4) {
                    mod.animationY = 0;
                }
            }

            mods.sort((o1, o2) -> getStringWidth(o2.getModRenderNameWithTagNoColor()) - getStringWidth(o1.getModRenderNameWithTagNoColor()));

            double textY = 2;
            int ticks = 0;

            for (Mod mod : mods) {
                final float directX = e.getScaledResolution().getScaledWidth() - 2.0f;

                final int staticColor = arrayListColor.getValue();
                final int height = useClientFont ? FontManager.default16.getHeight() + 2 : mc.fontRenderer.FONT_HEIGHT;
                int color = staticColor;

                switch (arrayListColorMode.getValue()) {
                    case "Fade":
                        ticks++;
                        color = fade(RenderUtils.getRed(staticColor),RenderUtils.getGreen(staticColor),RenderUtils.getBlue(staticColor),ticks);
                        ticks++;
                        break;
                    case "Rainbow":
                        color = RenderUtils.rainbow(3500.0f,1.0f,1.0f,ticks);
                        ticks++;
                        break;
                    case "DoubleFade":
                        color = ColorManager.interpolateColorsBackAndForth(15, ticks * 20, new Color(0x0059FF),  new Color(0x00FFC3), false).getRGB();
                        ticks++;
                        break;
                }

                if (mod.animationY != 0) {
                    mod.animationY = RenderUtils.getAnimationState(mod.animationY, textY, AnimationUtils.easing(mod.animationY, textY, 8));
                } else {
                    mod.animationY = textY;
                }

                if (bg.getValue()) {
                    final double renderY = mod.animationY;
                    RenderUtils.drawBlurRect((float) (directX - mod.animationX - 1), (float) renderY,directX + 2, (float) (renderY + height),RenderUtils.getRGB(0,0,0,arrayListBackgroundAlpha.getValue().intValue()));
                }

                if (arrayListRect.getValue()) {
                    final int stringWidth;

                    if (useClientFont) {
                        stringWidth = FontManager.default16.getStringWidth(mod.getModRenderNameWithTagNoColor());
                    } else {
                        stringWidth = mc.fontRenderer.getStringWidth(mod.getModRenderNameWithTagNoColor());
                    }

                    if (textY == 2.0) {
                        final double topX = directX + 2.0;
                        RenderUtils.drawRect(topX - stringWidth - 3.0, 2.0, topX, 3.0, color);
                    }

                    final double rX = directX + 2.0 + stringWidth - mod.animationX;

                    RenderUtils.drawRect(rX - 1.0, mod.animationY, rX, mod.animationY + height, color);
                }

                if (useClientFont) {
                    FontManager.default16.drawStringWithShadow(mod.getModRenderNameWithTag(), directX - mod.animationX, mod.animationY,color);
                } else {
                    mc.fontRenderer.drawStringWithShadow(mod.getModRenderNameWithTag(), directX - (float) mod.animationX, (float) mod.animationY,color);
                }

                textY += height;
            }
        }

        final boolean chatOpen = mc.ingameGUI.getChatGUI().getChatOpen();

        if (info.getValue()) {
            if (chatOpen) {
                FontManager.default16.drawStringWithShadow("Blocks: " + String.format("%.2f", Wrapper.getEntitySpeed(mc.player) * mc.timer.timerSpeed) + " X: " + ((int) mc.player.posX) + " Y:" + ((int) mc.player.posY) + " Z:" + ((int) mc.player.posZ) + " FPS:" + Minecraft.getDebugFPS(), 2, e.getScaledResolution().getScaledHeight() - FontManager.default16.getHeight() - 14, -1);
            } else {
                FontManager.default16.drawStringWithShadow("Blocks: " + String.format("%.2f", Wrapper.getEntitySpeed(mc.player) * mc.timer.timerSpeed), 2, e.getScaledResolution().getScaledHeight() - (FontManager.default16.getHeight() * 2) - 4, -1);
                FontManager.default16.drawStringWithShadow("X: " + ((int) mc.player.posX) + " Y:" + ((int) mc.player.posY) + " Z:" + ((int) mc.player.posZ) + " FPS:" + Minecraft.getDebugFPS(), 2, e.getScaledResolution().getScaledHeight() - FontManager.default16.getHeight() - 2, -1);
            }

            //FontManager.default16.drawStringWithShadow(NetworkClient.Instance.userName, e.getWidth() - FontManager.default16.getStringWidth(NetworkClient.Instance.userName) - 2.0, e.getHeight() - FontManager.default16.getHeight() - 2.0, -1);
        }

        if (!chatOpen) {
            if (potionMode.isCurrentMode("Text")) {
                int potionTextY = 18;
                for (PotionEffect effect : mc.player.getActivePotionEffects()) {
                    final Potion potion = Potion.potionTypes[effect.getPotionID()];

                    String renderString = I18n.format(potion.getName());
                    switch (effect.getAmplifier()) {
                        case 0:
                            renderString += " I";
                            break;
                        case 1:
                            renderString += " II";
                            break;
                        case 2:
                            renderString += " III";
                            break;
                        case 3:
                            renderString += " IV";
                            break;
                        default:
                            renderString += " " + (effect.getAmplifier() + 1);
                            break;
                    }

                    if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                        renderString = renderString + "§7:§6 " + Potion.getDurationString(effect);
                    } else if (effect.getDuration() < 300) {
                        renderString = renderString + "§7:§c " + Potion.getDurationString(effect);
                    } else if (effect.getDuration() > 600) {
                        renderString = renderString + "§7:§7 " + Potion.getDurationString(effect);
                    }

                    FontManager.default16.drawStringWithShadow(renderString,e.getScaledResolution().getScaledWidth() - FontManager.default16.getStringWidth(EnumChatFormatting.getTextWithoutFormattingCodes(renderString)) - 2,e.getScaledResolution().getScaledHeight() - 20 - potionTextY, potion.getLiquidColor());
                    potionTextY += 9;
                }
            }
        }
    }

    private int fade(int r,int g,int b, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(r,g,b,hsb);
        float brightness = Math.abs((System.currentTimeMillis() % 2000L / 1000.0f + 100.0f / count * 5) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return RenderUtils.getRGB(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    private int getStringWidth(String str) {
        return arrayListFontMode.isCurrentMode("Client") ? FontManager.default16.getStringWidth(str) : mc.fontRenderer.getStringWidth(str);
    }

    @EventTarget
    public void onKey(EventKey e) {
        if (tabGui.getValue())
            TabGui.onKey(e.getKeyCode());
    }

    private static class TabGui {
        private static Category currentCategory = Category.FIGHT;
        private static int currentCategoryIndex = 0;

        private static Mod currentMod;
        private static int currentModIndex = 0;
        private static boolean openingModList = false;

        private static void render() {
            double rectY = 22;

            RenderUtils.drawRect(2,22,40,106,RenderUtils.getRGB(0,0,0,80));

            for (Category value : Category.values()) {
                if (value == currentCategory) {
                    RenderUtils.drawRect(2,rectY,40,rectY + 12,RenderUtils.getRGB(255,0,255));
                    FontManager.default16.drawStringWithShadow(value.getName(),6,rectY + 1,-1);

                    if (openingModList) {
                        double modY = rectY;

                        for (Mod mod : ModManager.Instance.getCategoryMods(value)) {
                            RenderUtils.drawRect(42,modY,120,modY + 12,RenderUtils.getRGB(0,0,0,80));

                            if (mod == currentMod) {
                                RenderUtils.drawRect(42,modY,120,modY + 12,RenderUtils.getRGB(255,0,255,255));
                                FontManager.default16.drawStringWithShadow(mod.getRenderName(),46,modY + 1,mod.isEnable() ? -1 : RenderUtils.getRGB(150,150,150,255));
                            } else {
                                FontManager.default16.drawStringWithShadow(mod.getRenderName(),44,modY + 1,mod.isEnable() ? -1 : RenderUtils.getRGB(150,150,150,255));
                            }

                            modY += 12;
                        }
                    }
                } else {
                    FontManager.default16.drawStringWithShadow(value.getName(),4,rectY + 1,-1);
                }

                rectY += 12;
            }
        }

        private static void onKey(int keyCode) {
            if (keyCode == Keyboard.KEY_DOWN) {
                if (openingModList) {
                    final List<Mod> values = ModManager.Instance.getCategoryMods(currentCategory);
                    currentModIndex++;

                    if (currentModIndex >= values.size()) {
                        currentModIndex = 0;
                    }

                    currentMod = values.get(currentModIndex);
                } else {
                    final Category[] values = Category.values();
                    currentCategoryIndex++;

                    if (currentCategoryIndex >= values.length) {
                        currentCategoryIndex = 0;
                    }

                    currentCategory = values[currentCategoryIndex];
                }
            } else if (keyCode == Keyboard.KEY_UP) {
                if (openingModList) {
                    final List<Mod> values = ModManager.Instance.getCategoryMods(currentCategory);
                    currentModIndex--;

                    if (currentModIndex < 0) {
                        currentModIndex = values.size() - 1;
                    }

                    currentMod = values.get(currentModIndex);
                } else {
                    final Category[] values = Category.values();
                    currentCategoryIndex--;

                    if (currentCategoryIndex < 0) {
                        currentCategoryIndex = values.length - 1;
                    }

                    currentCategory = values[currentCategoryIndex];
                }
            } else if (keyCode == Keyboard.KEY_LEFT) {
                if (openingModList) {
                    currentMod = null;
                    openingModList = false;
                    currentModIndex = 0;
                }
            } else if (keyCode == Keyboard.KEY_RIGHT) {
                if (!openingModList) {
                    currentMod = ModManager.Instance.getCategoryMods(currentCategory).get(0);
                    openingModList = true;
                }
            } else if (keyCode == Keyboard.KEY_RETURN) {
                if (openingModList) {
                    if (currentMod != null) {
                        currentMod.toggle();
                    }
                }
            }
        }
    }
}
