package com.slykbots.components.commands;

import com.slykbots.components.settings.SettingService;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public class Toggle extends GuildOnlySlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(Toggle.class);
    private final Map<String, String> s;
    private final SettingService ss;


    public Toggle(Map<String, String> settings) {
        super("toggle", "Toggles a Setting");
        this.s = settings;
        this.ss = new SettingService();
    }

    @Override
    public SlashCommandData getData() {
        var d = super.getData();
        List<OptionData> o = new ArrayList<>();

        OptionData setting = new OptionData(OptionType.STRING, "setting", "which Setting do you want to change?").setRequired(true);
        this.s.forEach((key, value) -> setting.addChoice(value, key));
        o.add(setting);

        o.add(new OptionData(OptionType.BOOLEAN, "enabled", "Enable or Disable this Setting").setRequired(true));
        d.addOptions(o);
        return d;
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_SERVER))
            event.reply("you cannot do that").setEphemeral(true).queue();

        List<OptionMapping> om = event.getOptions();

        OptionMapping setting = om.stream().filter(f -> f.getName().equals("setting")).toList().getFirst();
        OptionMapping enabled = om.stream().filter(f -> f.getName().equals("enabled")).toList().getFirst();

        logger.debug("{} {}", setting, enabled);

        this.ss.setSetting(Objects.requireNonNull(event.getGuild()).getId(), setting.getAsString(), enabled.getAsString());

        event.reply("updated successfully!").setEphemeral(true).queue();
    }
}
