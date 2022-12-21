package io.space.renderer.gui.clientsetting;

import io.space.Wrapper;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.translate.TranslateManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public final class GuiClientSetting extends GuiScreen {
    private final GuiScreen parentScreen;

    public GuiClientSetting(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 1) {
            TranslateManager.Instance.writeModTranslateFile();
            NotificationManager.Instance.addNotification("TranslateManager","写出翻译文件成功!", Notification.NotificationType.SUCCESS,5000);
        } else if (button.id == 2) {
            Desktop.getDesktop().open(Wrapper.Instance.getClientDirectory());
        }

        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0,2,height - 22,68,20,"返回"));
        buttonList.add(new GuiButton(1,2,2,68,20,"写出翻译文件"));
        buttonList.add(new GuiButton(2,2,22,68,20,"打开Client目录"));

        super.initGui();
    }
}
