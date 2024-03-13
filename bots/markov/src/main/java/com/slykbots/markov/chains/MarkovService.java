package com.slykbots.markov.chains;

import com.slykbots.components.util.Helper;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class MarkovService {
    private static final Logger logger = LoggerFactory.getLogger(MarkovService.class);
    private final MarkovDao dao;
    private static final int MINIUM_PARTS = 3;
    private final Random r = new Random();

    public MarkovService() {
        this.dao = new MarkovDao();
    }

    public void handleMarkovChains(MessageReceivedEvent e) {
        var msg = e.getMessage().getContentDisplay();
        var server = e.getGuild().getId();

        if (e.isFromGuild() && !e.getAuthor().isBot() && !Helper.isBotMentioned(e.getMessage()))
            this.destructMessage(msg, server);

        if ((Helper.isBotMentioned(e.getMessage()) || r.nextInt(100 - 1) + 1 <= 7) && e.isFromGuild() && !e.getAuthor().isBot())
            e.getChannel().sendMessage(this.generateMessage(server)).queue();


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

    private String generateMessage(String guildId) {
        if (this.dao.getCount(guildId) < 1337) {
            return "me not smart enough";
        }

        StringBuilder msg = new StringBuilder();
        var parts = 0;
        var entry = this.dao.getRandom(guildId);

        while (msg.length() < 2000) {
            msg.append(entry.getCurrentWord());
            msg.append(" ");
            parts += 1;
            if (entry.getNextWord() != null) {
                entry = this.dao.getNext(entry.getNextWord(), guildId);
            } else if (parts <= MINIUM_PARTS) {
                entry = this.dao.getRandom(guildId);
            } else {
                return msg.toString();
            }
        }
        return msg.toString();
    }
}
