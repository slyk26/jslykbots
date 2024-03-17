package com.slykbots.components.commands;

import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
public class Help extends GuildOnlySlashCommand {

    private final List<SlashCommand> s;
    private final List<LegacyCommand> l;

    public Help(final List<SlashCommand> slash, final List<LegacyCommand> leg) {
        super("help", "get an Overview of available Commands");
        this.s = slash;
        this.l = leg;
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        EmbedBuilder e = new EmbedBuilder();

        e.setTitle("Available Commands");

        for (SlashCommand c : s) {
            e.addField("/" + c.getName(), c.getDescription(), false);
        }

        for(LegacyCommand c: l) {
            e.addField(LegacyCommand.getLegacyKey() + c.getName(), c.getDescription(), false);
        }

        event.replyEmbeds(e.build()).queue();
    }
}
