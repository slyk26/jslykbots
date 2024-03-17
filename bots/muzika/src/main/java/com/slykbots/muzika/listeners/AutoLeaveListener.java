package com.slykbots.muzika.listeners;

import com.slykbots.components.listeners.VoiceChannelListener;
import com.slykbots.components.util.Helper;
import com.slykbots.muzika.Muzika;

public class AutoLeaveListener extends VoiceChannelListener {

    public AutoLeaveListener() {
        super(c -> {
            var guildId = c.getGuild().getId();
            var members = Helper.getMembersOfVoiceChannel(c.getGuild(), Muzika.ss.getSetting(guildId, Muzika.MUZIKA_VC_KEY));

            if(members.size() == 1 && c.getJDA().getSelfUser().getId().equals(members.getFirst().getId())) {
                Muzika.getGuildAudioPlayer(c.getGuild()).player.destroy();
                c.getGuild().getAudioManager().closeAudioConnection();
            }
        });
    }
}
