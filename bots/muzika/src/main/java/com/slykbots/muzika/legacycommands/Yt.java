package com.slykbots.muzika.legacycommands;

import com.slykbots.muzika.Muzika;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Yt extends PlayCommand {
    public Yt() {
        super("yt", "`" + getLegacyKey() + "yt (query or yt link)`\nsearches Youtube for a Song and plays it!", 1);
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
    public void execute(MessageReceivedEvent e, List<String> args) {
        if(Muzika.vcCheck(e)) return;
        var arg = args.getFirst();

        if (!isUrl(arg)) arg = searchYt(arg);

        loadAndPlay(e.getChannel().asGuildMessageChannel(), arg, null, null);
    }
}
