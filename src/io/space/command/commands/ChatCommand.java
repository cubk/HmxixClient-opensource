package io.space.command.commands;

import io.space.Wrapper;
import io.space.command.Command;
import io.space.network.NetworkClient;
import io.space.network.common.client.CPacketMessage;

public final class ChatCommand extends Command {
    public ChatCommand() {
        super("chat");
    }

    @Override
    public void execute(String[] args) {
        super.execute(args);

        if (args.length == 0) {
            Wrapper.sendMessage("-chat <message>");
            return;
        }

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);

            if (i != args.length - 1) {
                builder.append(' ');
            }
        }

        NetworkClient.Instance.sendPacket(new CPacketMessage(builder.toString()));
    }
}
