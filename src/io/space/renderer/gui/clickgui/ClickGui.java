package io.space.renderer.gui.clickgui;

import io.space.Wrapper;
import io.space.cape.CapeManager;
import io.space.cape.CapeObject;
import io.space.config.ConfigManager;
import io.space.config.CustomConfig;
import io.space.designer.GuiDesigner;
import io.space.global.GlobalSetting;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.renderer.font.FontManager;
import io.space.renderer.gui.dropdown.取色器;
import io.space.utils.RenderUtils;
import io.space.value.Value;
import io.space.value.values.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.stream.Collectors;

public final class ClickGui extends GuiScreen {
    private static final ArrayList<ModObject> modObjects = new ArrayList<>();
    private static double mainX = 2,mainY = 2,lastMainX = 2,lastMainY = 2;
    private static double modCurrentY, modAnimationY;
    private static double configCurrentY, configAnimationY;
    private static double capeCurrentY, capeAnimationY;
    private boolean dragging;
    public static 取色器 取色器 = null;

    private static Type selectType = Type.HOME;
    private static Mod.Category selectCategory = Mod.Category.FIGHT;

    private final LinkedList<GuiTextField> modTextFields = new LinkedList<>();

    private boolean mouseLeftDown,mouseRightDown;

    static {
        for (Mod value : ModManager.Instance.getModMap().values()) {
            modObjects.add(new ModObject(value));
        }
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution scaledResolution = Wrapper.Instance.getScaledResolution();
        modTextFields.clear();

        if (dragging) {
            mainX = mouseX + lastMainX;
            mainY = mouseY + lastMainY;
        }

        if (mainX < 0) {
            mainX = 0;
        }

        if (mainY < 0) {
            mainY = 0;
        }

        if (mainX + 300 > scaledResolution.getScaledWidth_double()) {
            mainX = scaledResolution.getScaledWidth_double() - 300;
        }

        if (mainY + 180 > scaledResolution.getScaledHeight_double()) {
            mainY = scaledResolution.getScaledHeight_double() - 180;
        }

        modAnimationY = RenderUtils.getAnimationStateEasing(modAnimationY, modCurrentY,7);
        configAnimationY = RenderUtils.getAnimationStateEasing(configAnimationY,configCurrentY,7);
        capeAnimationY = RenderUtils.getAnimationStateEasing(capeAnimationY,capeCurrentY,7);

        RenderUtils.drawRect(mainX,mainY,mainX + 300,mainY + 180,RenderUtils.getRGB(40,40,40));

        double typeX = mainX + 5;
        for (Type value : Type.values()) {
            final String renderName = value.getRenderName();
            final int stringWidth = FontManager.default16.getStringWidth(renderName);

            FontManager.default16.drawStringWithShadow(renderName,typeX,mainY + 5,selectType == value ? -1 : RenderUtils.getRGB(128,128,128));

            if (Mouse.isButtonDown(0) && Wrapper.isHovered(typeX,mainY + 5,typeX + stringWidth,mainY + 12,mouseX,mouseY)) {
                selectType = value;
            }

            typeX += stringWidth + 8;
        }

        if (selectType == Type.HOME) {
            RenderUtils.drawRect(mainX,mainY + 20,mainX + 50,mainY + 127,RenderUtils.getRGB(43,43,43));

            double categoryY = mainY + 22;
            for (Mod.Category category : Mod.Category.values()) {
                if (Wrapper.isHovered(mainX, categoryY, mainX + 50, categoryY + 14, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    selectCategory = category;
                    modAnimationY = 0;
                    modCurrentY = 0;
                }

                if (category == selectCategory) {
                    FontManager.default16.drawStringWithShadow(category.getName(), mainX + 23 - (FontManager.default16.getStringWidth(category.getName()) / 2.0), categoryY + 2.5, RenderUtils.getRGB(0, 80, 180));
                } else {
                    FontManager.default16.drawStringWithShadow(category.getName(), mainX + 23 - (FontManager.default16.getStringWidth(category.getName()) / 2.0), categoryY + 2.5, -1);
                }

                categoryY += 15;
            }

            final boolean mouseInScreen = Wrapper.isHovered(mainX + 53, mainY + 22, mainX + 300, mainY + 180, mouseX, mouseY);
            double modX = mainX + 53;
            double modY = mainY + 22 - modAnimationY;
            double added = 0;

            RenderUtils.startGlScissor((int) mainX + 53, (int) mainY + 22, 247, 158);
            for (ModObject modObject : modObjects.stream().filter((modObject -> modObject.mod.getCategory() == selectCategory)).collect(Collectors.toList())) {
                if (Wrapper.isHovered(modX, modY, modX + 245, modY + 15, mouseX, mouseY)) {
                    modObject.animationX = RenderUtils.getAnimationStateEasing(modObject.animationX, 245, 8);

                    if (mouseInScreen && canClickLeft()) {
                        modObject.mod.toggle();
                    }

                    if (mouseInScreen && canClickRight()) {
                        modObject.showValue = !modObject.showValue;
                    }
                } else {
                    modObject.animationX = RenderUtils.getAnimationStateEasing(modObject.animationX, 0, 6);
                }

                RenderUtils.resetColor();
                RenderUtils.drawRect(modX, modY, modX + 1, modY + 15, modObject.mod.isEnable() ? -1 : RenderUtils.getRGB(90, 90, 90));
                RenderUtils.drawRect(modX + 1, modY, modX + 245, modY + 15, RenderUtils.getRGB(50, 50, 50));
                RenderUtils.drawRect(modX + 1, modY, modX + 1 + modObject.animationX, modY + 15, RenderUtils.getRGB(180, 180, 180, 50));
                FontManager.default16.drawStringWithShadow(modObject.mod.getRenderName(), modX + 5, modY + 3, modObject.mod.isEnable() ? -1 : RenderUtils.getRGB(90, 90, 90));

                if (!modObject.mod.getValues().isEmpty()) {
                    FontManager.default16.drawString("...", modX + 230, modY + 1, modObject.showValue ? RenderUtils.getRGB(0, 150, 255) : -1);

                    if (modObject.showValue) {
                        @SuppressWarnings("UnnecessaryLocalVariable")
                        double valueX = modX;
                        double valueY = modY + 16;

                        for (Value<?> value : modObject.mod.getValues()) {
                            if (value instanceof ModeValue) {
                                final ModeValue modeValue = (ModeValue) value;
                                FontManager.default16.drawStringWithShadow(modeValue.getValueName(), valueX, valueY, -1);

                                for (int i = 0; i < modeValue.getModes().length; i++) {
                                    final String mode = modeValue.getModes()[i];
                                    FontManager.default16.drawStringWithShadow(mode, valueX + 4, valueY + 10, modeValue.isCurrentMode(mode) ? RenderUtils.getRGB(0, 255, 0) : RenderUtils.getRGB(120,120,120));

                                    if (mouseInScreen && Wrapper.isHovered(valueX + 4, valueY + 10, valueX + 4 + FontManager.default16.getStringWidth(mode), valueY + 10 + FontManager.default16.getHeight(), mouseX, mouseY) && canClickLeft()) {
                                        modeValue.setValue(mode);
                                    }

                                    if (i != modeValue.getModes().length - 1) {
                                        valueY += 11;
                                        modY += 11;
                                        added += 11;
                                    }
                                }

                                modY += 20;
                                valueY += 20;
                                added += 20;
                            } else if (value instanceof BooleanValue) {
                                final BooleanValue booleanValue = (BooleanValue) value;

                                if (booleanValue.getValue() && booleanValue.animationX == 0.0) {
                                    booleanValue.animationX = 13.0;
                                }

                                final double xPos = valueX + 150;
                                final double yPos = valueY - 1;
                                FontManager.default16.drawStringWithShadow(value.getValueName(), valueX, valueY + 1, -1);

                                if (booleanValue.getValue()) {
                                    RenderUtils.drawRoundedRect(xPos, yPos, xPos + 24.0, yPos + 12.0, 5.0f, RenderUtils.getRGB(34, 94, 181));
                                    RenderUtils.drawCircle(xPos + 6 + booleanValue.animationX, yPos + 6.0, 4.0, 3, true, -1);
                                } else {
                                    RenderUtils.drawRoundedRect(xPos, yPos, xPos + 24.0, yPos + 12.0, 5.0f, RenderUtils.getRGB(50, 49, 53));
                                    RenderUtils.drawRoundedRect(xPos + 1, yPos + 1.0, xPos + 23.0, yPos + 11.0, 4.0f, RenderUtils.getRGB(31, 27, 31));
                                    RenderUtils.drawCircle(xPos + 6 + booleanValue.animationX, yPos + 6.0, 4.0, 3, true, RenderUtils.getRGB(50, 49, 53));
                                }

                                if (mouseInScreen && Wrapper.isHovered(xPos, yPos, xPos + 25.0, yPos + 12.0, mouseX, mouseY) && canClickLeft()) {
                                    booleanValue.setValue(!booleanValue.getValue());
                                }

                                booleanValue.animationX = RenderUtils.getAnimationStateEasing(booleanValue.animationX, booleanValue.getValue() ? 13.0 : 0.0, 5);

                                modY += 15;
                                valueY += 15;
                                added += 15;
                            } else if (value instanceof NumberValue) {
                                final NumberValue numberValue = (NumberValue) value;
                                FontManager.default16.drawStringWithShadow(numberValue.getValueName() + "  " + numberValue.getValue(), valueX, valueY - 2, -1);
                                RenderUtils.drawRect(valueX, valueY + 8, valueX + 245, valueY + 18, RenderUtils.getRGB(20, 20, 20));

                                final double render = 245.0 * ((numberValue.getValue() - numberValue.getMin()) / (numberValue.getMax() - numberValue.getMin()));

                                if (mouseInScreen && Wrapper.isHovered(valueX, valueY + 8, valueX + 245, valueY + 18, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                    final double min = numberValue.getMin();
                                    final double max = numberValue.getMax();
                                    final double inc = numberValue.getIncrease();
                                    final double valAbs = mouseX - valueX;
                                    double perc = valAbs / 245;
                                    perc = Math.min(Math.max(0.0, perc), 1.0);
                                    final double valRel = (max - min) * perc;
                                    double val = min + valRel;
                                    val = Math.round(val * (1.0 / inc)) / (1.0 / inc);
                                    numberValue.setValue(val);
                                }

                                numberValue.animationX = RenderUtils.getAnimationStateEasing(numberValue.animationX, render, 10);
                                RenderUtils.drawRect(valueX, valueY + 8, valueX + numberValue.animationX, valueY + 18, RenderUtils.getRGB(34, 94, 181));
                                modY += 20;
                                valueY += 20;
                                added += 20;
                            } else if (value instanceof TextValue) {
                                final TextValue textValue = ((TextValue) value);

                                FontManager.default16.drawStringWithShadow(textValue.getValueName(), valueX, valueY, -1);

                                RenderUtils.drawBorderedRect(valueX + 5,valueY + 9,valueX + 145,valueY + 21,1,Wrapper.isHovered(valueX + 5,valueY + 9,valueX + 145,valueY + 21,mouseX,mouseY) ? RenderUtils.getRGB(150,150,150) : RenderUtils.getRGB(100,100,100),RenderUtils.getRGB(0,0,0));

                                textValue.getGuiTextField().xPosition = (int) valueX + 7;
                                textValue.getGuiTextField().yPosition = (int) valueY + 11;
                                textValue.getGuiTextField().width = 140;
                                textValue.getGuiTextField().height = 10;
                                textValue.getGuiTextField().setVisible(true);
                                textValue.getGuiTextField().drawTextBox();
                                textValue.setValue(textValue.getGuiTextField().getText());
                                textValue.getGuiTextField().setEnableBackgroundDrawing(false);

                                if (valueY < mainY + 22) {
                                    textValue.getGuiTextField().setFocused(false);
                                }

                                modTextFields.add(textValue.getGuiTextField());

                                modY += 24;
                                valueY += 24;
                                added += 24;
                            } else if (value instanceof ColorValue) {
                                final ColorValue colorValue = (ColorValue) value;
                                final double OFFSET = 30;
                                final int valueColor = RenderUtils.getRGB(Color.HSBtoRGB(colorValue.getHue(), colorValue.getSaturation(), colorValue.getBrightness()));
                                RenderUtils.drawRect(valueX, valueY + 18, valueX + 245, valueY + 28,valueColor);
                                colorValue.setValue(valueColor);

                                FontManager.default16.drawStringWithShadow(colorValue.getValueName(), valueX, valueY, -1);
                                FontManager.default16.drawStringWithShadow("Hex:" + colorValue.getValue(),valueX + 2,valueY + 8.5,-1);
                                FontManager.default16.drawStringWithShadow("Hue:" + colorValue.getHue(),valueX + 2,valueY + 28.5,-1);
                                FontManager.default16.drawStringWithShadow("Saturation:" + colorValue.getSaturation(),valueX + 2,valueY + 48.5,-1);
                                FontManager.default16.drawStringWithShadow("Brightness:" + colorValue.getBrightness(),valueX + 2,valueY + 68.5,-1);

                                for (int i = 0; i < 245; i++) {
                                    final float percentage = i / 244.0f;

                                    RenderUtils.drawRect(valueX + i, valueY + 8 + OFFSET, valueX + i + 1, valueY + 18 + OFFSET,new Color(Color.HSBtoRGB(percentage,1.0f,1.0f)).getRGB());
                                    if (colorValue.getHue() == percentage) {
                                        RenderUtils.drawRect(valueX + i,valueY + 8 + OFFSET,valueX + i + 1,valueY + 18 + OFFSET,RenderUtils.getRGB(255,255,255));
                                    } else if (valueY + 8 + OFFSET >= mainY + 22 && Mouse.isButtonDown(0) && Wrapper.isHovered(valueX + i,valueY + 8 + OFFSET,valueX + i + 0.5,valueY + 18 + OFFSET,mouseX,mouseY)) {
                                        RenderUtils.drawRect(valueX + i,valueY + 8 + OFFSET,valueX + i + 1,valueY + 18 + OFFSET,RenderUtils.getRGB(255,255,255));
                                        colorValue.setHue(percentage);
                                    }

                                    RenderUtils.drawRect(valueX + i,valueY + 28 + OFFSET,valueX + i + 1,valueY + 38 + OFFSET,RenderUtils.getRGB(Color.HSBtoRGB(0.67f,percentage,1.0f)));
                                    if (colorValue.getSaturation() == percentage) {
                                        RenderUtils.drawRect(valueX + i,valueY + 28 + OFFSET,valueX + i + 1,valueY + 38 + OFFSET,RenderUtils.getRGB(220,220,0));
                                    } else if (valueY + 28 + OFFSET >= mainY + 22 && Mouse.isButtonDown(0) && Wrapper.isHovered(valueX + i,valueY + 28 + OFFSET,valueX + i + 0.5,valueY + 38 + OFFSET,mouseX,mouseY)) {
                                        RenderUtils.drawRect(valueX + i,valueY + 28 + OFFSET,valueX + i + 1,valueY + 38 + OFFSET,RenderUtils.getRGB(220,220,0));
                                        colorValue.setSaturation(percentage);
                                    }

                                    RenderUtils.drawRect(valueX + i,valueY + 48 + OFFSET,valueX + i + 1,valueY + 58 + OFFSET,RenderUtils.getRGB(Color.HSBtoRGB(0.67f,1.0f, percentage)));
                                    if (colorValue.getBrightness() == percentage) {
                                        RenderUtils.drawRect(valueX + i,valueY + 48 + OFFSET,valueX + i + 1,valueY + 58 + OFFSET,RenderUtils.getRGB(255,255,255));
                                    } else if (valueY + 48 + OFFSET >= mainY + 22 && Mouse.isButtonDown(0) && Wrapper.isHovered(valueX + i,valueY + 48 + OFFSET,valueX + i + 0.5,valueY + 58 + OFFSET,mouseX,mouseY)) {
                                        RenderUtils.drawRect(valueX + i,valueY + 48 + OFFSET,valueX + i + 1,valueY + 58 + OFFSET,RenderUtils.getRGB(255,255,255));
                                        colorValue.setBrightness(percentage);
                                    }
                                }

                                modY += 90;
                                valueY += 90;
                                added += 90;
                            }
                        }
                    }
                }

                added += 20;
                modY += 20;
            }

            RenderUtils.stopGlScissor();

            if (modCurrentY > added - 160) {
                modCurrentY = Math.max(0, added - 160);
            }

            if (modCurrentY < 0) {
                modCurrentY = 0;
            }

            if (Mouse.hasWheel()) {
                final int wheel = Mouse.getDWheel();

                if (wheel != 0) {
                    if (wheel < 0) {//Down
                        modCurrentY += 30;
                    } else {//Up
                        modCurrentY -= 30;
                    }
                }
            }
        } else if (selectType == Type.CONFIG) {
            { //Button add config
                RenderUtils.drawRect(mainX + 5, mainY + 22, mainX + 30, mainY + 33, RenderUtils.getRGB(0, 0, 0, 50));
                FontManager.default16.drawString("添加", mainX + 9, mainY + 22, -1);

                if (Wrapper.isHovered(mainX + 5, mainY + 22, mainX + 30, mainY + 33, mouseX, mouseY)) {
                    RenderUtils.drawRect(mainX + 5, mainY + 22, mainX + 30, mainY + 33, RenderUtils.getRGB(0, 0, 0, 50));

                    if (canClickLeft()) {
                        mc.displayGuiScreen(new GuiAddConfig(this));
                    }
                }
            }

            final int maxY = ConfigManager.Instance.getCustomConfigList().size() * 20 - 160;
            double configX = mainX + 35;
            double configY = mainY + 22 - configAnimationY;

            final boolean canClick = Wrapper.isHovered(configX,mainY + 22,configX + 260,mainY + 180,mouseX,mouseY);

            RenderUtils.startGlScissor((int) configX,(int) mainY + 22,260,158);
            for (CustomConfig config : ConfigManager.Instance.getCustomConfigList()) {
                if ((configY < mainY) || (configY > mainY + 180)) {
                    configY += 20;
                    continue;
                }

                RenderUtils.drawRect(configX,configY,configX + 260,configY + 15,RenderUtils.getRGB(32, 31, 35));
                RenderUtils.drawRect(configX,configY,configX + 1,configY + 15,RenderUtils.getRGB(90, 90, 90));
                FontManager.default16.drawString(config.getName(),configX + 5,configY + 3,-1);

                { //Button load
                    RenderUtils.drawRect(configX + 240,configY,configX + 260,configY + 15,RenderUtils.getRGB(0,150,50));
                    FontManager.default16.drawString("加载", configX + 241,configY + 3, -1);

                    if (Wrapper.isHovered(configX + 240,configY,configX + 260,configY + 15, mouseX, mouseY) && canClick) {
                        RenderUtils.drawRect(configX + 240,configY,configX + 260,configY + 15, RenderUtils.getRGB(0, 0, 0, 50));

                        if (canClickLeft()) {
                            try {
                                ConfigManager.Instance.loadConfig(config);
                                NotificationManager.Instance.addNotification("Config","成功加载Config " + config.getName(), Notification.NotificationType.SUCCESS,5000);
                            } catch (Throwable e) {
                                e.addSuppressed(new Throwable("Failed to load config"));
                                e.printStackTrace();

                                NotificationManager.Instance.addNotification("Config","加载Config失败 " + config.getName() + " 原因:" + e.getClass().getName() + ":" + e.getMessage(), Notification.NotificationType.SUCCESS,5000);
                            }
                        }
                    }
                }

                { //Button update
                    RenderUtils.drawRect(configX + 220,configY,configX + 239,configY + 15,RenderUtils.getRGB(0,150,150));
                    FontManager.default16.drawString("更新", configX + 221,configY + 3, -1);

                    if (Wrapper.isHovered(configX + 220,configY,configX + 239,configY + 15, mouseX, mouseY) && canClick) {
                        RenderUtils.drawRect(configX + 220,configY,configX + 239,configY + 15, RenderUtils.getRGB(0, 0, 0, 50));

                        if (canClickLeft()) {
                            try {
                                ConfigManager.Instance.saveConfig(config);
                                NotificationManager.Instance.addNotification("Config","成功更新Config " + config.getName(), Notification.NotificationType.SUCCESS,5000);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                { //Button remove
                    RenderUtils.drawRect(configX + 200,configY,configX + 219,configY + 15,RenderUtils.getRGB(200,0,50));
                    FontManager.default16.drawString("删除", configX + 201,configY + 3, -1);

                    if (Wrapper.isHovered(configX + 200,configY,configX + 219,configY + 15, mouseX, mouseY) && canClick) {
                        RenderUtils.drawRect(configX + 200,configY,configX + 219,configY + 15, RenderUtils.getRGB(0, 0, 0, 50));

                        if (canClickLeft()) {
                            ConfigManager.Instance.removeCustomConfig(config);
                            NotificationManager.Instance.addNotification("Config","成功删除Config " + config.getName(), Notification.NotificationType.SUCCESS,5000);
                        }
                    }
                }

                configY += 20;
            }
            RenderUtils.stopGlScissor();

            if (configCurrentY > maxY) {
                configCurrentY = Math.max(0,maxY);
            }

            if (configCurrentY < 0) {
                configCurrentY = 0;
            }

            if (Mouse.hasWheel()) {
                final int wheel = Mouse.getDWheel();

                if (wheel != 0) {
                    if (wheel < 0) {//Down
                        configCurrentY += 30;
                    } else {//Up
                        configCurrentY -= 30;
                    }
                }
            }
        } else if (selectType == Type.DESIGNER) {
            selectType = Type.HOME;
            mc.displayGuiScreen(new GuiDesigner());
        } else if (selectType == Type.SETTING) {
            final double xPos = mainX + 5;
            double yPos = mainY + 20;

            for (BooleanValue value : GlobalSetting.Instance.getValues().values()) {
                if (value == GlobalSetting.Instance.getFakeForge()) continue;

                if (value.getValue() && value.animationX == 0.0) {
                    value.animationX = 13.0;
                }

                FontManager.default16.drawStringWithShadow(value.getValueName(),xPos + 30, yPos + 2, -1);

                if (value.getValue()) {
                    RenderUtils.drawRoundedRect(xPos, yPos, xPos + 24.0, yPos + 12.0, 5.0f, RenderUtils.getRGB(34, 94, 181));
                    RenderUtils.drawCircle(xPos + 6 + value.animationX, yPos + 6.0, 4.0, 3, true, -1);
                } else {
                    RenderUtils.drawRoundedRect(xPos, yPos, xPos + 24.0, yPos + 12.0, 5.0f, RenderUtils.getRGB(50, 49, 53));
                    RenderUtils.drawRoundedRect(xPos + 1, yPos + 1.0, xPos + 23.0, yPos + 11.0, 4.0f, RenderUtils.getRGB(31, 27, 31));
                    RenderUtils.drawCircle(xPos + 6 + value.animationX, yPos + 6.0, 4.0, 3, true, RenderUtils.getRGB(50, 49, 53));
                }

                if (Wrapper.isHovered(xPos, yPos, xPos + 25.0, yPos + 12.0, mouseX, mouseY) && canClickLeft()) {
                    value.setValue(!value.getValue());
                }

                value.animationX = RenderUtils.getAnimationStateEasing(value.animationX,value.getValue() ? 13.0 : 0.0, 5);

                yPos += 20;
            }
        } else if (selectType == Type.CAPE) {
            double capeY = mainY + 22 - capeAnimationY;

            final int maxY = CapeManager.Instance.getCapes().size() * 99;

            RenderUtils.startGlScissor((int) mainX + 2,(int) mainY + 22,298,158);

            for (CapeObject cape : CapeManager.Instance.getCapes()) {
                final boolean isCurrentCape = cape == CapeManager.Instance.getCurrentCape();

                RenderUtils.drawImage(mainX + 3,capeY + 1,99,99,-1,cape.getResourceLocation());
                RenderUtils.drawBorderedRect(mainX + 2,capeY,mainX + 100,capeY + 100,1, isCurrentCape ? RenderUtils.getRGB(0,255,0) : RenderUtils.getRGB(100,100,100),RenderUtils.getRGB(0,0,0,0));

                FontManager.default16.drawStringWithShadow(cape.getName(),mainX + 150,capeY + 45,isCurrentCape ? RenderUtils.getRGB(0,255,0) : -1);

                if (Wrapper.isHovered(mainX,mainY + 22,mainX + 100,mainY + 180,mouseX,mouseY)) {
                    if (Wrapper.isHovered(mainX + 2, capeY, mainX + 100, capeY + 100, mouseX, mouseY) && canClickLeft()) {
                        CapeManager.Instance.setCurrentCape(cape);
                    }
                }

                capeY += 104;
            }

            RenderUtils.stopGlScissor();

            if (capeCurrentY > maxY) {
                capeCurrentY = Math.max(0,maxY);
            }

            if (capeCurrentY < 0) {
                capeCurrentY = 0;
            }

            if (Mouse.hasWheel()) {
                final int wheel = Mouse.getDWheel();

                if (wheel != 0) {
                    if (wheel < 0) {//Down
                        capeCurrentY += 50;
                    } else {//Up
                        capeCurrentY -= 50;
                    }
                }
            }
        }

        if (mouseLeftDown && !Mouse.isButtonDown(0)) {
            mouseLeftDown = false;
        }

        if (mouseRightDown && !Mouse.isButtonDown(1)) {
            mouseRightDown = false;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (selectType == Type.HOME) {
            for (GuiTextField modTextField : modTextFields) {
                modTextField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (Wrapper.isHovered(mainX, mainY, mainX + 300, mainY + 20, mouseX, mouseY)) {
            if (mouseButton == 0) {
                dragging = true;
                lastMainX = mainX - mouseX;
                lastMainY = mainY - mouseY;
            }
        }

        if (selectType == Type.HOME) {
            for (GuiTextField modTextField : modTextFields) {
                modTextField.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        dragging = false;
    }

    private boolean canClickLeft() {
        if (!mouseLeftDown && Mouse.isButtonDown(0)) {
            mouseLeftDown = true;
            return true;
        }

        return false;
    }

    private boolean canClickRight() {
        if (!mouseRightDown && Mouse.isButtonDown(1)) {
            mouseRightDown = true;
            return true;
        }

        return false;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private enum Type {
        HOME("Home"),
        CONFIG("Config"),
        DESIGNER("Designer"),
        SETTING("Setting"),
        CAPE("Cape");

        private final String renderName;

        Type(String renderName) {
            this.renderName = renderName;
        }

        public String getRenderName() {
            return renderName;
        }
    }

    private static class ModObject {
        private final Mod mod;
        private double animationX;
        private boolean showValue;

        public ModObject(Mod mod) {
            this.mod = mod;
        }
    }

    public static class GuiAddConfig extends GuiScreen {
        private GuiTextField textField;
        private GuiScreen preScr;

        public GuiAddConfig(GuiScreen pre){
            preScr = pre;
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);

            switch (button.id) {
                case 0: {
                    if (!textField.getText().isEmpty()) {
                        if (ConfigManager.Instance.containsCustomConfig(textField.getText())) {
                            NotificationManager.Instance.addNotification("Config","Config已存在!", Notification.NotificationType.ERROR,5000);
                            mc.displayGuiScreen(preScr);
                            return;
                        }

                        ConfigManager.Instance.saveConfig(new CustomConfig(textField.getText()));
                        NotificationManager.Instance.addNotification("Config","成功保存Config " + textField.getText(), Notification.NotificationType.SUCCESS,5000);
                        mc.displayGuiScreen(preScr);
                    }
                    break;
                }
                case 1: {
                    mc.displayGuiScreen(preScr);
                    break;
                }
            }
        }

        @Override
        public void initGui() {
            super.initGui();
            textField = new GuiTextField(114514, this.mc.fontRenderer, width / 2 - 100, height / 2 - 20, 200, 20);
            buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 + 20, 200, 20, "Add"));
            buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 + 60, 200, 20, "Cancel"));
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            RenderUtils.drawRect(0.0, 0.0, width, height, new Color(0, 0, 0, 130).getRGB());
            super.drawScreen(mouseX, mouseY, partialTicks);
            mc.fontRenderer.drawCenteredStringWithShadow("Add Config", (float)width / 2.0f, (float)height / 2.0f - 60.0f, -1);
            textField.drawTextBox();
            textField.setMaxStringLength(20);
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);
            textField.textboxKeyTyped(typedChar, keyCode);
        }

        @Override
        protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            textField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
}
