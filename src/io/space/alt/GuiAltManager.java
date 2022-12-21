package io.space.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import io.space.Wrapper;
import io.space.alt.altimpl.MicrosoftAlt;
import io.space.alt.altimpl.MojangAlt;
import io.space.alt.altimpl.OfflineAlt;
import io.space.alt.altimpl.OriginalAlt;
import io.space.alt.microsoft.GuiLoginMicrosoftAccount;
import io.space.alt.microsoft.MicrosoftAuth;
import io.space.object.SlidingCalculation;
import io.space.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;
import utils.hodgepodge.object.StringUtils;

import java.io.IOException;
import java.net.Proxy;

public final class GuiAltManager extends GuiScreen {
    private final GuiScreen parentScreen;
    private static final SlidingCalculation slidingCalculation = new SlidingCalculation(30,30);

    private GuiButton buttonLogin;
    private GuiButton buttonRemove;
    private volatile String status = EnumChatFormatting.YELLOW + "等待中...";

    private static Alt selectAlt;

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        buttonLogin.enabled = buttonRemove.enabled = selectAlt != null;

        RenderUtils.drawRect(0,0,200,height,RenderUtils.getRGB(0,0,0,80));

        mc.customFontRenderer.drawCenteredString(EnumChatFormatting.YELLOW + "当前用户名:" + mc.session.getUsername(),width / 2.0f,height / 2.0f - 10,-1);
        mc.customFontRenderer.drawCenteredString(status,width / 2.0f,height / 2.0f,-1);

        double altY = 2 - slidingCalculation.getCurrent();
        for (Alt alt : AltManager.Instance.getAltList()) {
            RenderUtils.drawRect(2,altY,198,altY + 50,RenderUtils.getRGB(50,50,50,150));

            if (alt == selectAlt) {
                RenderUtils.drawBorderedRect(2,altY,198,altY + 50,1,-1,0);
            } else if (Wrapper.isHovered(2,altY,198,altY + 50,mouseX,mouseY) && Mouse.isButtonDown(0)) {
                selectAlt = alt;
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5,1.5,1.5);
            mc.customFontRenderer.drawCenteredString(alt.getUserName(),98 / 1.5f,((float) altY + 4) / 1.5f,-1);
            GlStateManager.popMatrix();

            switch (alt.getAccountType()) {
                case OFFLINE:
                    mc.customFontRenderer.drawCenteredString("离线账户",98,((float) altY + 22),RenderUtils.getRGB(255,100,100));
                    break;
                case MOJANG:
                    mc.customFontRenderer.drawCenteredString("账号:" + ((MojangAlt) alt).getAccount(),98,((float) altY + 22),RenderUtils.getRGB(150,150,150));
                    break;
                case MICROSOFT:
                    mc.customFontRenderer.drawCenteredString("微软账户",98,((float) altY + 22),RenderUtils.getRGB(0,255,0));
                    break;
                case ORIGINAL: {
                    final OriginalAlt originalAlt = (OriginalAlt) alt;
                    mc.customFontRenderer.drawCenteredString("原始账户", 98, ((float) altY + 22), RenderUtils.getRGB(255, 255, 0));
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.5, 0.5, 0.5);
                    mc.customFontRenderer.drawCenteredString("Type:" + originalAlt.getType(), 98 * 2f, ((float) altY + 36) * 2f, RenderUtils.getRGB(150,150,150));
                    mc.customFontRenderer.drawCenteredString("UUID:" + originalAlt.getUUID(), 98 * 2f, ((float) altY + 41) * 2f, RenderUtils.getRGB(150,150,150));
                    GlStateManager.popMatrix();
                    break;
                }
            }

            slidingCalculation.calculation();

            if (slidingCalculation.getCurrent() < 0) {
                slidingCalculation.setCurrent(0);
            }

            altY += 55;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 1) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account, String password) {
                    if (StringUtils.isNullOrEmpty(password)) {
                        status = EnumChatFormatting.GREEN + "增添成功! " + account;
                        AltManager.Instance.getAltList().add(new OfflineAlt(account));
                    } else {
                        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
                        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
                        auth.setUsername(account);
                        auth.setPassword(password);

                        status = EnumChatFormatting.YELLOW + "增添中...";

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    auth.logIn();
                                    status = EnumChatFormatting.GREEN + "增添成功! " + account;

                                    AltManager.Instance.getAltList().add(new MojangAlt(account,password,auth.getSelectedProfile().getName()));
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    status = EnumChatFormatting.RED + "增加失败! " + e.getClass().getName() + ": " + e.getMessage();
                                }

                                interrupt();
                            }
                        }.start();
                    }
                }
            });
        } else if (button.id == 2) {
            if (selectAlt != null) {
                new Thread() {
                    @Override
                    public void run() {
                        status = EnumChatFormatting.YELLOW + "登录中...";

                        switch (selectAlt.getAccountType()) {
                            case OFFLINE:
                                Minecraft.getMinecraft().session = new Session(selectAlt.getUserName(), "", "", "mojang");
                                status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                break;
                            case MOJANG: {
                                try {
                                    final MojangAlt mojangAlt = (MojangAlt) selectAlt;
                                    final AltManager.LoginStatus loginStatus = AltManager.loginAlt(mojangAlt.getAccount(), mojangAlt.getPassword());

                                    switch (loginStatus) {
                                        case FAILED:
                                            status = EnumChatFormatting.RED + "登录失败!";
                                            break;
                                        case SUCCESS:
                                            status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                            break;
                                    }
                                } catch (AuthenticationException e) {
                                    if (e.getMessage().equals("Migrated")) {
                                        status = EnumChatFormatting.RED + "此用户迁移至微软了!";
                                    } else {
                                        e.printStackTrace();
                                        status = EnumChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                                    }
                                }
                                break;
                            }
                            case MICROSOFT: {
                                new Thread("AltManager login microsoft thread") {
                                    @Override
                                    public void run() {
                                        new MicrosoftAuth(
                                                data -> mc.session = new Session(data.getUserName(),data.getUuid(),data.getAccessToken(),"mojang"),
                                                status -> GuiAltManager.this.status = status,
                                                ((MicrosoftAlt) selectAlt).getRefreshToken()
                                        );
                                    }
                                }.start();

                                break;
                            }
                            case ORIGINAL: {
                                final OriginalAlt originalAlt = (OriginalAlt) selectAlt;
                                mc.session = new Session(originalAlt.getUserName(),originalAlt.getUUID(),originalAlt.getAccessToken(),originalAlt.getType());
                                status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                break;
                            }
                        }

                        interrupt();
                    }
                }.start();
            }
        } else if (button.id == 3) {
            if (selectAlt != null) {
                AltManager.Instance.getAltList().remove(selectAlt);
                selectAlt = null;
            }
        } else if (button.id == 4) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account,String password) {
                    new Thread() {
                        @Override
                        public void run() {
                            final AltManager.LoginStatus loginStatus;
                            try {
                                status = EnumChatFormatting.YELLOW + "登录中...";
                                loginStatus = AltManager.loginAlt(account, password);

                                switch (loginStatus) {
                                    case FAILED:
                                        status = EnumChatFormatting.RED + "登录失败!";
                                        break;
                                    case SUCCESS:
                                        status = EnumChatFormatting.GREEN + "登录成功! " + mc.session.getUsername();
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                e.printStackTrace();
                                status = EnumChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                            }

                            interrupt();
                        }
                    }.start();
                }
            });
        } else if (button.id == 5) {
            mc.displayGuiScreen(new GuiLoginMicrosoftAccount(this) {
                @Override
                protected void onLogin(@NotNull MicrosoftAuth.Data data) {
                    mc.session = new Session(data.getUserName(), data.getUuid(), data.getAccessToken(), "mojang");
                }
            });
        } else if (button.id == 6) {
            mc.displayGuiScreen(new GuiLoginMicrosoftAccount(this) {
                @Override
                protected void onLogin(@NotNull MicrosoftAuth.Data data) {
                    AltManager.Instance.getAltList().add(new MicrosoftAlt(data.getUserName(), data.getRefreshToken()));
                }
            });
        } else if (button.id == 7) {
            mc.displayGuiScreen(new GuiOriginalLogin(this,true));
        } else if (button.id == 8) {
            mc.displayGuiScreen(new GuiOriginalLogin(this,false));
        }

        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0,205,height - 22,60,20,"返回"));
        buttonList.add(new GuiButton(1,270,height - 22,60,20,"增添"));
        buttonList.add(buttonLogin = new GuiButton(2,205,height - 44,60,20,"登录"));
        buttonList.add(buttonRemove = new GuiButton(3,205,height - 66,60,20,"删除"));
        buttonList.add(new GuiButton(4,270,height - 44,60,20,"直接登录"));
        buttonList.add(new GuiButton(5,270,height - 66,60,20,"微软登录"));
        buttonList.add(new GuiButton(6,335,height - 22,60,20,"添加微软账户"));
        buttonList.add(new GuiButton(7,335,height - 44,60,20,"原始登录"));
        buttonList.add(new GuiButton(8,335,height - 66,60,20,"添加原始账号"));
        super.initGui();
    }
}
