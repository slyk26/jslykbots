package com.slykbots.muzika.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

public class Skip extends LegacyCommand {
    public Skip() {
        super("skip", 0);
    }

    @Override
    public void execute(TextChannel c, List<String> args) {
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        musicManager.scheduler.nextTrack();

        c.sendMessage("Skipped to next track.").queue();
    }
}
