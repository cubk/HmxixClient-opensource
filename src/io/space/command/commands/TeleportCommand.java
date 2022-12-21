package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.mod.move.Teleport;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import utils.hodgepodge.object.StringUtils;

public final class TeleportCommand extends Command {
    private final String[] errorMessages = new String[2];

    public TeleportCommand() {
        super("tp");
        setHelp("Teleport");
        errorMessages[0] = "-tp <x> <y> <z>";
        errorMessages[1] = "-tp <player name>";
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 3) {
            final String xS = args[0];
            final String yS = args[1];
            final String zS = args[2];
            if (StringUtils.isNumber(xS)) {
                if (StringUtils.isNumber(yS)) {
                    if (StringUtils.isNumber(zS)) {
                        final double x = Double.parseDouble(xS);
                        final double y = Double.parseDouble(yS);
                        final double z = Double.parseDouble(zS);
                        Teleport.doTeleport(x,y,z);
                        NotificationManager.Instance.addNotification("Teleport","马上将你传送到: " + xS + "," + yS + "," + zS, Notification.NotificationType.INFO,2000);
                    } else {
                        Wrapper.sendMessage(zS + " 不是一个数字!");
                    }
                } else {
                    Wrapper.sendMessage(yS + " 不是一个数字!");
                }
            } else {
                Wrapper.sendMessage(xS + " 不是一个数字!");
            }
        } else if (args.length == 1) {
            final EntityPlayer entity = Minecraft.getMinecraft().world.getPlayerEntityByName(args[0]);

            if (entity == null) {
                Wrapper.sendMessage("找不到玩家 " + args[0]);
            } else {
                final double x = entity.posX,y = entity.posY,z = entity.posZ;
                Teleport.doTeleport(x,y,z);
                NotificationManager.Instance.addNotification("Teleport","马上将你传送到: " + (int) x + "," + (int) y + "," + (int) z, Notification.NotificationType.INFO,2000);
            }
        } else {
            printErrorMessage();
        }
        super.execute(args);
    }

    private void printErrorMessage() {
        for (String errorMessage : errorMessages)
            Wrapper.sendMessage(errorMessage);
    }
}
