package com.slykbots.markov;

import com.slykbots.components.listeners.MessageListener;
import com.slykbots.components.listeners.ReadyListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class Markov {
    private static final Logger logger = LoggerFactory.getLogger(Markov.class);

    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault(args[0]);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        builder.addEventListeners(new ReadyListener(e -> logger.debug("Ready called")));
        builder.addEventListeners(new MessageListener(Markov::resendMessage));

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.customStatus("Forsen"));

        builder.build();
    }

    private static void resendMessage(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot())
            e.getChannel().sendMessage(MessageFormat.format("{0}: {1}", e.getAuthor().getName(), e.getMessage().getContentDisplay())).queue();
    }
}