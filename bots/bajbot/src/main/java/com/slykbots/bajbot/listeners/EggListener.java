package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.MessageListener;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class EggListener extends MessageListener {
    public EggListener() {
        super(c -> {
            try {
                if ("337700906751754246".equals(c.getMessage().getAuthor().getId())) {
                    c.getMessage().addReaction(Emoji.fromUnicode("\uD83C\uDFF3\uFE0F\u200D⚧\uFE0F")).queue();
                    c.getMessage().addReaction(Emoji.fromUnicode("\uD83E\uDD5A")).queue();
                }
            }catch(ErrorResponseException e){
                //
            }
        });
    }
}
