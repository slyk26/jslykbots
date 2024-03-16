package com.slykbots.muzika.legacycommands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class FastForward extends LegacyCommand {
    public FastForward() {
        super("ff", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        if (Muzika.vcCheck(e)) return;
        var c = e.getChannel().asTextChannel();
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        List<AudioTrack> pl = musicManager.scheduler.getPlaylist();

        while(pl.size() > 1 ){
            musicManager.scheduler.nextTrack();
            pl.removeFirst();
        }

        c.sendMessage(">> fast forward to newest Song >> ").queue();
    }
}
