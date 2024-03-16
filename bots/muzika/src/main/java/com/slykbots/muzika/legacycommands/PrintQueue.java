package com.slykbots.muzika.legacycommands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintQueue extends LegacyCommand {
    public PrintQueue() {
        super("list", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        AtomicInteger cnt = new AtomicInteger();
        var c = e.getChannel().asTextChannel();
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        var a = musicManager.scheduler.getPlaylist().stream().filter(Objects::nonNull).toList();
        StringBuilder sb = new StringBuilder();

        if (a.isEmpty()) {
            c.sendMessage("Queue is empty. Add music with "+ this.getLegacyKey() +"yt or " + this.getLegacyKey() + "sc.").queue();
            return;
        }

        for (AudioTrack b : a) {
            cnt.addAndGet(1);
            var i = b.getInfo();
            var s = i.length;

            sb.append("[").append(i.identifier.length() == 11 ? "YT" : "SC").append("]");

            if (i.isStream) {
                sb.append("[LIVE]");
            } else {
                float f = s%10000;
                sb.append(String.format("[%02d:%02.0f]", s / 60000, (f / 1000)));
            }

            sb.append(" ").append(i.title).append("\n");

            if (cnt.get() == 10) {
                sb.append("... and ").append(a.size() - 10).append(" more!");
                break;
            }
        }

        c.sendMessage(sb.toString()).queue();
    }
}
