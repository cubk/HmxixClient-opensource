package io.space.mod.other;

import com.darkmagician6.eventapi.EventTarget;
import io.space.Wrapper;
import io.space.events.EventPacket;
import io.space.events.EventPostUpdate;
import io.space.events.EventPreUpdate;
import io.space.events.EventWorldLoad;
import io.space.mod.Mod;
import io.space.notification.Notification;
import io.space.notification.NotificationManager;
import io.space.utils.ListsUtils;
import io.space.value.values.ModeValue;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import utils.hodgepodge.object.time.TimerUtils;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

public final class Disabler extends Mod {
    private final ModeValue mode = new ModeValue("Mode","BlocksMC",new String[]{"BlocksMC","MinePlex","Vulcan","Watchdog"});

    private final TimerUtils blocksMCTimerUtils = new TimerUtils(true);
    private final LinkedList<Packet<?>> blocksMCPacketList = new LinkedList<>();

    private final LinkedList<C0FPacketConfirmTransaction> minePlexPacketList = new LinkedList<>();

    private final LinkedList<C0FPacketConfirmTransaction> vulcanC0FPackets = new LinkedList<>();
    private final LinkedList<C00PacketKeepAlive> vulcanC00Packets = new LinkedList<>();

    private final Watchdog watchdog = new Watchdog();

    public Disabler() {
        super("Disabler",Category.OTHER);
        registerValues(mode);
    }

    @Override
    protected void onEnable() {
        if (mode.isCurrentMode("BlocksMC")) {
            Wrapper.sendMessage("BlocksMC Disabler by fdp client!");
        }
        super.onEnable();
    }

    @Override
    protected void onDisable() {
        if (mode.isCurrentMode("BlocksMC")) {
            if (!blocksMCPacketList.isEmpty()) {
                ListsUtils.foreachWithPoll(blocksMCPacketList,(packet) -> mc.getNetHandler().sendPacketNoEvent(packet));
            }
        } else if (mode.isCurrentMode("MinePlex")) {
            if (!minePlexPacketList.isEmpty()) {
                ListsUtils.foreachWithPoll(minePlexPacketList,(packet) -> mc.getNetHandler().sendPacketNoEvent(packet));
            }
        } else if (mode.isCurrentMode("Vulcan")) {
            if (!vulcanC0FPackets.isEmpty()) {
                ListsUtils.foreachWithPoll(vulcanC0FPackets,(packet) -> mc.getNetHandler().sendPacketNoEvent(packet));
            }

            if (!vulcanC00Packets.isEmpty()) {
                ListsUtils.foreachWithPoll(vulcanC00Packets,(packet) -> mc.getNetHandler().sendPacketNoEvent(packet));
            }
        }

        super.onDisable();
    }

    @EventTarget
    public void onPreUpdate(EventPreUpdate e) {
        if (mode.isCurrentMode("BlocksMC")) {
            if (blocksMCTimerUtils.hasReached(490)) {
                if (!blocksMCPacketList.isEmpty()) {
                    mc.getNetHandler().sendPacketNoEvent(blocksMCPacketList.poll());
                }
            }

            if (mc.player.ticksExisted % 40 == 0) {
                final ItemStack stack = mc.player.inventory.mainInventory[0];

                if (!(stack != null && stack.getItem() == Items.compass && stack.getDisplayName().endsWith("Game Menu"))) {
                    mc.getNetHandler().sendPacket(new C0CPacketInput());
                    e.setY(e.getY() - 0.114514);
                    e.setOnGround(false);
                }
            }
        } else if (mode.isCurrentMode("MinePlex")) {
            if (mc.player.ticksExisted % 10 == 5) {
                mc.getNetHandler().sendPacket(new C0FPacketConfirmTransaction(0, (short) -1, false));
                mc.getNetHandler().sendPacket(new C00PacketKeepAlive(-1));

                e.setY(e.getY() + 1.0E-4);
            }
        } else if (mode.isCurrentMode("Watchdog")) {
            //watchdog.onUpdate();
        }
    }

    @EventTarget
    public void onPostUpdate(EventPostUpdate e) {
        if (mode.isCurrentMode("Watchdog")) {
           // watchdog.onUpdate();
        }
    }

    @EventTarget
    public void onPacket(EventPacket e) {
        if (mode.isCurrentMode("BlocksMC")) {
            if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                blocksMCPacketList.add(e.getPacket());
                e.cancelEvent();

                if(blocksMCPacketList.size() > 300) {
                    mc.getNetHandler().sendPacketNoEvent(blocksMCPacketList.poll());
                }
            } else if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                if (mc.player.ticksExisted >= 20) {
                    final ItemStack stack = mc.player.inventory.mainInventory[0];

                    if (!(stack != null && stack.getItem() == Items.compass && stack.getDisplayName().endsWith("Game Menu"))) {
                        final S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
                        final double x = packet.getX() - mc.player.posX;
                        final double y = packet.getY() - mc.player.posY;
                        final double z = packet.getZ() - mc.player.posZ;
                        final double diff = Math.sqrt(x * x + y * y + z * z);

                        if (diff <= 8) {
                            e.cancelEvent();
                            mc.getNetHandler().sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), true));
                        }
                    }
                }
            }

            if (mc.player.ticksExisted <= 7) {
                blocksMCTimerUtils.reset();
                blocksMCPacketList.clear();
            }
        } else if (mode.isCurrentMode("Vulcan")) {
            if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                final C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction) e.getPacket();

                if (packet.getWindowId() == 0) {
                    if (vulcanC0FPackets.size() > 5) {
                        for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, vulcanC0FPackets.size()); i++) {
                            mc.getNetHandler().sendPacketNoEvent(vulcanC0FPackets.poll());
                        }
                    }

                    vulcanC0FPackets.offer(packet);
                }
            } else if (e.getPacket() instanceof C00PacketKeepAlive) {
                if (vulcanC00Packets.size() > 1) {
                    for (int i = 0; i < ThreadLocalRandom.current().nextInt(1,vulcanC00Packets.size()); i++) {
                        mc.getNetHandler().sendPacketNoEvent(vulcanC00Packets.poll());
                    }
                }

                vulcanC00Packets.offer(((C00PacketKeepAlive) e.getPacket()));
            }
        } else if (mode.isCurrentMode("Watchdog")) {
            //watchdog.onPacket(e);
        }
    }

    @EventTarget
    public void onWorldLoad(EventWorldLoad e) {
        if (mode.isCurrentMode("MinePlex")) {
            minePlexPacketList.clear();
        } else if (mode.isCurrentMode("Vulcan")) {
            vulcanC00Packets.clear();
            vulcanC0FPackets.clear();
        } else if (mode.isCurrentMode("Watchdog")) {
            //watchdog.loadWorld();
        }
    }

    private static class Watchdog {
        private final ConcurrentLinkedQueue<C0FPacketConfirmTransaction> confirmTransactionQueue = new ConcurrentLinkedQueue<>();
        private final ConcurrentLinkedQueue<C00PacketKeepAlive> keepAliveQueue = new ConcurrentLinkedQueue<>();
        private boolean disabled = false;
        private int lastuid = 0;
        private final TimerUtils lastRelease = new TimerUtils();
        private final TimerUtils timer2 = new TimerUtils();

        private void loadWorld() {
            timer2.reset();
            confirmTransactionQueue.clear();
            disabled = false;
            lastuid = 0;
        }

        private void onPacket(EventPacket e) {
            if (disabled) {
                if (e.getPacket() instanceof C03PacketPlayer) {
                    e.cancelEvent();
                }
            }

            if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                final C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction) e.getPacket();

                if (packet.getWindowId() == 0 && packet.getUid() < 0 && packet.getUid() != -1) {
                    if (disabled) {
                        e.cancelEvent();
                    }
                }

                processConfirmTransactionPacket(e, packet);
            }

            if (e.getPacket() instanceof C00PacketKeepAlive) {
                processKeepAlivePacket(e, (C00PacketKeepAlive) e.getPacket());
            }
        }

        private void onUpdate() {
            if (disabled) {
                if (confirmTransactionQueue.isEmpty()) {
                    lastRelease.reset();
                } else {
                    if (confirmTransactionQueue.size() >= 6) {
                        while (!keepAliveQueue.isEmpty()) mc.getNetHandler().sendPacketNoEvent(keepAliveQueue.poll());
                        while (!confirmTransactionQueue.isEmpty()) {
                            mc.getNetHandler().sendPacketNoEvent(confirmTransactionQueue.poll());
                        }
                    }
                }
            }
        }

        private void processConfirmTransactionPacket(EventPacket e, C0FPacketConfirmTransaction packet) {
            int preuid = lastuid - 1;

            if (packet.getWindowId() == 0 || packet.getUid() < 0) {
                if ((int) packet.getUid() == preuid) {
                    if (!disabled) {
                        NotificationManager.Instance.addNotification("Disabler", "Watchdog disabled", Notification.NotificationType.INFO, 1000);
                        disabled = true;
                    }
                    confirmTransactionQueue.offer(packet);
                    e.cancelEvent();
                }
                lastuid = packet.getUid();
            }
        }

        private void processKeepAlivePacket(EventPacket e, C00PacketKeepAlive packet) {
            if (disabled) {
                keepAliveQueue.offer(packet);
                e.cancelEvent();
            }
        }
    }
}
