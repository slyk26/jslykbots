package com.slykbots.components.commands;

import com.slykbots.components.util.EnvLoader;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.slykbots.components.util.Helper.timed;

@Getter
public abstract class LegacyCommand {
    protected final ConcurrentHashMap<Long, Integer> cooldown = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(LegacyCommand.class);

    private final String name;
    private final String description;
    private final int pLength;
    private final Integer s;


    protected LegacyCommand(String name, String desc, int parameterLength) {
        this.name = name;
        this.description = desc;
        this.pLength = parameterLength;
        this.s = null;
    }

    protected LegacyCommand(String name, String description, int pLength, int s) {
        this.name = name;
        this.description = description;
        this.pLength = pLength;
        this.s = s;

        timed(() -> cooldown.forEach((id, time) -> {
            var t = time - 1;
            logger.debug("cd for {} {}s", name, t);
            if (t == 0) {
                cooldown.remove(id);
            } else {
                cooldown.put(id, t);
            }
        }), s);
    }

    public static String getLegacyKey() {
        return EnvLoader.getVar("LEGACY_KEY");
    }

    public boolean validate(String cmd, int length) {
        var c = getLegacyKey() + this.name;
        return c.equals(cmd) && length == this.pLength + 1;
    }

    public abstract void execute(MessageReceivedEvent e, List<String> args);

    public void handleLegacyCommand(MessageReceivedEvent e) {
        List<String> command = Arrays.asList(e.getMessage().getContentRaw().split(" ", 2));
        var userId = Objects.requireNonNull(e.getMember(), "[handleLegacyCommand] Member is null").getIdLong();
        if (this.validate(command.getFirst(), command.size())) {

            if(this.s != null && (cooldown.putIfAbsent(userId, this.s) != null)) {
                    e.getMessage().reply("you can ping in " + cooldown.get(userId) + "s again").queue();
                    return;

            }

            this.execute(e, command.subList(1, command.size()));
        }
    }
}
