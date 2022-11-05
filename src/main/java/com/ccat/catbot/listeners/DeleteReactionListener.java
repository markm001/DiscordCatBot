package com.ccat.catbot.listeners;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class DeleteReactionListener extends ListenerAdapter {
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        long messageIdLong = event.getMessageIdLong();

        event.getChannel().retrieveMessageById(messageIdLong).queue(message -> {

            if(message.getAuthor().isBot()) {
                Optional<MessageEmbed> msgEmbed = message.getEmbeds().stream().filter(embed -> {
                    MessageEmbed.AuthorInfo author;
                    if ((author = embed.getAuthor()) != null) {
                        return author.getName().equals(Objects.requireNonNull(event.getUser()).getName());
                    }
                    return false;
                }).findFirst();

                if(msgEmbed.isPresent()) {
                    EmojiUnion emoji = Emoji.fromFormatted("✅");
                    MessageReaction reaction;
                    if((reaction = message.getReaction(emoji)) != null) {
                        message.delete().queue();
                    }
                }
            }
        });
    }
}
