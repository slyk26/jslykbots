package com.slykbots.muzika.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Skip extends LegacyCommand {
    public Skip() {
        super("skip", "skip the current Song", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        if (Muzika.vcCheck(e)) return;
        var c = e.getChannel().asGuildMessageChannel();
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());
        musicManager.scheduler.nextTrack();

        c.sendMessage("Skipped to next track.").queue();
    }
}
