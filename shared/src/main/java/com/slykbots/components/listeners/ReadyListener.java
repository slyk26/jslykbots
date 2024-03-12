package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.function.Consumer;

public class ReadyListener extends TypedListener<ReadyEvent> {
    public ReadyListener(Consumer<ReadyEvent> c) {
        super(c, ReadyEvent.class);
    }
}
