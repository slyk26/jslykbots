package com.slykbots.components.commands;

import com.slykbots.components.util.EnvLoader;
import lombok.Getter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

@Getter
public abstract class LegacyCommand {
    private final String name;

    private final String description;
    private final int pLength;


    protected LegacyCommand(String name, String desc, int parameterLength) {
        this.name = name;
        this.description = desc;
        this.pLength = parameterLength;
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
            this.execute(e, command.subList(1, command.size()));
        }
    }
}
