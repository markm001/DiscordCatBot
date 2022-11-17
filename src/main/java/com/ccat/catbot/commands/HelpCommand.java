package com.ccat.catbot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

public class HelpCommand implements ServerCommand {
    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        message.delete().queue();

        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder subBuilder = new StringBuilder();
        stringBuilder.append("`!ping` - receive a pong from the bot. \n");
        stringBuilder.append("`!purge [amount]` - delete a specific amount of messages. \n");
        stringBuilder.append("`!createrole [name] [#color]` - create a role with a hexadecimal color. \n");
        stringBuilder.append("`!reactrole [channel-Id] [message-Id] [role-Id] [emote]` - to allow users to receive roles via reactions on a specific message. \n");
        stringBuilder.append("`!permit [@user] [permission code]` - grant a member without elevated permissions access to certain bot-commands. \n");
        stringBuilder.append("`!specify [channel-Id] [specifier]` - specify channels the bot will listen on (e.g. MUSIC,VOICE,SYSTEM). \n");
        stringBuilder.append("`!createEvent [\"topic\"] [startTimeUTC] [duration]` - creates an Event with Event-Id description. (Start-time in UTC and duration are optional.) \n");
        stringBuilder.append("`!schedule [event-Id]` - allows Users to input their desired times for a specific event. \n");

        stringBuilder.append("`!roll [sides] [amount]` - roll an amount of dice with desired sides.");
        subBuilder.append(" - roll to generate 6 ability scores. \n");
        subBuilder.append("★ default: 4d6 - drop the lowest roll. \n");
        subBuilder.append("★ `4reroll` - roll 4d6, drop the lowest roll and reroll 1s. \n");
        subBuilder.append("★ `3times` - roll 3d6, use the highest roll. \n");
        subBuilder.append("★ `3strict` - roll 3d6. \n");
        subBuilder.append("★ `plus6` - roll 2d6, add 6 to each result. \n");

        member.getUser().openPrivateChannel().queue(privateChannel -> {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle(Emoji.fromUnicode("U+2699").getAsReactionCode() +
                    " | List of all available Commands for this bot:");
            builder.setDescription(stringBuilder.toString());
            builder.addField("", "`!roll stats [ruling]`",true);
            builder.addField("\n", subBuilder.toString(), true);
            builder.setColor(Color.decode("#f7c315"));

            privateChannel.sendMessageEmbeds(builder.build()).queue();
        });
    }
}
