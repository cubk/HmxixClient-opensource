package io.space.network.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() == 0) return;

        final ProxyByteBuf buf = new ProxyByteBuf(in);
        final int id = buf.readInt();

        final Class<? extends IPacket> packetClass = PacketRegistry.getPacketClass(id);

        if (packetClass == null) {
            throw new IllegalArgumentException((((InetSocketAddress) ctx.channel().remoteAddress()).getHostName()) + " Illegal packet id: " + id);
        }

        final IPacket packet = packetClass.newInstance();

        packet.read(buf);

        out.add(packet);
    }
}
