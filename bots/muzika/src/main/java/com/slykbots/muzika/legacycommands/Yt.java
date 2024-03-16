package com.slykbots.muzika.legacycommands;

import lombok.SneakyThrows;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Yt extends PlayCommand{
    public Yt() {
        super("yt", 1);
    }

    @SneakyThrows
    private static String searchYt(String query) {
        query = query.replace("\"", "'");

        Process p = new ProcessBuilder().command("./yt-dlp_linux", "ytsearch:\"" + query + "\"", "--no-download", "--print", "original_url").start();
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        return br.lines().toList().getLast();
    }

    @Override
    public void execute(TextChannel c, List<String> args) {
        var arg = args.getFirst();

        if(!isUrl(arg)) arg = searchYt(arg);

        loadAndPlay(c, arg, null);
    }
}
