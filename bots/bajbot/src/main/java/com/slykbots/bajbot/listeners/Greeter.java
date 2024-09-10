package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.MessageListener;

public class Greeter extends MessageListener {
    public Greeter() {
        super(c-> {
            var chan = c.getChannel();
            var msg = c.getMessage();
            var ret = switch (msg.getContentDisplay()) {
                case "selam" -> "Aleyküm Selam";
                case "dere" -> "dere oida";
                case "yo" -> "sup";
                default -> null;
            };

            if (ret == null) return;

            chan.sendMessage(ret).queue();
        });
    }
}
