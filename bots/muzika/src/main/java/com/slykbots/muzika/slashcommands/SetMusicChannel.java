package com.slykbots.muzika.slashcommands;

import com.slykbots.components.commands.GuildOnlySlashCommand;
import com.slykbots.components.settings.SettingService;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.internal.entities.channel.concrete.VoiceChannelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
public class SetMusicChannel extends GuildOnlySlashCommand {

    private static final Logger logger = LoggerFactory.getLogger(SetMusicChannel.class);
    private final SettingService ss;

    public SetMusicChannel() {
        super("set", "choose which Vc the Bot will play in");

        this.ss = new SettingService();
    }

    @Override
    public SlashCommandData getData() {
        var d = super.getData();
        d.addOptions(new OptionData(OptionType.CHANNEL, "vc", "the bot will join to this vc").setRequired(true));
        return d;
    }

    @Override
    public void execute(SlashCommandInteraction event) {
        if(!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply("you cannot do that").setEphemeral(true).queue();
            return;
        }

        List<OptionMapping> om = event.getOptions();
        OptionMapping vc = om.stream().filter(f -> f.getName().equals("vc")).toList().getFirst();
        var c = vc.getAsChannel();

        if(!(c instanceof VoiceChannelImpl)) {
            event.reply("This is not a VoiceChannel").setEphemeral(true).queue();
            return;
        }

        var d = c.asVoiceChannel();

        if(!d.canTalk()){
            event.reply("I cannot play in that Channel").queue();
            return;
        }

        logger.debug("channel: {}", c);
        this.ss.setSetting(this.getGuildId(event), "muzika.voiceChannel", vc.getAsString());
        event.reply("Voice Channel is set to: <#" + vc.getAsString() + ">").queue();
    }
}
