package com.slykbots.bajbot.legacycommands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slykbots.bajbot.listeners.QuizListener;
import com.slykbots.components.commands.LegacyCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Quiz extends LegacyCommand {

    private static final Logger logger = LoggerFactory.getLogger(Quiz.class);

    public Quiz() {
        super("quiz", "create a quiz (slyk only)", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        if (!"1082034134647574618".equals(e.getAuthor().getId())) return;

        EmbedBuilder eb = new EmbedBuilder();
        var q = getQuestion();
        assert q != null;

        var options = new ArrayList<Button>();
        options.add(Button.secondary(d(q.c) + "-correct", d(q.c)));
        q.w.forEach(o -> options.add(Button.secondary(d(o), d(o))));
        Collections.shuffle(options);

        eb.setTitle(String.format("%s (%s)", d(q.category), d(q.difficulty)));
        eb.setDescription(d(q.q));
        e.getChannel().sendMessageEmbeds(eb.build()).addActionRow(options).queue(s -> {
            e.getChannel().sendMessage("TIME IS UP!").queueAfter(10, TimeUnit.SECONDS, d -> {
                s.editMessageComponents(ActionRow.of(s.getActionRows().getFirst().getComponents().stream().map(c -> (Button) c).map(b -> {
                    if (Objects.requireNonNull(b.getId()).contains("-correct"))
                        return Button.success(b.getId(), b.getLabel());
                    return Button.danger(b.getId(), b.getLabel());
                }).toList()).asDisabled()).queue();
                e.getChannel().sendMessage("Correct are: " + QuizListener.mapWinners()).queue();

            });
        });
    }

    private String d(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private Response.Question getQuestion() {
        String urlString = "https://opentdb.com/api.php?amount=1&encode=url3986";
        try {
            ObjectMapper o = new ObjectMapper();
            return o.readValue(new URI(urlString).toURL(), Response.class).results.getFirst();

        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    private static class Response {
        @JsonProperty("response_code")
        private int code;
        private List<Question> results;

        @Data
        @NoArgsConstructor
        private static class Question {
            private String type;
            private String difficulty;
            private String category;
            @JsonProperty("question")
            private String q;
            @JsonProperty("incorrect_answers")
            private List<String> w;
            @JsonProperty("correct_answer")
            private String c;
        }
    }
}
