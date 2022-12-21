package io.space.renderer.gui.clickgui;

import io.space.Wrapper;
import io.space.mod.Mod;
import io.space.mod.ModManager;
import io.space.utils.RenderUtils;
import io.space.value.Value;
import io.space.value.values.BooleanValue;
import io.space.value.values.ModeValue;
import io.space.value.values.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public final class ActivatedClickGui extends GuiScreen {
    private static final ModCategoryPanel[] modCategoryPanels;

    static {
        modCategoryPanels = new ModCategoryPanel[Mod.Category.values().length];

        int x = 10;

        for (int i = 0; i < Mod.Category.values().length; i++) {
            final Mod.Category value = Mod.Category.values()[i];
            modCategoryPanels[i] = new ModCategoryPanel(value.name().toLowerCase(),x,10,value);
            x += 55;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRect(0,0, Wrapper.Instance.getScaledResolution().getScaledWidth(),Wrapper.Instance.getScaledResolution().getScaledHeight(),RenderUtils.getRGB(0,0,0,50));
        for (ModCategoryPanel modCategoryPanel : modCategoryPanels) {
            modCategoryPanel.draw(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static class Panel {
        protected final String panelName;
        protected int x,y;
        private int dragX,dragY;
        private boolean leftMouseKeyDown,rightMouseKeyDown;

        public Panel(String panelName) {
            this.panelName = panelName;
        }

        public Panel(String panelName, int x, int y) {
            this.panelName = panelName;
            this.x = x;
            this.y = y;
        }

        public void draw(int mouseX, int mouseY, float partialTicks) {
            RenderUtils.drawRect(x,y,x + 50,y + 12,RenderUtils.getRGB(0,1,19,255));
            RenderUtils.drawRect(x + 1,y + 1,x + 49,y + 11,RenderUtils.getRGB(0,48,84,255));
            Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(panelName,x + 3,y + 2,-1);

            if (Wrapper.isHovered(x,y,x + 50,y + 12,mouseX,mouseY)) {
                RenderUtils.drawRect(x,y,x + 50,y + 12,RenderUtils.getRGB(50,50,50,100));

                if (Mouse.isButtonDown(0)) {
                    if (dragX == 0 && dragY == 0) {
                        dragX = mouseX - x;
                        dragY = mouseY - y;
                    } else {
                        x = mouseX - dragX;
                        y = mouseY - dragY;
                    }
                } else if (this.dragX != 0 || this.dragY != 0) {
                    dragX = 0;
                    dragY = 0;
                }
            } else if (this.dragX != 0 || this.dragY != 0) {
                dragX = 0;
                dragY = 0;
            }

            if (Mouse.isButtonDown(1)) {
                if (!rightMouseKeyDown) {
                    onMouseRightClick(mouseX, mouseY, partialTicks);
                    rightMouseKeyDown = true;
                }
            } else rightMouseKeyDown = false;

            if (Mouse.isButtonDown(0)) {
                if (!leftMouseKeyDown) {
                    onMouseLeftClick(mouseX, mouseY, partialTicks);
                    leftMouseKeyDown = true;
                }
            } else leftMouseKeyDown = false;
        }

        protected void onMouseRightClick(int mouseX, int mouseY, float partialTicks) {}
        protected void onMouseLeftClick(int mouseX, int mouseY, float partialTicks) {}
    }

    private static class ModCategoryPanel extends Panel {
        private static final ModInfo[] modInfos = new ModInfo[ModManager.Instance.getModMap().size()];

        private final Mod.Category category;
        private boolean leftMouseKeyDown,rightMouseKeyDown;
        private boolean shouldShow = true;

        static {
            final Mod[] mods = ModManager.Instance.getModMap().values().toArray(new Mod[0]);
            for (int i = 0; i < modInfos.length; i++) {
                modInfos[i] = new ModInfo(mods[i]);
            }
        }

        public ModCategoryPanel(String panelName,Mod.Category category) {
            super(panelName);
            this.category = category;
        }

        public ModCategoryPanel(String panelName, int x, int y, Mod.Category category) {
            super(panelName, x, y);
            this.category = category;
        }

        @Override
        public void draw(int mouseX, int mouseY, float partialTicks) {
            super.draw(mouseX, mouseY, partialTicks);

            if (shouldShow) {
                final List<ModInfo> categoryMods = Arrays.stream(modInfos).filter(modInfo -> modInfo.mod.getCategory() == category).collect(Collectors.toList());;

                if (!categoryMods.isEmpty()) {
                    int y = this.y + 12;
                    //RenderUtils.drawRect(x, this.y + 12, x + 50, this.y + 12 + (categoryMods.size() * 10), RenderUtils.getRGB(110, 110, 110, 255));
                    for (ModInfo modInfo : categoryMods) {
                        RenderUtils.drawRect(x + 1, y, x + 49, y + 10, RenderUtils.getRGB(0,0,0, 100));
                        if (modInfo.mod.isEnable()) {
                            RenderUtils.drawRect(x + 1, y, x + 49, y + 10, RenderUtils.getRGB(25,125,209,255));
                        }
                        Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(modInfo.mod.getModName(), x + 2, y + 1, modInfo.mod.isEnable() ? -1 : RenderUtils.getRGB(128, 128, 128, 255));

                        if (!modInfo.mod.getValues().isEmpty()) {
                            Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(modInfo.shouldShowValuePanel ? "<" : ">", x + 45, y + 1, -1);
                        }

                        if (Wrapper.isHovered(x + 1, y + 1, x + 49, y + 9, mouseX, mouseY)) {
                            RenderUtils.drawRect(x + 1, y, x + 49, y + 10, RenderUtils.getRGB(20, 20, 20, 100));

                            if (Mouse.isButtonDown(0) && !leftMouseKeyDown) {
                                modInfo.mod.toggle();
                                leftMouseKeyDown = true;
                            }

                            if (Mouse.isButtonDown(1)) {
                                if (!rightMouseKeyDown) {
                                    modInfo.shouldShowValuePanel = !modInfo.shouldShowValuePanel;
                                    rightMouseKeyDown = true;
                                }
                            }
                        }

                        if (modInfo.shouldShowValuePanel) {
                            float valueY = y;
                            for (Value<?> value : modInfo.mod.getValues()) {
                                RenderUtils.drawRect(x + 50, valueY, x + 120, valueY + 10, RenderUtils.getRGB(10,13,5,220));
                                if (value instanceof BooleanValue) {
                                    BooleanValue booleanValue = ((BooleanValue) value);
                                    Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(booleanValue.getValueName(),x + 52,valueY,-1);
                                    RenderUtils.drawRect(x + 110,valueY + 1,x + 119,valueY + 9,RenderUtils.getRGB(0,0,0,255));
                                    RenderUtils.drawRect(x + 111,valueY + 2,x + 118,valueY + 8,booleanValue.getValue() ? RenderUtils.getRGB(0,139,253,255) : RenderUtils.getRGB(0,0,0,255));

                                    if (Wrapper.isHovered(x + 110,valueY + 1,x + 119,valueY + 9,mouseX,mouseY)) {
                                        if (Mouse.isButtonDown(0) && !leftMouseKeyDown) {
                                            booleanValue.setValue(!booleanValue.getValue());
                                            leftMouseKeyDown = true;
                                        }
                                    }
                                } else if (value instanceof ModeValue) {
                                    ModeValue modeValue = ((ModeValue) value);
                                    Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(modeValue.getValueName(),x + 52,valueY,-1);
                                    Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(modeValue.getValue(),x + 120 - Minecraft.getMinecraft().unicodeFontRenderer.getStringWidth(modeValue.getValue()) - 2,valueY,-1);

                                    if (Wrapper.isHovered(x + 50, valueY, x + 120, valueY + 10,mouseX,mouseY)) {
                                        if (Mouse.isButtonDown(1)) {
                                            if (!rightMouseKeyDown) {
                                                modeValue.shouldShow = !modeValue.shouldShow;
                                                rightMouseKeyDown = true;
                                            }
                                        }
                                    }

                                    if (modeValue.shouldShow) {
                                        valueY += 10;
                                        for (int i = 0; i < modeValue.getModes().length; i++) {
                                            String mode = modeValue.getModes()[i];
                                            RenderUtils.drawRect(x + 50, valueY, x + 120, valueY + 10, RenderUtils.getRGB(20,20,20, 220));
                                            Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(mode,x + 52,valueY,modeValue.isCurrentMode(mode) ? RenderUtils.getRGB(0,255,0,255) : -1);

                                            if (!modeValue.isCurrentMode(mode)) {
                                                if (Wrapper.isHovered(x + 50, valueY, x + 120, valueY + 10, mouseX, mouseY)) {
                                                    if (Mouse.isButtonDown(0) && !leftMouseKeyDown) {
                                                        modeValue.setValue(mode);
                                                        leftMouseKeyDown = true;
                                                    }
                                                }
                                            }

                                            if (i != modeValue.getModes().length - 1) {
                                                valueY += 10;
                                            }
                                        }
                                    }
                                } else if (value instanceof NumberValue) {
                                    NumberValue numberValue = (NumberValue) value;
                                    Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(numberValue.getValueName(),x + 52,valueY,-1);
                                    Minecraft.getMinecraft().unicodeFontRenderer.drawStringWithShadow(numberValue.getValue().toString(),x + 119 - Minecraft.getMinecraft().unicodeFontRenderer.getStringWidth(value.getValue().toString()),valueY,-1);
                                    RenderUtils.drawRect(x + 50, valueY + 10, x + 120, valueY + 20, RenderUtils.getRGB(0,0,0, 255));
                                    double render = 70.0 * ((numberValue.getValue() - numberValue.getMin()) / (numberValue.getMax() - numberValue.getMin()));
                                    if (Wrapper.isHovered(x + 50, valueY + 10, x + 120, valueY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                                        double min = numberValue.getMin();
                                        double max = numberValue.getMax();
                                        double inc = numberValue.getIncrease();
                                        double valAbs = (double) mouseX - (x + 50);
                                        double perc = valAbs / 68.0;
                                        perc = Math.min(Math.max(0.0, perc), 1.0);
                                        double valRel = (max - min) * perc;
                                        double val = min + valRel;
                                        val = (double) Math.round(val * (1.0 / inc)) / (1.0 / inc);
                                        numberValue.setValue(val);
                                    }
                                    RenderUtils.drawRect(x + 50, valueY + 10, x + 50 + render, valueY + 20, RenderUtils.getRGB(0,200,0, 255));
                                    valueY += 10;
                                }

                                valueY += 10;
                            }
                        }

                        y += 10;
                    }
                }
            }
            if (!Mouse.isButtonDown(1)) {
                rightMouseKeyDown = false;
            }

            if (leftMouseKeyDown && !Mouse.isButtonDown(0)) {
                leftMouseKeyDown = false;
            }
        }

        @Override
        protected void onMouseRightClick(int mouseX, int mouseY, float partialTicks) {
            if (Wrapper.isHovered(x,y,x + 50,y + 12,mouseX,mouseY)) {
                shouldShow = !shouldShow;
            }
        }
    }

    private static class ModInfo {
        public final Mod mod;
        public boolean shouldShowValuePanel;

        public ModInfo(Mod mod) {
            this.mod = mod;
        }
    }
}