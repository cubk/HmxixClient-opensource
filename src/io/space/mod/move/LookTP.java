package io.space.mod.move;

import com.darkmagician6.eventapi.EventTarget;
import io.space.events.EventTick;
import io.space.mod.Mod;
import io.space.utils.WorldUtils;
import io.space.value.values.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import utils.hodgepodge.object.StringUtils;

public final class LookTP extends Mod {
    private final NumberValue range = new NumberValue("Range",500.0,0.0,500.0,1.0);

    private boolean keyDown = false;
    private EntityPlayer lastEntity;

    public LookTP() {
        super("LookTP",Category.MOVE);
        registerValues(range);
    }

    @EventTarget
    public void onTick(EventTick e) {
        final boolean buttonDown = Mouse.isButtonDown(2);
        if (!keyDown && buttonDown) {
            final Entity entityCache = WorldUtils.findEntityFromLook(range.getValue());

            if (entityCache instanceof EntityPlayer) {
                lastEntity = (EntityPlayer) entityCache;
            }

            if (lastEntity != null) {
                final ChatComponentText chatComponentText = new ChatComponentText(StringUtils.buildString(
                        EnumChatFormatting.AQUA,
                        "[LookTP] ",
                        EnumChatFormatting.WHITE,
                        "名字:",
                        lastEntity.getDisplayName().getFormattedText(),
                        " X:",
                        (int) lastEntity.posX,
                        " Y:",
                        (int) lastEntity.posY,
                        " Z:",
                        (int) lastEntity.posZ,
                        " 距离咱:",
                        (int) mc.player.getDistanceToEntity(lastEntity),
                        "米"
                ));

                final ChatComponentText clickEventChatComponent = new ChatComponentText(StringUtils.buildString(
                        EnumChatFormatting.WHITE,
                        " [",
                        EnumChatFormatting.YELLOW,
                        "点我TP",
                        EnumChatFormatting.WHITE,
                        "]"
                ));

                final ChatStyle chatStyle = clickEventChatComponent.getChatStyle();
                chatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "-tp " + lastEntity.getName()));
                chatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("点我TP啦!~")));

                chatComponentText.appendSibling(clickEventChatComponent);

                mc.player.addChatMessage(chatComponentText);

                lastEntity = null;
            }

            keyDown = true;
        }

        if (!buttonDown) {
            keyDown = false;
        }
    }
}
