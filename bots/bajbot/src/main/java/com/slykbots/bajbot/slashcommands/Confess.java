package com.slykbots.bajbot.slashcommands;

import com.slykbots.components.commands.GuildOnlySlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class Confess extends GuildOnlySlashCommand {
    public Confess() {
        super("confess", "Confess your sns");
    }

    @Override
    public SlashCommandData getData() {
        var d = super.getData();

        var confession = new OptionData(OptionType.STRING, "confession", "write your confession");
        confession.setRequired(true);

        d.addOptions(confession);
        return d;
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        var confession = Objects.requireNonNull(event.getOption("confession")).getAsString();
        var e = new EmbedBuilder().setTitle("CONFESSION").setDescription(confession);

        Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getChannelById(TextChannel.class, "1196465945447759913")).sendMessageEmbeds(e.build()).queue();
        event.reply("Confessed!").setEphemeral(true).queue();
    }
}
