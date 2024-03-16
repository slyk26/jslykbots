package com.slykbots.components.util;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Helper {

    private Helper() {
    }

    /**
     * @param m the map with the keys to be checked
     * @param a the array with the elements
     * @return true if map "m" has all elements of "a" as keys
     */
    public static <T> boolean mapHasKeys(Map<T, ?> m, List<T> a) {
        for (T key : a) {
            if (!m.containsKey(key))
                return false;
        }
        return true;
    }

    /**
     * @param msg a Message from a Message event
     * @return whether the bot is mentioned or not
     */
    public static boolean isBotMentioned(Message msg) {
        var m = msg.getMentions();
        return m.isMentioned(msg.getJDA().getSelfUser(), Message.MentionType.USER);
    }

    public static List<Member> getMembersOfVoiceChannel(Guild g, String channel) {
        return Objects.requireNonNull(Objects.requireNonNull(g.getVoiceChannelById(channel)).getMembers());
    }

    public static boolean isInChannel(Member m, Guild g, String channel) {
        var members = Helper.getMembersOfVoiceChannel(g, channel);
        return members.stream().anyMatch(x -> x.getId().equals(m.getId()));
    }

}
