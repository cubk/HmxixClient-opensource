package io.space.renderer.gui;

import io.space.network.NetworkClient;
import io.space.network.common.client.CPacketRegister;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import utils.hodgepodge.object.StringUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public final class GuiRegister extends GuiLogin {
    private GuiTextField activationCodeTextField;

    public GuiRegister() {
        RInstance = this;

        title = "注册";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final double wMid = width / 2.0;
        final double hMid = height / 2.0;

        activationCodeTextField.drawTextBox();

        if (!activationCodeTextField.isFocused() && StringUtils.isNullOrEmpty(activationCodeTextField.getText())) {
            mc.fontRenderer.drawStringWithShadow("ActivationCode",(float) wMid - 114,(float) hMid + 36, RenderUtils.getRGB(100,100,100));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            if (userNameTextField.getText() == null || userNameTextField.getText().isEmpty()) {
                NotificationManager.Instance.addNotification("Register","User name cannot be empty", Notification.NotificationType.WARNING,5000);
                return;
            }

            if (passwordTextField.getText() == null || passwordTextField.getText().isEmpty()) {
                NotificationManager.Instance.addNotification("Register","Password cannot be empty", Notification.NotificationType.WARNING,5000);
                return;
            }

            if (activationCodeTextField.getText() == null || activationCodeTextField.getText().isEmpty()) {
                NotificationManager.Instance.addNotification("Register","ActivationCode cannot be empty", Notification.NotificationType.WARNING,5000);
                return;
            }

            statusText = "注册中...";
            loginButton.enabled = false;

            try {
                if (NetworkClient.Instance.isOpen())
                    NetworkClient.Instance.sendPacket(new CPacketRegister(userNameTextField.getText(), passwordTextField.getText(), getHardwareID(), activationCodeTextField.getText()));
                else
                    statusText = "Can't send packet: Channel closed";
            } catch (NoSuchAlgorithmException e) {
                statusText = e.getClass().getName() + ':' + e.getMessage();
            }
        } else if (button.id == 1) {
            mc.displayGuiScreen(new GuiLogin());
        } else if (button.id == 2) {
            try {
                setClipboardString(getHardwareID());

                statusText = "复制成功";
            } catch (NoSuchAlgorithmException e) {
                statusText = e.getClass().getName() + ':' + e.getMessage();
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        activationCodeTextField.mouseClicked(mouseX,mouseY,mouseButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        activationCodeTextField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        activationCodeTextField.textboxKeyTyped(typedChar,keyCode);
    }

    @Override
    public void initGui() {
        final int wMid = width / 2;
        final int hMid = height / 2;

        buttonList.add(loginButton = new GuiButton(0,wMid - 118,hMid + 48,60,20,"注册"));
        buttonList.add(new GuiButton(1,wMid + 58,hMid + 48,60,20,"返回"));
        buttonList.add(new GuiButton(2,wMid - 29,hMid + 48,60,20,"复制机器码"));

        userNameTextField = new GuiTextField(0,mc.fontRenderer,wMid - 116,hMid - 30,233,20);
        passwordTextField = new GuiTextField(1,mc.fontRenderer,wMid - 116,hMid,233,20);
        activationCodeTextField = new GuiTextField(2,mc.fontRenderer,wMid - 116,hMid + 30,233,20);

        activationCodeTextField.setMaxStringLength(50);
    }
}
