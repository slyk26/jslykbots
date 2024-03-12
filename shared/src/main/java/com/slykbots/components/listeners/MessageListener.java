package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

public class MessageListener extends TypedListener<MessageReceivedEvent> {
    public MessageListener(Consumer<MessageReceivedEvent> c) {
        super(c, MessageReceivedEvent.class);
    }
}
