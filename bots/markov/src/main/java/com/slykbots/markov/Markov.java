package com.slykbots.markov;

import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.MessageListener;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.markov.chains.MarkovService;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Markov {
    private static final Logger logger = LoggerFactory.getLogger(Markov.class);

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault(EnvLoader.getVar("MARKOV_KEY"));
        MarkovService service = new MarkovService();
        DB.healthcheck();

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())));
        builder.addEventListeners(new MessageListener(service::handleMarkovChains));
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.customStatus("Forsen"));

        builder.build();
    }
}