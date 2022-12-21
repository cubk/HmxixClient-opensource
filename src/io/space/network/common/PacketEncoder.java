package io.space.network.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket msg, ByteBuf out) throws Exception {
        final int id = PacketRegistry.getID(msg.getClass());

        if (id == -1) {
            throw new Exception("Can't encode packet " + msg.getClass().getName() + " because id not found");
        }

        final ProxyByteBuf proxy = new ProxyByteBuf(out);

        out.writeInt(id);
        msg.write(proxy);
    }
}
