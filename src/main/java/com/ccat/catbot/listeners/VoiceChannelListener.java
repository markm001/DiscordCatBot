package com.ccat.catbot.listeners;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VoiceChannelListener extends ListenerAdapter {
    List<Long> tempVoiceChannel;

    public VoiceChannelListener() {
        this.tempVoiceChannel = new ArrayList<>();
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        //Set Voice-Channel Hub via Database later!
        if(event.getChannelJoined() != null) {
            VoiceChannel joinChannel;

            if ((joinChannel = event.getChannelJoined().asVoiceChannel()).getIdLong() == 1034456110377214074L) {
                Category parentCategory = joinChannel.getParentCategory();
                Member member = event.getMember();

                if (parentCategory != null) {
                    VoiceChannel tempChannel = parentCategory.createVoiceChannel("\uD83C\uDF99" + member.getEffectiveName()).complete();
                    tempChannel.getManager().setUserLimit(joinChannel.getUserLimit()).queue();

                    tempChannel.getGuild().moveVoiceMember(member, tempChannel).queue();

                    tempVoiceChannel.add(tempChannel.getIdLong());
                }
            }
        }

        if(event.getChannelLeft() != null) {
            AudioChannelUnion leftChannel = event.getChannelLeft();
            if(leftChannel.getMembers().isEmpty()) {
                if(tempVoiceChannel.contains(leftChannel.getIdLong())) {
                    tempVoiceChannel.remove(leftChannel.getIdLong());
                    leftChannel.delete().queue();
                }
            }
        }
    }
}
