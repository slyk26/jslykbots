package com.slykbots.markov.chains;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MarkovService {
    private static final Logger logger = LoggerFactory.getLogger(MarkovService.class);
    private final MarkovDao dao;

    public MarkovService() {
        this.dao = new MarkovDao();
    }

    public void handleMarkovChains(MessageReceivedEvent e) {
        var msg = e.getMessage().getContentDisplay();
        var server = e.getGuild().getId();

        if (e.isFromGuild() && !e.getAuthor().isBot())
            this.destructMessage(msg, server);

    }

    private void destructMessage(String msg, String server) {
        List<String> parts = List.of(msg.split(" "));

        for (int i = 0; i < parts.size(); i++) {
            var curr = parts.get(i);
            var next = i + 1 < parts.size() ? parts.get(i + 1) : null;
            logger.debug("{} {}", curr, next);

            var m = this.dao.getExistingCombination(curr, next, server);

            if (m != null) {
                logger.debug("found entry: {}", m);
                m.setFrequency(m.getFrequency() + 1);
                this.dao.update(m);
            } else {
                var n = new MarkovEntry();
                n.setFrequency(1);
                n.setCurrentWord(curr);
                n.setNextWord(next);
                n.setGuildId(server);
                var id = this.dao.create(n);
                logger.debug("Created Entry Id={}: {}", id, n);
            }
        }
    }
}
