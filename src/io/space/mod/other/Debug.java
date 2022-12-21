package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.Event2D;
import io.space.mod.Mod;
import io.space.utils.RenderUtils;
import org.lwjgl.input.Keyboard;
import utils.hodgepodge.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Debug extends Mod {
    private final File catchInfosDirectory = new File(Wrapper.Instance.getClientDirectory(),"catchinfo/");

    private final List<DebugInfo> debugInfos = Arrays.asList(
            new DebugInfo("Version") {
                @Override
                public void onUpdateValue() {
                    value = Wrapper.Instance.getClientVersion();
                }
            },
            new DebugInfo("PositionX") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.posX;
                }
            },
            new DebugInfo("PositionY") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.posY;
                }
            },
            new DebugInfo("PositionZ") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.posZ;
                }
            },
            new DebugInfo("MotionX") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.motionX;
                }
            },
            new DebugInfo("MotionY") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.motionY;
                }
            },
            new DebugInfo("MotionZ") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.motionZ;
                }
            },
            new DebugInfo("JumpMovementFactor") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.jumpMovementFactor;
                }
            },
            new DebugInfo("StepHeight") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.stepHeight;
                }
            },
            new DebugInfo("EntityID") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getEntityId();
                }
            },
            new DebugInfo("MoveForward") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.movementInput.moveForward;
                }
            },
            new DebugInfo("MoveStrafe") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.movementInput.moveStrafe;
                }
            },
            new DebugInfo("EyeHeight") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getEyeHeight();
                }
            },
            new DebugInfo("FallDistance") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.fallDistance;
                }
            },
            new DebugInfo("Health") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getHealth();
                }
            },
            new DebugInfo("MaxHealth") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getMaxHealth();
                }
            },
            new DebugInfo("AbsorptionAmount") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getAbsorptionAmount();
                }
            },
            new DebugInfo("TotalArmorValue") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.getTotalArmorValue();
                }
            },
            new DebugInfo("HurtTime") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.hurtTime;
                }
            },
            new DebugInfo("HurtResistantTime") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.hurtResistantTime;
                }
            },
            new DebugInfo("RotationYaw") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.rotationYaw;
                }
            },
            new DebugInfo("RotationPitch") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.rotationPitch;
                }
            },
            new DebugInfo("PacketRotationYaw") {
                @Override
                public void onUpdateValue() {
                    value = Wrapper.Instance.getUpdateYaw();
                }
            },
            new DebugInfo("PacketRotationPitch") {
                @Override
                public void onUpdateValue() {
                    value = Wrapper.Instance.getUpdatePitch();
                }
            },
            SKIP,
            new DebugInfo("OnGround") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.onGround;
                }
            },
            new DebugInfo("IsCollidedHorizontally") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.isCollidedHorizontally;
                }
            },
            new DebugInfo("IsSprinting") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.isSprinting();
                }
            },
            new DebugInfo("IsSneaking") {
                @Override
                public void onUpdateValue() {
                    value = mc.player.isSneaking();
                }
            }
    );

    private static final DebugInfo SKIP = new DebugInfo(null) {
        @Override
        public void onUpdateValue() {

        }
    };

    private boolean keyDown = false;

    public Debug() {
        super("Debug",Category.OTHER);

        if (!catchInfosDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            catchInfosDirectory.mkdirs();
        }
    }

    @EventTarget
    public void on2D(Event2D e) {


        float debugX = 2,debugY = 120;

        for (DebugInfo debugInfo : debugInfos) {
            if (debugInfo != SKIP) {
                debugInfo.onUpdateValue();

                mc.unicodeFontRenderer.drawStringWithShadow(debugInfo.name + " " + debugInfo.value, debugX, debugY, RenderUtils.getRGB(255, 0, 0));
            }

            if (debugY > 500) {
                debugX += 50;
                debugY = 0;
            } else {
                debugY += 10;
            }
        }

        final boolean keyPressing = Keyboard.isKeyDown(Keyboard.KEY_7);

        if (keyPressing && !keyDown) {
            final StringBuilder builder = new StringBuilder();

            for (DebugInfo debugInfo : debugInfos) {
                if (debugInfo == SKIP) {
                    builder.append(System.lineSeparator());
                    continue;
                }
                builder.append(debugInfo.name).append(" ").append(debugInfo.value).append(System.lineSeparator());
            }

            int index = 0;
            File file;

            while ((file = new File(catchInfosDirectory,"CatchInfos" + index + ".txt")).exists()) {
                index++;
            }

            try {
                FileUtils.writeStringToFile(file,builder.toString(),StandardCharsets.UTF_8);

                for (DebugInfo debugInfo : debugInfos) {
                    Wrapper.sendMessage(debugInfo.name + " " + debugInfo.value);
                }

                Wrapper.sendMessage("Catch debug infos save to " + file.getAbsolutePath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Wrapper.sendMessage("Catch debug infos error " + ioException.getMessage());
            }

            keyDown = true;
        }

        if (!keyPressing) {
            keyDown = false;
        }
    }

    @Override
    protected void onEnable() {
        Wrapper.sendMessage("Press \"7\" to catch infos");
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        keyDown = false;
        super.onDisable();
    }

    private static abstract class DebugInfo {
        public final String name;
        public Object value;

        public DebugInfo(String name) {
            this.name = name;
        }

        public abstract void onUpdateValue();
    }
}
