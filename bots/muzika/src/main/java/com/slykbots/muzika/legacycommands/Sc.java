package com.slykbots.muzika.legacycommands;

import com.slykbots.muzika.Muzika;
import lombok.Data;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Sc extends PlayCommand {
    public Sc() {
        super("sc", "`" + getLegacyKey() + "sc query`\nsearches Soundcloud for a Song and plays it!", 1);
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

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        if (Muzika.vcCheck(e)) return;
        var arg = args.getFirst();
        var scTrack = searchSc(arg);
        var c = e.getChannel().asGuildMessageChannel();

        if (scTrack.getUrl().contains("cf-hls-media")) {
            c.sendMessage("Song found but has invalid Encoding serverside").queue();
        } else if (scTrack.getUrl().contains("cf-preview-media")) {
            c.sendMessage("Song found but is GO+ (premium)").queue();
        } else {
            loadAndPlay(c, scTrack.getUrl(), scTrack.getTitle());
        }
    }

    @Data
    private static class ScResult {
        private String title;
        private String url;
    }
}
