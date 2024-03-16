package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

import java.util.function.Consumer;

public class VoiceChannelListener extends TypedListener<GuildVoiceUpdateEvent> {
    public VoiceChannelListener(Consumer<GuildVoiceUpdateEvent> c) {
        super(c, GuildVoiceUpdateEvent.class);
    }
}
