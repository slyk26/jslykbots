package com.slykbots.muzika.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Objects;

public class PrintQueue extends LegacyCommand {
    public PrintQueue() {
        super("list", 0);
    }

    @Override
    public void execute(TextChannel c, List<String> args) {
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        var a = musicManager.scheduler.getPlaylist().stream().filter(Objects::nonNull).toList();
        StringBuilder sb = new StringBuilder();

        if (a.isEmpty()) {
            c.sendMessage("Queue is empty. Add music with "+ this.getLegacyKey() +"yt or " + this.getLegacyKey() + "sc.").queue();
            return;
        }

        a.forEach(b -> {
            var i = b.getInfo();
            var s = i.length;

            sb.append("[").append(i.identifier.length() == 11 ? "YT" : "SC").append("]");

            if (i.isStream) {
                sb.append("[LIVE]");
            } else {
                sb.append(String.format("[%02d:%02d]", s / 60000, (s % 10000) / 100 * 60 / 100));
            }

            sb.append(" ").append(i.title).append("\n");
        });

        c.sendMessage(sb.toString()).queue();
    }
}
