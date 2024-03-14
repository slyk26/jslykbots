package com.slykbots.markov.slashcommands;

import com.slykbots.components.commands.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class Ping extends SlashCommand {
    public Ping() {
        super("ping", "Checks the Ping of the Bot.");
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true) // reply or acknowledge
                .flatMap(v ->
                        event.getHook().editOriginalFormat("Pong: %d ms", System.currentTimeMillis() - time) // then edit original
                ).queue(); // Queue both reply and edit

    }
}
