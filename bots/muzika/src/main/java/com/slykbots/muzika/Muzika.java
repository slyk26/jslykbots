package com.slykbots.muzika;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.components.commands.Ping;
import com.slykbots.components.commands.SlashCommand;
import com.slykbots.components.db.DB;
import com.slykbots.components.listeners.MessageListener;
import com.slykbots.components.listeners.ReadyListener;
import com.slykbots.components.listeners.SCIListener;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.muzika.lavastuff.GuildMusicManager;
import com.slykbots.muzika.legacycommands.*;
import com.slykbots.muzika.slashcommands.SetMusicChannel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Muzika {

    public static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    protected static final Map<Long, GuildMusicManager> musicManagers =  new HashMap<>();
    private static final List<SlashCommand> c = List.of(
            new Ping(),
            new SetMusicChannel()
    );
    private static final List<LegacyCommand> l = List.of(
            new Yt(),
            new Sc(),
            new PrintQueue(),
            new Skip(),
            new FastForward()
    );

    public static void main(String[] args) {
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
        Logger logger = LoggerFactory.getLogger(Muzika.class);
        DB.healthcheck();

        JDA jda = JDABuilder.createDefault(EnvLoader.getVar("MUZIKA_KEY"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new ReadyListener(e -> logger.info("Started as {}!", e.getJDA().getSelfUser().getName())))
                .addEventListeners(new SCIListener(e -> c.forEach(cmd -> cmd.onSlashCommandInteraction(e))))
                .addEventListeners(new MessageListener(e -> l.forEach(cmd -> cmd.handleLegacyCommand(e))))
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setActivity(Activity.listening("gachimuchi")).build();

        jda.updateCommands().addCommands(c.stream().map(SlashCommand::getData).toList()).queue();
    }

    @SuppressWarnings("java:S3824")
    public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
}