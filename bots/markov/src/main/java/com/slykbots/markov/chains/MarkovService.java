package com.slykbots.markov.chains;

import com.slykbots.components.util.EnvLoader;
import com.slykbots.components.util.Helper;
import com.slykbots.markov.Markov;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class MarkovService {
    private static final Logger logger = LoggerFactory.getLogger(MarkovService.class);
    private final MarkovDao dao;

    private static final int MINIMUM_PARTS = 3;
    private final Random r = new Random();

    public MarkovService() {
        this.dao = new MarkovDao();
    }

    public void handleMarkovChains(MessageReceivedEvent e) {
        var msg = e.getMessage().getContentDisplay();

        // ignore attachment only messages
        if (msg.isBlank())
            return;

        var server = e.getGuild().getId();

        if (e.isFromGuild() && !e.getAuthor().isBot() && !Helper.isBotMentioned(e.getMessage()))
            this.destructMessage(msg, server);

        if ((Helper.isBotMentioned(e.getMessage()) || r.nextInt(100 - 1) + 1 <= 3) && e.isFromGuild() && !e.getAuthor().isBot() && "true".equals(Markov.ss.getSetting(server, Markov.GENERATE_KEY)))
            e.getChannel().sendMessage(this.generateMessage(server)).queue();


    }

    private void destructMessage(String msg, String server) {
        boolean learn = Boolean.parseBoolean(Markov.ss.getSetting(server, Markov.LEARN_KEY));

        if (!learn || msg.startsWith(EnvLoader.getVar("LEGACY_KEY"))) return;

        List<String> parts = List.of(msg.split(" "));

        for (int i = 0; i < parts.size(); i++) {
            var curr = parts.get(i);
            var next = i + 1 < parts.size() ? parts.get(i + 1) : null;
            logger.debug("{} {}", curr, next);

            var m = this.dao.getExistingCombination(curr, next, server);

            if (m != null) {
                logger.debug("found token: {}", m);
                m.setFrequency(m.getFrequency() + 1);
                this.dao.update(m);
            } else {
                var n = new MarkovToken();
                n.setFrequency(1);
                n.setCurrentWord(curr);
                n.setNextWord(next);
                n.setGuildId(server);
                var id = this.dao.create(n);
                logger.debug("Created token Id={}: {}", id, n);
            }
        }
    }

    private String generateMessage(String guildId) {
        boolean fromGlobal = Boolean.parseBoolean(Markov.ss.getSetting(guildId, Markov.USE_GLOBAL_KEY));

        logger.debug("guild: {} global: {}", guildId, fromGlobal);

        if (this.dao.getCount(guildId) < 1337 && !fromGlobal) {
            return "me not smart enough";
        }

        StringBuilder msg = new StringBuilder();
        var parts = 0;
        var token = this.dao.getRandom(guildId, fromGlobal);

        while (msg.length() < 2000) {
            msg.append(token.getCurrentWord()).append(" ");
            parts += 1;
            if (token.getNextWord() != null) {
                token = this.dao.getNext(token.getNextWord(), guildId, fromGlobal);
            } else if (parts <= MINIMUM_PARTS) {
                token = this.dao.getRandom(guildId, fromGlobal);
            } else {
                return msg.toString();
            }
        }
        return msg.toString();
    }

    public long getTotalTokens() {
        return this.dao.getTotalTokens();
    }

    public long getTotalTokensOfGuild(String guildId) {
        return this.dao.getTotalTokensOfGuild(guildId);
    }
}
