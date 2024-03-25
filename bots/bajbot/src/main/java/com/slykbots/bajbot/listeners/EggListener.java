package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.MessageListener;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class EggListener extends MessageListener {
    public EggListener() {
        super(c -> {
           if("337700906751754246".equals(c.getMessage().getAuthor().getId())){
               c.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDFF3\uFE0F\u200D⚧\uFE0F")).queue();
               c.getMessage().addReaction(Emoji.fromUnicode("\uD83E\uDD5A")).queue();
            }
        });
    }
}
