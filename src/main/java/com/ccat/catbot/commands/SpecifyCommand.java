package com.ccat.catbot.commands;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.entities.ChannelTypeSpecifier;
import com.ccat.catbot.model.entities.ServerChannel;
import com.ccat.catbot.model.services.MessageService;
import com.ccat.catbot.model.services.ServerChannelService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;

import java.awt.*;
import java.util.Optional;
import java.util.function.Consumer;

public class SpecifyCommand implements ServerCommand{

    private final MessageService messageService;
    private final ServerChannelService serverChannelService;

    public SpecifyCommand(MessageService messageService, ServerChannelService serverChannelService) {
        this.messageService = messageService;
        this.serverChannelService = serverChannelService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        //!specify [channel-Id] [specifier]
        String[] args = message.getContentDisplay().split(" ");

        if(member.hasPermission(Permission.MANAGE_SERVER)) {
            if(args.length == 3) {
                try {
                    long guildId = textChannel.getGuild().getIdLong();
                    long channelId = Long.parseLong(args[1]);
                    ChannelTypeSpecifier typeSpecifier = ChannelTypeSpecifier.valueOf(args[2].toUpperCase());

                    ServerChannel serverChannelRequest = new ServerChannel(
                            guildId,
                            channelId,
                            typeSpecifier);

                    Optional<ServerChannel> channelResponse = serverChannelService
                            .getServerChannelFromGuildAndChannel(serverChannelRequest);

                    if(channelResponse.isPresent()) {
                        //TODO: .submit -> wait for User confirmation & Overrride existing Entry
                        textChannel.sendMessage("Entry already exists.").queue();
                        return;
                    }

                    ServerChannel serverChannelResponse = serverChannelService
                            .setServerChannel(serverChannelRequest);
                    textChannel.sendMessage("Saved requested channel to: " + typeSpecifier).queue();

                } catch (IllegalArgumentException e) { //IllegalArgumentException or NumberFormatException
                    sendError(member, textChannel);
                }
            } else {
                sendError(member,textChannel);
            }
        } else {
            messageService.sendAccessDenied(member, textChannel, "you lack the permission to manage the bot.");
        }
    }

    private void sendError(Member member, TextChannel textChannel) {
        messageService.sendMessageEmbed(member, textChannel,
                "⚠ | Invalid Argument Error.",
                "The `!specify` command, requires a channel-Id and valid specifier. `!specify [channel-Id] [specifier]`.",
                Color.decode("#f7c315")
        );
    }
}
