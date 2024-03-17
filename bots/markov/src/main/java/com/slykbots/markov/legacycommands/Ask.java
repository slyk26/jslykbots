package com.slykbots.markov.legacycommands;

import com.slykbots.components.commands.LegacyCommand;
import com.slykbots.markov.Markov;
import com.theokanning.openai.completion.CompletionRequest;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Ask extends LegacyCommand {
    private static final Logger logger = LoggerFactory.getLogger(Ask.class);

    public Ask() {
        super("ask", "Ask Chatgpt a question", 1);
    }

    @Override
    public void execute(MessageReceivedEvent e, List<String> args) {
        var arg = args.getFirst();
        logger.debug("{}", arg);
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt("Generate a Response with less or equal than 2000 characters. Be sure to count the characters and whitespaces and rewrite the Message to be less or equal than 2000 Characters: " + arg)
                .maxTokens(1000)
                .model("gpt-3.5-turbo-instruct")
                .build();
        var choices = Markov.os.createCompletion(completionRequest).getChoices();
        choices.forEach(c -> logger.debug("{}", c));
        e.getMessage().reply(choices.getFirst().getText()).queue();
    }
}
