package io.space.notification;

import io.space.renderer.font.FontManager;
import io.space.utils.AnimationUtils;
import io.space.utils.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.CopyOnWriteArrayList;

public final class NotificationManager {
    public static NotificationManager Instance;
    private final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public void draw(ScaledResolution scaledResolution) {
        float notificationY = scaledResolution.getScaledHeight() - 60;
        for (Notification notification : notifications) {
            final double secondDouble = (notification.timerUtil.getElapsedTime() / 1000.0) - (notification.getTime() / 1000.0);
            String second = (String.valueOf((String.format("%." + 1 + "f", secondDouble))));
            final double notificationWidth = scaledResolution.getScaledWidth() - FontManager.default16.getStringWidth(notification.getMessage() + " (" + second + ")");

            try {
                second = second.split("-")[1];
            } catch (Exception ignored) { }

            if (!notification.setY) {
                notification.animationY = notificationY;
                notification.setY = true;
            }

            if (notification.canRemove) {
                notification.animationX = RenderUtils.getAnimationState(notification.animationX,scaledResolution.getScaledWidth() + 30.0,notification.animationX > scaledResolution.getScaledWidth_double() + 28 ? 200 : AnimationUtils.easing(notification.animationX,scaledResolution.getScaledWidth() + 30,5));//notificationWidth / 3.0f
                if (notification.animationX >= scaledResolution.getScaledWidth_double() + 30.0) {
                    notifications.remove(notification);
                }
            } else {
                notification.animationX = RenderUtils.getAnimationStateEasing(notification.animationX,notificationWidth,8);
            }

            if (notification.timerUtil.hasReached(notification.getTime())) {
                notification.canRemove = true;
                notification.timerUtil.reset();
            } else {
                notification.animationY = RenderUtils.getAnimationState(notification.animationY,notificationY, Math.max(10,AnimationUtils.easing(notification.animationY,notificationY,8)));//240
            }

            final double timeX = (scaledResolution.getScaledWidth() - notification.animationX + 22) * ((notification.getTime() - notification.timerUtil.getElapsedTime()) / (double)notification.getTime());
            RenderUtils.drawRect(notification.animationX - 22.0,notification.animationY,scaledResolution.getScaledWidth_double(),notification.animationY + 20.0,RenderUtils.getRGB(0,0,0,50));

            final int imageColor;
            final String iconName;
            double imageXOffset = 0;
            double imageYOffset = 0;
            double imageWidthOffset = 0;
            double imageHeightOffset = 0;

            switch (notification.getType()) {
                case WARNING:
                    imageColor = RenderUtils.getRGB(255,215,100);
                    iconName = "textures/notificationicons/warning.png";
                    break;
                case ERROR:
                    imageColor = RenderUtils.getRGB(255, 100,100);
                    iconName = "textures/notificationicons/error.png";
                    break;
                case SUCCESS:
                    iconName = "textures/notificationicons/success.png";
                    imageColor = RenderUtils.getRGB(0, 200, 0);
                    break;
                case INFO:
                default:
                    iconName = "textures/notificationicons/info.png";
                    imageColor = RenderUtils.getRGB(180, 180, 180);
                    imageWidthOffset += 4;
                    imageHeightOffset += 4;
                    imageXOffset -= 2;
                    imageYOffset -= 2;
                    break;
            }

            if (!notification.canRemove) {
                RenderUtils.drawRect(notification.animationX - 22.0, notification.animationY + 19, scaledResolution.getScaledWidth_double() - timeX, notification.animationY + 20.0, imageColor);
            }

            RenderUtils.drawImage(notification.animationX - 20 + imageXOffset, notification.animationY + 2 + imageYOffset,16 + imageWidthOffset,16 + imageHeightOffset,imageColor,new ResourceLocation(iconName));
            FontManager.default16.drawStringWithShadow(notification.getTitle(),notification.animationX,notification.animationY + 1,RenderUtils.getRGB(251,251,251));
            FontManager.default16.drawStringWithShadow(notification.getMessage() + " (" + second + ")",notification.animationX,notification.animationY + 9,RenderUtils.getRGB(184,184,184));
            notificationY -= 32;
        }
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }

    public void addNotification(String title, String message, Notification.NotificationType type, long time) {
        notifications.add(new Notification(title, message, type, time));
    }
}
