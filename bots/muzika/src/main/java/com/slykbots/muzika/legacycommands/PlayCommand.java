package com.slykbots.muzika.legacycommands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.components.settings.SettingService;
import com.slykbots.muzika.Muzika;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import com.slykbots.muzika.lavastuff.ScHttpAudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class PlayCommand extends LegacyCommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayCommand.class);
    private final SettingService ss = new SettingService();

    protected PlayCommand(String name, int parameterLength) {
        super(name, parameterLength);
    }

    protected boolean isUrl(String s) {
        try {
            new URI(s);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    protected void loadAndPlay(final GuildMessageChannel c, final String trackUrl, String fallbackTitle) {
        GuildMusicManager musicManager = Muzika.getGuildAudioPlayer(c.getGuild());

        Muzika.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                // Soundcloud workaround
                if (fallbackTitle != null) {
                    var b = (HttpAudioTrack) track;
                    track = new ScHttpAudioTrack(track.getInfo(), track.getSourceManager(), b.getContainerTrackFactory(), fallbackTitle);
                }

                c.sendMessage(("Adding to queue: " + track.getInfo().title)).queue();
                play(c.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().getFirst();
                }

                c.sendMessage("Adding to queue: " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(c.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                c.sendMessage("Nothing found by: " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.error(exception.getMessage());
                c.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToMusicChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    private void connectToMusicChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            var g = audioManager.getGuild().getId();
            var a = this.ss.getSetting(g, "muzika.voiceChannel");

            if(a == null) {
                logger.warn("muzika.voiceChannel is not set");
            }

            var channels = audioManager.getGuild().getChannels().stream().filter(c -> c.getId().equals(a)).toList();

            if(channels.isEmpty()) {
                logger.error("VC does not exist anymore");
                return;
            }
            audioManager.openAudioConnection((AudioChannel) channels.getFirst());
        }
    }
}
