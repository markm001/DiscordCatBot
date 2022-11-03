package com.ccat.catbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Component
public class MemberJoinListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        String welcomeMessage = member.getAsMention() + " joined the guild. \n\n" +
                "Welcome! Please make sure to read the rules and claim your preferred roles! \n";

        EmbedBuilder builder = new EmbedBuilder();
        builder.setDescription(welcomeMessage);
        builder.setImage(avatarUrl);

        builder.setColor(Color.decode("#15c0f7"));
        builder.setTimestamp(OffsetDateTime.now());

        Objects.requireNonNull(event.getGuild().getDefaultChannel())
                .asTextChannel().sendMessageEmbeds(builder.build()).queue();
    }
}
