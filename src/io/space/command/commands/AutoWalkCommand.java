package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.pathfinder.PathFinder;
import io.space.utils.pathfinder.SigmaVec3;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import utils.hodgepodge.object.StringUtils;

import java.util.List;

public final class AutoWalkCommand extends Command {
    public static boolean needWalk = false;
    public static List<SigmaVec3> path;
    public static int index = 0;

    public AutoWalkCommand() {
        super("autowalk");
        setHelp("Auto walk to a position");
    }

    @Override
    public void execute(String[] args) {
        super.execute(args);

        if (args.length == 1) {
            for (EntityPlayer playerEntity : Minecraft.getMinecraft().world.playerEntities) {
                if (playerEntity.getName().equalsIgnoreCase(args[0])) {
                    path = PathFinder.computePath(new SigmaVec3(Minecraft.getMinecraft().player.posX,Minecraft.getMinecraft().player.posY,Minecraft.getMinecraft().player.posZ),new SigmaVec3(playerEntity.posX,playerEntity.posY,playerEntity.posZ));
                    index = 0;
                    needWalk = true;
                    return;
                }
            }

            if (args[0].equalsIgnoreCase("stop")) {
                if (needWalk) {
                    path = null;
                    index = 0;
                    needWalk = false;
                    NotificationManager.Instance.addNotification("AutoWalk", "Stop auto walk!", Notification.NotificationType.INFO, 5000);
                } else {
                    NotificationManager.Instance.addNotification("AutoWalk","别闹宝 咱压根就没有开始走", Notification.NotificationType.INFO,5000);
                }
            } else {
                Wrapper.sendMessage("找不到玩家 " + args[0]);
            }

            return;
        }

            if (args.length < 3) {
                Wrapper.sendMessage("-autowalk <x> <y> <z>");
                Wrapper.sendMessage("-autowalk stop");
                Wrapper.sendMessage("-autowalk <PlayerName>");
                return;
            }

        if (StringUtils.isNumber(args[0])) {
            if (StringUtils.isNumber(args[1])) {
                if (StringUtils.isNumber(args[2])) {
                    final double x = Double.parseDouble(args[0]);
                    final double y = Double.parseDouble(args[1]);
                    final double z = Double.parseDouble(args[2]);

                    path = PathFinder.computePath(new SigmaVec3(Minecraft.getMinecraft().player.posX,Minecraft.getMinecraft().player.posY,Minecraft.getMinecraft().player.posZ),new SigmaVec3(x,y,z));
                    index = 0;
                    needWalk = true;

                    NotificationManager.Instance.addNotification("AutoWalk",StringUtils.compileString("设定AutoWalk X:{0} Y:{1} Z:{2}",x,y,z), Notification.NotificationType.INFO,5000);
                } else {
                    Wrapper.sendMessage(args[2] + " 不是一个数字!");
                }
            } else {
                Wrapper.sendMessage(args[1] + " 不是一个数字!");
            }
        } else {
            Wrapper.sendMessage(args[0] + " 不是一个数字!");
        }
    }
}
