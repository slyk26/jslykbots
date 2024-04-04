package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.TypedListener;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class QuizListener extends TypedListener<ButtonInteractionEvent> {
    protected static ConcurrentMap<Long, String> contestants = new ConcurrentHashMap<>();

    public QuizListener() {
        super(c -> {
            var submitter = c.getMember();
            var button = c.getButton();

            contestants.put(Objects.requireNonNull(submitter).getIdLong(), Objects.requireNonNull(button.getId()));
            c.deferEdit().queue();
        }, ButtonInteractionEvent.class);
    }

    public static String mapWinners() {
        StringBuilder sb = new StringBuilder();

        var correct = contestants.entrySet().stream().filter(f -> f.getValue().contains("-correct")).toList();

        if (correct.isEmpty()) {
            sb.append("None! :(");
        } else {
            correct.forEach(e -> sb.append(makePing(e.getKey())));
        }
        return sb.toString();
    }

    public static String mapSubmitters() {
        StringBuilder sb = new StringBuilder();
        contestants.keySet().forEach(k -> sb.append(makePing(k)));
        return sb.toString();
    }

    private static String makePing(Long id) {
        return "<@" + id + "> ";
    }

    public static void reset() {
        contestants = new ConcurrentHashMap<>();
    }
}
