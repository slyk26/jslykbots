package com.slykbots.muzika;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.slykbots.components.commands.Ping;
import com.slykbots.components.commands.SlashCommand;
import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.listeners.SCIListener;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import com.slykbots.muzika.lavastuff.ScHttpAudioTrack;
import lombok.Data;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Muzika extends ListenerAdapter {

    private static final List<SlashCommand> c = List.of(
            new Ping()
    );

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private Muzika() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Muzika.class);

        DB.healthcheck();

        JDA jda = JDABuilder.createDefault(EnvLoader.getVar("MUZIKA_KEY"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())))
                .addEventListeners(new Muzika())
                .addEventListeners(new SCIListener(e -> c.forEach(cmd -> cmd.onSlashCommandInteraction(e))))
                .disableCache(CacheFlag.MEMBER_OVERRIDES)
                .setActivity(Activity.listening("gachimuchi")).build();

        jda.updateCommands().addCommands(c.stream().map(SlashCommand::getData).toList()).queue();
    }

    @SuppressWarnings("java:S1751")
    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }

    @SneakyThrows
    private static String searchYt(String query) {
        query = query.replace("\"", "'");

        Process p = new ProcessBuilder().command("./yt-dlp_linux", "ytsearch:\"" + query + "\"", "--no-download", "--print", "original_url").start();
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        return br.lines().toList().getLast();
    }

    @SneakyThrows
    private static ScResult searchSc(String query) {
        query = query.replace("\"", "'");

        Process p = new ProcessBuilder().command("./yt-dlp_linux", "scsearch:\"" + query + "\"", "--no-download", "--get-url", "--get-title").start();
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        var lines = br.lines().toList();
        ScResult s = new ScResult();

        s.title = lines.getFirst();
        s.url = lines.getLast();

        return s;
    }

    private static boolean isUrl(String s) {
        try {
            new URI(s);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        var d = event.getChannel().asTextChannel();
        String[] command = event.getMessage().getContentRaw().split(" ", 2);

        if ("~sc".equals(command[0]) && command.length == 2) {
            var a = searchSc(command[1]);
            loadAndPlay(d, a.getUrl(), a.getTitle());
        } else if ("~yt".equals(command[0]) && command.length == 2) {
            if (isUrl(command[1]))
                loadAndPlay(d, command[1], null);
            else
                loadAndPlay(d, searchYt(command[1]), null);
        } else if ("~skip".equals(command[0])) {
            skipTrack(d);
        } else if ("~list".equals(command[0])) {
            printQueue(d);
        } else if("~ff".equals(command[0])) {
            fastForward(d);
        }

        super.onMessageReceived(event);
    }

    private void fastForward(final TextChannel c){
        GuildMusicManager musicManager = getGuildAudioPlayer(c.getGuild());
        List<AudioTrack> pl = musicManager.scheduler.getPlaylist();

        while(pl.size() > 1 ){
            musicManager.scheduler.nextTrack();
            pl.removeFirst();
        }

        c.sendMessage(">> fast forward to newest Song >> ").queue();
    }

    private void loadAndPlay(final TextChannel c, final String trackUrl, String fallbackTitle) {
        GuildMusicManager musicManager = getGuildAudioPlayer(c.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                boolean isHls = false;
                boolean isPreview = false;
                if (fallbackTitle != null) {
                    var b = (HttpAudioTrack) track;
                    track = new ScHttpAudioTrack(track.getInfo(), track.getSourceManager(), b.getContainerTrackFactory(), fallbackTitle);
                    isHls = track.getInfo().uri.contains("cf-hls-media");
                    isPreview = track.getInfo().uri.contains("cf-preview-media");
                }
                if (isHls)
                    c.sendMessage("Song found but has invalid Encoding serverside").queue();
                if (isPreview)
                    c.sendMessage("Song found but is GO+ (premium)").queue();
                if (!isHls && !isPreview) {
                    c.sendMessage(("Adding to queue " + track.getInfo().title)).queue();
                    play(c.getGuild(), musicManager, track);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().getFirst();
                }

                c.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(c.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                c.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                c.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel c) {
        GuildMusicManager musicManager = getGuildAudioPlayer(c.getGuild());
        musicManager.scheduler.nextTrack();

        c.sendMessage("Skipped to next track.").queue();
    }

    private void printQueue(TextChannel c) {
        GuildMusicManager musicManager = getGuildAudioPlayer(c.getGuild());

        var a = musicManager.scheduler.getPlaylist().stream().filter(Objects::nonNull).toList();
        StringBuilder sb = new StringBuilder();

        if (a.isEmpty()) {
            c.sendMessage("Queue is empty. Add music with ~yt or ~sc.").queue();
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

    @Data
    private static class ScResult {
        private String title;
        private String url;
    }
}