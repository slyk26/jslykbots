package com.slykbots.markov.slashcommands;

import com.slykbots.components.commands.GuildOnlySlashCommand;
import com.slykbots.markov.Markov;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.text.DecimalFormat;


@EqualsAndHashCode(callSuper = true)
public class Info extends GuildOnlySlashCommand {
    private static final DecimalFormat df = new DecimalFormat("###,###,###");

    public Info() {
        super("info", "shows markov information about this server");
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        String guildId = super.getGuildId(event);
        EmbedBuilder e = new EmbedBuilder();

        boolean useGlobalTokens = Boolean.parseBoolean(Markov.ss.getSetting(guildId, Markov.USE_GLOBAL_KEY));

        e.setTitle("Markov Info");

        e.addField("Minimum Token Requirement", "Tokens required to generate messages: 1337", false);
        e.addField("Tokens Stored", String.format("Server: %s // Global: %s", df.format(Markov.ms.getTotalTokensOfGuild(guildId)), df.format(Markov.ms.getTotalTokens())), false);
        e.addField("Token Source", useGlobalTokens ? "Global" : "This Server only", false);
        e.addField("How to get Tokens", "Just chat and Markov generates them from messages sent. If you want to get a quickstart, change the setting with the `/toggle` command to use Global Tokens (global = yours + others)", false);
        e.addField("How to restrict Markov", "If you want it to not write/collect messages in certain channels, remove permissions for said channels.", false);
        e.setFooter("made by slyk26");
        event.replyEmbeds(e.build()).queue();
    }

}
