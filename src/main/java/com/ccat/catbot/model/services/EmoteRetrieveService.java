package com.ccat.catbot.model.services;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmoteRetrieveService {
    public String findServerEmote(Guild guild, String emote) {
        String emoteName = emote.replaceAll(":", "");

        List<RichCustomEmoji> richCustomEmojis = guild.retrieveEmojis().complete();

        Optional<RichCustomEmoji> foundEmote =
                richCustomEmojis.stream()
                        .filter(emoji -> emoji.getName().equalsIgnoreCase(emoteName))
                        .findFirst();

        return foundEmote.map(Emoji::getAsReactionCode).orElse(null);
    }
}
