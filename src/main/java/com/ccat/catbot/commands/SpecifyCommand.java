package com.ccat.catbot.commands;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.entities.ChannelTypeSpecifier;
import com.ccat.catbot.model.entities.ServerChannel;
import com.ccat.catbot.model.services.MessageService;
import com.ccat.catbot.model.services.implementations.ServerChannelService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
                        ServerChannel response = channelResponse.get();
                        textChannel.sendMessage("Entry already exists. Do you want to overwrite the entry?")
                                .queue(msg -> {
                                    msg.addReaction(Emoji.fromFormatted("✅")).queue();

                                    JdaConfiguration.INSTANCE.getEventWaiter().waitForEvent(MessageReactionAddEvent.class,
                                            reaction -> (reaction.getMessageIdLong() == msg.getIdLong())
                                                    && (Objects.requireNonNull(reaction.getUser()).getIdLong() == member.getIdLong()),
                                            a -> { saveRequestedChannel(textChannel,
                                                    new ServerChannel(
                                                            response.getId(),
                                                            response.getGuildId(),
                                                            response.getChannelId(),
                                                            typeSpecifier));
                                                    msg.delete().queueAfter(20, TimeUnit.SECONDS);
                                            },90,TimeUnit.SECONDS,
                                            () -> msg.delete().queue()); //if reaction time exceeded -> delete.
                                });
                        return;
                    }
                    saveRequestedChannel(textChannel, serverChannelRequest);

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

    private void saveRequestedChannel(TextChannel textChannel, ServerChannel request) {
        ServerChannel response = serverChannelService
                .setServerChannel(request);
        String channelAsMention = Objects.requireNonNull(textChannel.getGuild().getGuildChannelById(response.getChannelId())).getAsMention();
        textChannel.sendMessage("Saved requested channel:" + channelAsMention + " to: " + response.getSpecifier())
                .complete().delete().queueAfter(10, TimeUnit.SECONDS);
    }

    private void sendError(Member member, TextChannel textChannel) {
        messageService.sendMessageEmbed(member, textChannel,
                "⚠ | Invalid Argument Error.",
                "The `!specify` command, requires a channel-Id and valid specifier. `!specify [channel-Id] [specifier]`.",
                Color.decode("#f7c315")
        );
    }
}
