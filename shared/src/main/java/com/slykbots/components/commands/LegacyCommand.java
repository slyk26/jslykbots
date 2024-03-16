package com.slykbots.components.commands;

import com.slykbots.components.util.EnvLoader;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public abstract class LegacyCommand {
    private final String name;
    private final int pLength;


    protected LegacyCommand(String name, int parameterLength) {
        this.name = name;
        this.pLength = parameterLength;
    }

    public String getLegacyKey() {
        return EnvLoader.getVar("LEGACY_KEY");
    }

    public boolean validate(String cmd, int length) {
        var c = this.getLegacyKey() + this.name;
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
