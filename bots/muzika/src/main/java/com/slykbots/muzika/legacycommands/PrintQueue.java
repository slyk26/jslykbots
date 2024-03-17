package com.slykbots.muzika.legacycommands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintQueue extends LegacyCommand {
    private static Logger logger = LoggerFactory.getLogger(PrintQueue.class);
    public PrintQueue() {
        super("list", "print all queued Songs", 0);
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

        em.setTitle("Playlist");
        em.setColor(Color.RED);

        for (AudioTrack b : a) {
            logger.debug("duration: {}", b.getDuration());
            cnt.addAndGet(1);
            var i = b.getInfo();
            float d = i.length;
            float min = (d / 1000 / 60);
            float sec = (min - (int) min) / 100 * 60 * 100;
            var src = (i.identifier.length() == 11 ? "Youtube" : "Soundcloud");
            var time = i.isStream ? "LIVE" : String.format("%02d:%02d", (int)min, (int)sec);

            em.addField((a.indexOf(b) == 0 ? "__Now playing__: " : "") + i.title + " - " + i.author, time + " - " + src , false);

            if (cnt.get() == 6) {
                em.setFooter("... and " + (a.size() - 6) + " more!");
                break;
            }
        }
        c.sendMessageEmbeds(em.build()).queue();
    }
}
