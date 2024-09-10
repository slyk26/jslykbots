package com.slykbots.bajbot.slashcommands;

import com.slykbots.components.commands.GuildOnlySlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class Avatar extends GuildOnlySlashCommand {
    public Avatar() {
        super("avatar", "get pfp of user");
    }

    @Override
    public SlashCommandData getData() {
        var d = super.getData();
        var user = new OptionData(OptionType.USER, "user", "the wanted user pfp");
        user.setRequired(true);

        d.addOptions(user);
        return d;
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        var user = Objects.requireNonNull(event.getOption("user").getAsUser());
        var url = user.getAvatarUrl();
        var b = new EmbedBuilder();

        if(url != null) {
            b.setImage(url);
        } else {
            b.setDescription("user has no pfp");
        }
        b.setTitle(user.getName());

        event.replyEmbeds(b.build()).queue();

    }
}


