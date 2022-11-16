package com.ccat.catbot.listeners;

import com.ccat.catbot.model.entities.ChannelTypeSpecifier;
import com.ccat.catbot.model.entities.ServerChannel;
import com.ccat.catbot.model.services.implementations.ServerChannelService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VoiceChannelListener extends ListenerAdapter {
    List<Long> tempVoiceChannel;
    private final ServerChannelService channelService;

    public VoiceChannelListener(ServerChannelService channelService) {
        this.tempVoiceChannel = new ArrayList<>();
        this.channelService = channelService;
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        //Set Voice-Channel Hub via Database later!
        if(event.getChannelJoined() != null) {
            VoiceChannel joinChannel = event.getChannelJoined().asVoiceChannel();

            ServerChannel channelRequest = new ServerChannel(
                    event.getGuild().getIdLong(),
                    joinChannel.getIdLong(),
                    ChannelTypeSpecifier.VOICE);

            if(channelService.checkServerChannelForSpecifier(channelRequest)) {

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
