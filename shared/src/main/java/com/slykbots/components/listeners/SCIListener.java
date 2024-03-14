package com.slykbots.components.listeners;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.function.Consumer;

public class SCIListener extends TypedListener<SlashCommandInteractionEvent> {
    public SCIListener(Consumer<SlashCommandInteractionEvent> c) {
        super(c, SlashCommandInteractionEvent.class);
    }
}
