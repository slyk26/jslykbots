package com.slykbots.bajbot;

import com.slykbots.bajbot.legacycommands.Ping;
import com.slykbots.components.commands.Help;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.components.commands.SlashCommand;
import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.MessageListener;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.listeners.SCIListener;
import com.slykbots.components.util.EnvLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BajBot {
     private static final List<SlashCommand> c = new ArrayList<>(Arrays.asList(
    ));

    private static final List<LegacyCommand> l = List.of(
            new Ping()
    );

    static {
        c.add(new Help(c, l));
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(BajBot.class);
        DB.healthcheck();

        JDA jda = JDABuilder.createDefault(EnvLoader.getVar("BAJBOT_KEY"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())))
                .addEventListeners(new SCIListener(e -> c.forEach(cmd -> cmd.onSlashCommandInteraction(e))))
                .addEventListeners(new MessageListener(e -> l.forEach(cmd -> cmd.handleLegacyCommand(e))))
                .setActivity(Activity.customStatus("discord.gg/bajs")).build();

        jda.updateCommands().addCommands(c.stream().map(SlashCommand::getData).toList()).queue();
    }
}