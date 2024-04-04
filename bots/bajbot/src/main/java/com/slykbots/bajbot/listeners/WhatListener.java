package com.slykbots.bajbot.listeners;

import com.slykbots.components.listeners.MessageListener;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.Arrays;

public class WhatListener extends MessageListener {
    public WhatListener() {
        super(c -> {
            var msg = c.getMessage();
            var txt = msg.getContentDisplay().split(" ");

            if (Arrays.stream(txt).map(String::toLowerCase).anyMatch(t ->
                    ("que".equals(t) ||
                            (t.contains("what") && txt.length < 3))))
                msg.addReaction(Emoji.fromCustom("chickenbutt", 1204210992377106502L, false)).complete();

        });
    }
}
