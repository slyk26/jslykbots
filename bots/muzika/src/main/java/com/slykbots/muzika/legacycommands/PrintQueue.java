package com.slykbots.muzika.legacycommands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintQueue extends LegacyCommand {
    public PrintQueue() {
        super("list", "print all queued songs", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        AtomicInteger cnt = new AtomicInteger();
        var c = e.getChannel().asGuildMessageChannel();
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        var a = musicManager.scheduler.getPlaylist().stream().filter(Objects::nonNull).toList();
        EmbedBuilder em = new EmbedBuilder();

        if (a.isEmpty()) {
            c.sendMessage("Queue is empty. Add music with "+ LegacyCommand.getLegacyKey() +"yt or " + LegacyCommand.getLegacyKey() + "sc.").queue();
            return;
        }

        em.setTitle("Current Bangers");
        em.setColor(Color.RED);

        for (AudioTrack b : a) {
            cnt.addAndGet(1);
            var i = b.getInfo();
            var s = i.length;
            float f = s%10000;
            em.addField("[" + (i.identifier.length() == 11 ? "Youtube" : "Soundcloud") + "]" + (i.isStream ? "[LIVE]" : String.format(" - %02d:%02.0f", s / 60000, (f / 1000)))
                    , i.title + " /// " + i.author , false);

            if (cnt.get() == 10) {
                em.setFooter("... and " + (a.size() - 10) + " more!");
                break;
            }
        }
        c.sendMessageEmbeds(em.build()).queue();
    }
}
