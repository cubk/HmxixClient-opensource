package net.minecraft.client.gui;

import io.space.alt.GuiAltManager;
import io.space.global.GlobalSetting;
import io.space.renderer.font.FontManager;
import io.space.renderer.gui.button.GuiClientButton;
import io.space.utils.RenderUtils;
import net.minecraft.client.renderer.texture.DynamicTexture;

import java.util.concurrent.CopyOnWriteArrayList;

public class GuiMainMenu extends GuiScreen {
    private final CopyOnWriteArrayList<GuiClientButton> clientButtons = new CopyOnWriteArrayList<>();

    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        clientButtons.clear();
        clientButtons.add(new GuiClientButton("单人游戏",width / 2.0 - 50,height / 2.0 - 42,100,20, RenderUtils.getRGB(0,0,0,80), FontManager.default16) {
            @Override
            public void onClicked() {
                mc.displayGuiScreen(new GuiSelectWorld(GuiMainMenu.this));
            }
        });
        clientButtons.add(new GuiClientButton("多人游戏",width / 2.0 - 50,height / 2.0 - 21,100,20, RenderUtils.getRGB(0,0,0,80), FontManager.default16) {
            @Override
            public void onClicked() {
                mc.displayGuiScreen(new GuiMultiplayer(GuiMainMenu.this));
            }
        });
        clientButtons.add(new GuiClientButton("设置",width / 2.0 - 50,height / 2.0,100,20, RenderUtils.getRGB(0,0,0,80), FontManager.default16) {
            @Override
            public void onClicked() {
                mc.displayGuiScreen(new GuiOptions(GuiMainMenu.this,mc.gameSettings));
            }
        });
        clientButtons.add(new GuiClientButton("账户",width / 2.0 - 50,height / 2.0 + 21,100,20, RenderUtils.getRGB(0,0,0,80), FontManager.default16) {
            @Override
            public void onClicked() {
                mc.displayGuiScreen(new GuiAltManager(GuiMainMenu.this));
            }
        });
        clientButtons.add(new GuiClientButton("退出",width / 2.0 - 50,height / 2.0 + 42,100,20, RenderUtils.getRGB(0,0,0,80), FontManager.default16) {
            @Override
            public void onClicked() {
                mc.shutdown();
            }
        });
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        final DynamicTexture backGround = GlobalSetting.Instance.getBackground();

        if (backGround != null) {
            RenderUtils.drawImage(0,0,width,height,-1, backGround);
        } else {
            drawDefaultBackground();
        }

        for (GuiClientButton clientButton : clientButtons) {
            clientButton.setFontRenderer(FontManager.default16);
            clientButton.drawButton(mouseX,mouseY);
        }

        FontManager.default16.drawStringWithShadow("Copyright Mojang AB. Do not distribute!",2,height - 10,-1);
    }
}
