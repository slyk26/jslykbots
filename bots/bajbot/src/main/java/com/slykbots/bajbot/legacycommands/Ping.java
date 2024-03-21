package com.slykbots.bajbot.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Random;

public class Ping extends LegacyCommand {
    private final Random r = new Random();

    public Ping() {
        super("ping", "`" + LegacyCommand.getLegacyKey() + "ping someone who`...\nMentions a random user. Additional Text has to be added for context", 1, 60);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        var users = e.getGuild().getMembers();

        var u = users.get(r.nextInt(users.size()));

        e.getMessage().reply(String.format("<@%s>", u.getId())).queue();
    }
}
