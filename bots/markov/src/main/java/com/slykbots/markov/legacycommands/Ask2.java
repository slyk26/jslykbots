package com.slykbots.markov.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.components.util.EnvLoader;
import com.slykbots.markov.Markov;
import com.theokanning.openai.completion.CompletionRequest;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Ask2 extends LegacyCommand {
    private static final Logger logger = LoggerFactory.getLogger(Ask2.class);

    public Ask2() {
        super("ask2", "Ask Chatgpt a Question (uncensored)", 1, 30000);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        var arg = args.getFirst();
        var p = EnvLoader.getVar("OPENAI_PRE_PROMPT");
        logger.debug("{}", arg);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(p.replace("%s", e.getAuthor().getEffectiveName()) + arg)
                .maxTokens(1000)
                .model("gpt-3.5-turbo-instruct")
                .build();
        var choices = Markov.os.createCompletion(completionRequest).getChoices();
        choices.forEach(c -> logger.debug("{}", c));
        e.getMessage().reply(choices.getFirst().getText()).queue();
    }
}
