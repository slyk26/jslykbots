package com.slykbots.markov.chains;

import lombok.Data;

@Data
public class MarkovToken {
    private int id;
    private String guildId;
    private String currentWord;
    private String nextWord;
    private int frequency;
}
