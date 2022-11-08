package com.ccat.catbot.commands;

import com.ccat.catbot.JdaConfiguration;
import com.ccat.catbot.model.entities.GuildMember;
import com.ccat.catbot.model.entities.ReactRole;
import com.ccat.catbot.model.services.EmoteRetrieveService;
import com.ccat.catbot.model.services.MemberPermissionService;
import com.ccat.catbot.model.services.MessageService;
import com.ccat.catbot.model.services.ReactRoleService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReactRoleCommand implements ServerCommand {
    private final MessageService messageService;
    private final ReactRoleService reactRoleService;

    private final EmoteRetrieveService emoteService;

    private final MemberPermissionService permissionService;

    public ReactRoleCommand(MessageService messageService,
                            ReactRoleService reactRoleService,
                            EmoteRetrieveService emoteService,
                            MemberPermissionService permissionService) {
        this.messageService = messageService;
        this.reactRoleService = reactRoleService;
        this.emoteService = emoteService;
        this.permissionService = permissionService;
    }


    //TODO: WRITE OWN EXCEPTION HANDLER FOR BELOW!!

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        // !reactrole [channelId] [messageId] [roleId] [emote]
        String[] args = message.getContentDisplay().split(" ");

        GuildMember guildMemberRequest = new GuildMember(
                member.getIdLong(),
                textChannel.getGuild().getIdLong(),
                Permission.MANAGE_ROLES.getRawValue()
        );

        //TODO: WRITE A MORE ELEGANT EXCEPTION HANDLER SERVICE.

        boolean hasPermission = false;
        if((member.hasPermission(Permission.MANAGE_ROLES)) ||
                permissionService.checkMemberPermissions(guildMemberRequest)) {
            hasPermission = true;
        }

        if (hasPermission) {
            if (args.length == 5) {
                try {
                    long channelId = Long.parseLong(args[1]);
                    long messageId = Long.parseLong(args[2]);
                    long roleId = Long.parseLong(args[3]);
                    String emote = args[4];

                    //Resolve Emote :emote: OR Unicode-emoji:
                    if (emote.startsWith(":")) {
                        emote = emoteService.findServerEmote(textChannel.getGuild(), emote);

                        if(emote.isEmpty()) {
                            sendError(member, textChannel, "⚠ | Error, no such Emote!", "The requested Emote was not found.");
                            return;
                        }
                    }

                    //Check IDs-validity:
                    ShardManager shardManager = JdaConfiguration.INSTANCE.getShardManager();
                    TextChannel targetChannel;
                    Role targetRole;
                    if ((targetChannel = shardManager.getTextChannelById(channelId)) == null) {
                        sendError(member, textChannel, "⚠ | Invalid Channel!.", "The requested channel-Id doesn't exist: " + channelId);
                        return;
                    }
                    if ((targetRole = shardManager.getRoleById(roleId)) == null) {
                        sendError(member, textChannel, "⚠ | Invalid Role!.", "The requested role-Id doesn't exist: " + roleId);
                        return;
                    }

                    String finalEmote = emote;
                    targetChannel.retrieveMessageById(messageId).queue(targetMessage -> {
                        ReactRole reactRoleRequest = new ReactRole(
                                UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE,
                                textChannel.getGuild().getIdLong(),
                                channelId,
                                messageId,
                                roleId,
                                finalEmote
                        );

                        //check if already exists:
                        Optional<ReactRole> reactRoleResponse = reactRoleService.findReactRole(reactRoleRequest);

                        if (reactRoleResponse.isPresent()) {
                            sendError(member, textChannel, "⚠ | Error.", "The requested Reaction-Role already exists! \n Please check the requested Message in channel: " + targetChannel.getAsMention());
                        } else {
                            //create role:
                            ReactRole savedReactRole = reactRoleService.createReactRole(reactRoleRequest);
                            sendSuccess(member, textChannel, targetRole, savedReactRole.getEmote(), targetChannel);
                        }
                    }, error -> {
                        sendError(member, textChannel, "⚠ | Invalid Message-Id!.", "The requested Message doesn't exist: " + messageId);
                    });

                } catch (NumberFormatException e) {
                    sendError(member, textChannel, "⚠ | NumberFormatException.", "The requested Id is not a valid value.");
                }
            } else {
                sendError(member, textChannel, "⚠ | Syntax Error.", "The `!reactrole` command, requires a channel-Id, message-Id, role-Id and an emote to be specified. \n `!reactrole [channelId] [messageId] [roleId] [emote]`.");
            }
        } else {
            messageService.sendAccessDenied(member, textChannel, "you lack the permission to manage roles.");
        }
    }

    private void sendError(Member member, TextChannel textChannel, String title, String description) {
        messageService.sendMessageEmbed(member, textChannel,
                title,
                description,
                Color.decode("#f7c315")
        );
    }

    private void sendSuccess(Member member, TextChannel textChannel, Role role, String emote, TextChannel channel) {
        String title = "Success!";

        String text = "The role: " +
                role.getAsMention() +
                " is now available via reaction: " +
                emote +
                " in the channel:" +
                channel.getAsMention();
        messageService.sendMessageEmbed(member, textChannel, title, text, Color.decode("#13d96a"));
    }
}
