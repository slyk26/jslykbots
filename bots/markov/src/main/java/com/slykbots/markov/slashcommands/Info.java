package com.slykbots.markov.slashcommands;

import com.slykbots.components.commands.GuildOnlySlashCommand;
import com.slykbots.markov.chains.MarkovService;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

import java.text.DecimalFormat;


@EqualsAndHashCode(callSuper = true)
public class Info extends GuildOnlySlashCommand {
    private static final DecimalFormat df = new DecimalFormat("###,###,###");

    private final MarkovService ms;

    public Info() {
        super("info", "shows markov information about this server");
        this.ms = new MarkovService();
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        String guildId = super.getGuild(event).getId();
        EmbedBuilder e = new EmbedBuilder();

        e.setTitle("Markov Info");

        e.addField("Minimum Token Requirement", "Tokens required to generate messages: 1337", false);
        e.addField("Tokens Stored", String.format("Server: %s // Global: %s", df.format(this.ms.getTotalTokensOfGuild(guildId)), df.format(this.ms.getTotalTokens())), false);
        e.addField("How to get Tokens", "Just chat and Markov generates them from messages sent", false);
        e.addField("How to restrict Markov", "If you want it to not write/collect messages in certain channels, remove permissions for said channels.", false);
        e.setFooter("made by slyk26");
        event.replyEmbeds(e.build()).queue();
    }

}
