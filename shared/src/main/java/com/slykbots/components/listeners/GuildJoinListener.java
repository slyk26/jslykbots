package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.guild.GuildJoinEvent;

import java.util.function.Consumer;

public class GuildJoinListener extends TypedListener<GuildJoinEvent>{
    public GuildJoinListener(Consumer<GuildJoinEvent> c) {
        super(c, GuildJoinEvent.class);
    }
}
