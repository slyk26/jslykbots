package com.slykbots.markov.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class Who extends LegacyCommand {
    private final Random r = new Random();

    public Who() {
        super("who", getLegacyKey() + "who is/has...\nMentions a random user. Additional Text has to be added for context", 1);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        var users = e.getGuild().getMembers();

        var u = users.get(r.nextInt(users.size()));

        e.getMessage().reply(String.format("<@%s>", u.getId())).queue();
    }
}
