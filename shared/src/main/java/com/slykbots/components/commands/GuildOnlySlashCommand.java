package com.slykbots.components.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.Objects;

public abstract class GuildOnlySlashCommand extends SlashCommand {

    protected GuildOnlySlashCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            super.onSlashCommandInteraction(event);
        } else {
            event.reply("This command only works in Servers.").queue();
        }
    }

    protected String getGuildId(SlashCommandInteraction e) {
        return Objects.requireNonNull(e.getGuild(), "[guildOnlyGuild] Guild is null").getId();
    }
}
