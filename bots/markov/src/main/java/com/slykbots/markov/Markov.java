package com.slykbots.markov;

import com.slykbots.components.commands.Ping;
import com.slykbots.components.commands.SlashCommand;
import com.slykbots.components.commands.Toggle;
import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.GuildJoinListener;
import com.slykbots.components.listeners.GuildMessageListener;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.listeners.SCIListener;
import com.slykbots.components.settings.SettingService;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.markov.chains.MarkovService;
import com.slykbots.markov.slashcommands.Info;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class Markov {

    public static final String USE_GLOBAL_KEY = "markov.useGlobalTokens";
    public static final String LEARN_KEY = "markov.learnFromServer";
    private static final List<SlashCommand> c = List.of(
            new Ping(),
            new Info(),
            new Toggle(Map.of(
                    USE_GLOBAL_KEY, "Generate with Global Tokens",
                    LEARN_KEY, "Learn from this Server"
            ))
    );

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Markov.class);
        MarkovService service = new MarkovService();
        SettingService ss = new SettingService();
        DB.healthcheck();

        JDA jda = JDABuilder.createDefault(EnvLoader.getVar("MARKOV_KEY"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())))
                .addEventListeners(new GuildMessageListener(service::handleMarkovChains))
                .addEventListeners(new SCIListener(e -> c.forEach(cmd -> cmd.onSlashCommandInteraction(e))))
                .addEventListeners(new GuildJoinListener(e -> {
                    var gi = e.getGuild().getId();
                    if(ss.getSetting(gi, USE_GLOBAL_KEY) == null) ss.setSetting(gi, USE_GLOBAL_KEY, "false");
                    if(ss.getSetting(gi, LEARN_KEY) == null) ss.setSetting(gi, LEARN_KEY, "false");
                }))
                .disableCache(CacheFlag.MEMBER_OVERRIDES)
                .setActivity(Activity.customStatus("Forsen")).build();

        jda.updateCommands().addCommands(c.stream().map(SlashCommand::getData).toList()).queue();
    }
}