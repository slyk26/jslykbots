package com.slykbots.markov;

import com.slykbots.components.commands.SlashCommand;
import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.GuildMessageListener;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.listeners.SCIListener;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.markov.chains.MarkovService;
import com.slykbots.markov.slashcommands.Info;
import com.slykbots.markov.slashcommands.Ping;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Markov {
    private static final List<SlashCommand> c = List.of(new Ping(), new Info());

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Markov.class);
        MarkovService service = new MarkovService();
        DB.healthcheck();

        JDA jda = JDABuilder.createDefault(EnvLoader.getVar("MARKOV_KEY"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())))
                .addEventListeners(new GuildMessageListener(service::handleMarkovChains))
                .addEventListeners(new SCIListener(e -> c.forEach(cmd -> cmd.onSlashCommandInteraction(e))))
                .disableCache(CacheFlag.MEMBER_OVERRIDES)
                .setActivity(Activity.customStatus("Forsen")).build();

        jda.updateCommands().addCommands(c.stream().map(SlashCommand::getData).toList()).queue();
    }
}