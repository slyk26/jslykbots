package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.MessageListener;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.Objects;

public class NotifyMirrorListener extends MessageListener {
    public NotifyMirrorListener() {
        super(c -> {
            if("1044050359586394192".equals(Objects.requireNonNull(c.getMember()).getId())){
                var msg = c.getMessage().getContentRaw();
                var embed = c.getMessage().getEmbeds().getFirst();
                var button = c.getMessage().getActionRows().getFirst().getActionComponents();
                var d = c.getGuild().getChannelById(ThreadChannel.class, "1216722790061707284");

                if(msg.contains("drhouse")){
                    d = c.getGuild().getChannelById(ThreadChannel.class, "1218192945891643494");
                }

                Objects.requireNonNull(d).sendMessageEmbeds(embed).addActionRow(button).queue();
            }
        });
    }
}
