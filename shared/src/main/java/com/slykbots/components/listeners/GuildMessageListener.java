package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

public class GuildMessageListener extends MessageListener{
    public GuildMessageListener(Consumer<MessageReceivedEvent> c) {
        super(d -> {
            if(d.isFromGuild())
                c.accept(d);
        });
    }
}
