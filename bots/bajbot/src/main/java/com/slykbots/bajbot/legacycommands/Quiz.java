package com.slykbots.bajbot.legacycommands;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slykbots.bajbot.listeners.QuizListener;
import com.slykbots.components.commands.LegacyCommand;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.slykbots.components.util.Helper.doTimes;

public class Quiz extends LegacyCommand {

    private static final Logger logger = LoggerFactory.getLogger(Quiz.class);

    public Quiz() {
        super("quiz", "create a quiz (slyk only)", 0);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        if (Objects.requireNonNull(e.getMember()).getRoles().stream().filter(f -> "1222260841215164527".equals(f.getId())).findFirst().isEmpty()) return;

        int thinkTime = 11;
        AtomicBoolean active = new AtomicBoolean(false);
        AtomicInteger time = new AtomicInteger(thinkTime);
        var q = getQuestion();
        if (q == null) return;

        QuizListener.reset();

        e.getChannel().sendMessageEmbeds(createEmbed(q)).addActionRow(createButtons(q, thinkTime)).queue(s -> {
            if (!active.get()) {
                doTimes(() -> {
                    if (time.get() == 0) {
                        disableButtons(s);
                        e.getChannel().sendMessage("Participants: " + QuizListener.mapSubmitters() + "\nCorrect: " + QuizListener.mapWinners()).queue();
                        return;
                    }
                    countDown(s, time);
                }, thinkTime, thinkTime);
                active.set(true);
            }
        });
    }

    private void countDown(Message s, AtomicInteger time) {
        s.editMessageComponents(ActionRow.of(s.getActionRows().getFirst().getComponents().stream().map(c -> (Button) c).map(f -> {
            if ("TIMER".equals(f.getId())) {
                return f.withLabel(time.getAndDecrement() + "");
            }
            return f;
        }).toList())).queue();
    }

    private void disableButtons(Message s){
        s.editMessageComponents(ActionRow.of(s.getActionRows().getFirst().getComponents().stream().map(c -> (Button) c).map(b -> {
            if (Objects.requireNonNull(b.getId()).contains("-correct"))
                return Button.success(b.getId(), b.getLabel());
            return Button.danger(b.getId(), b.getLabel());
        }).skip(1).toList()).asDisabled()).queue();
    }

    private List<Button> createButtons(Response.Question q, int time) {
        var options = new ArrayList<Button>();
        options.add(Button.secondary(d(q.c) + "-correct", d(q.c)));
        q.w.forEach(o -> options.add(Button.secondary(d(o), d(o))));
        Collections.shuffle(options);
        var timer = Button.of(ButtonStyle.PRIMARY, "TIMER", time + "");
        options.addFirst(timer.asDisabled());
        return options;
    }

    private MessageEmbed createEmbed(Response.Question q) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(String.format("%s (%s)", d(q.category), d(q.difficulty)));
        eb.setDescription(d(q.q));
        return eb.build();
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
