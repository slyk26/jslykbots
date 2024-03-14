package com.slykbots.components.commands;


import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class SlashCommand extends ListenerAdapter {
    private String name;
    private String description;

    protected SlashCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public SlashCommandData getData() {
        return Commands.slash(name, description);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(this.getName())) return;
        this.execute(event);
    }

    public abstract void execute(SlashCommandInteraction event);
}
