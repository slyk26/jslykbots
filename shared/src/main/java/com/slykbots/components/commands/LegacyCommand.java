package com.slykbots.components.commands;

import com.slykbots.components.util.EnvLoader;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
public abstract class LegacyCommand {

    private static final Map<String, LocalDateTime> cooldowns = new HashMap<>();

    private final String name;
    private final String description;
    private final int pLength;
    private final Integer cooldownSeconds;


    protected LegacyCommand(String name, String desc, int parameterLength) {
        this.name = name;
        this.description = desc;
        this.pLength = parameterLength;
        this.cooldownSeconds = null;
    }

    protected LegacyCommand(String name, String description, int pLength, int cdSec) {
        this.name = name;
        this.description = description;
        this.pLength = pLength;
        this.cooldownSeconds = cdSec;
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
        if (this.validate(command.getFirst(), command.size())) {
            if (this.cooldownSeconds != null) {
                var user = Objects.requireNonNull(e.getMember()).getId();
                var done = cooldowns.get(user);

                if (done != null) {
                    if (done.isAfter(LocalDateTime.now())) {
                        e.getMessage().reply(String.format("You can use that command in %ss again.", ChronoUnit.SECONDS.between(LocalDateTime.now(), done))).queue();
                        return;
                    } else {
                        cooldowns.remove(user);
                    }
                } else {
                    cooldowns.put(user, LocalDateTime.now().plusSeconds(this.cooldownSeconds));
                }
            }
            this.execute(e, command.subList(1, command.size()));
        }
    }
}
