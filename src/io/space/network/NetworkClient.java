package io.space.network;

import by.radioegor146.annotation.Native;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.space.Wrapper;
import io.space.network.common.*;
import io.space.network.common.server.*;
import io.space.renderer.gui.GuiLogin;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class NetworkClient extends SimpleChannelInboundHandler<IPacket> implements ISPacketHandler, Closeable {
    public static NetworkClient Instance;

    private static final Logger logger = LogManager.getLogger("NetworkClient");

    public String userName;
    public int authResult;

    private Channel channel;
    private NioEventLoopGroup group;

    static {
        PacketRegistry.init();
    }

    public NetworkClient() {
        Instance = this;
    }

    @Native
    public void connect() {
        group = new NioEventLoopGroup(0, r -> {
            final Thread thread = new Thread(r, "Vector Netty Client Thread");

            thread.setDaemon(true);

            return thread;
        });

        final Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline()
                                .addLast("encoder", new PacketEncoder())
                                .addLast("decoder", new PacketDecoder())
                                .addLast("handler", NetworkClient.this);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        super.exceptionCaught(ctx, cause);

                        GuiLogin.processError(cause);

                        logger.error("Exception caught!", cause);
                        close();
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);

                        GuiLogin.processChannelInactive();
                        logger.info("Channel inactive");
                    }
                });

        final String code = "5L2g5pivc2INDUuMTI1LjQ1LjIw";
        final String s = new String(Base64.getDecoder().decode(code.substring(11).getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);

        final ChannelFuture future = bootstrap.connect(s, 1314).syncUninterruptibly();

        this.channel = future.channel();
    }

    public boolean isOpen() {
        return channel != null && channel.isOpen();
    }

    public void sendPacket(IPacket packet) {
        if (isOpen()) {
            channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            if (Minecraft.getMinecraft().world != null) {
                Wrapper.sendMessage("Can't send packet: Channel closed");
            }

            logger.warn("Try send packet {} but channel is closed", packet.getClass().getName());
        }
    }

    private boolean canSendMessageToPlayer() {
        return Minecraft.getMinecraft() != null && Minecraft.getMinecraft().isCallingFromMinecraftThread() && Minecraft.getMinecraft().world != null;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception {
        if (Minecraft.getMinecraft() != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> packet.process(NetworkClient.this));
        } else {
            packet.process(this);
        }
    }

    @Override
    public void close() {
        if (isOpen()) {
            channel.close().awaitUninterruptibly();
            group.shutdownGracefully();

            channel = null;
            group = null;
        }
    }

    @Native
    @Override
    public void processAuthResult(SPacketAuthResult packet) {
        GuiLogin.processAuthResult(packet);
    }

    @Native
    @Override
    public void processRegisterResult(SPacketRegisterResult packet) {
        GuiLogin.processRegisterResult(packet);
    }

    @Override
    public void processMessage(SPacketMessage packet) {
        if (canSendMessageToPlayer()) {
            Wrapper.sendMessageOriginal(packet.getMessage());
        }
    }

    @Override
    public void processServerClose(SPacketServerClose packet) {
        if (canSendMessageToPlayer()) {
            Wrapper.sendMessage("IRC Server closed!");
        }

        logger.info("Server closed");
    }

    @Override
    public void processKick(SPacketKick packet) {
        if (canSendMessageToPlayer()) {
            Wrapper.sendMessage("你被踢出IRC服务器!");
        }

        logger.info("Kicked");
    }
}
