package com.ccat.catbot.commands;

import com.ccat.catbot.model.entities.GuildMember;
import com.ccat.catbot.model.services.implementations.MemberPermissionService;
import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.EnumSet;

public class MemberPermissionCommand implements ServerCommand{

    private final MessageService messageService;
    private final MemberPermissionService permissionService;

    public MemberPermissionCommand(MessageService messageService, MemberPermissionService permissionService) {
        this.permissionService = permissionService;
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        String[] args = message.getContentDisplay().split(" ");

        if(member.hasPermission(Permission.MANAGE_PERMISSIONS)) {
            if(args.length == 3) {
                try {
                    Member mentionedMember = message.getMentions().getMembers().get(0);
                    long permissionCode = Long.parseLong(args[2]);

                    EnumSet<Permission> permissions = Permission.getPermissions(permissionCode);
                    if (permissions.isEmpty()) {
                        sendError(member, textChannel, "⚠ | Permission Error.", "The requested permission-code doesn't exist.");
                        return;
                    }

                    GuildMember guildMemberRequest = new GuildMember(
                            mentionedMember.getIdLong(),
                            textChannel.getGuild().getIdLong(),
                            permissionCode
                    );

                    permissionService.grantMemberPermission(guildMemberRequest);

                    String title = "Success!";
                    String text = mentionedMember.getAsMention() +
                            " has been granted permissions: '" +
                            permissionCode +
                            "' to interact with the bot.";

                    messageService.sendMessageEmbed(member, textChannel, title, text, Color.decode("#13d96a"));

                } catch (NumberFormatException e) {
                    sendError(member, textChannel, "⚠ | Error parsing Id.", "The requested Id is not a valid long value.");
                }
            } else {
                messageService.sendMessageEmbed(member,
                    textChannel,
                    "⚠ | Syntax Error.",
                    "The `!permit` command, requires a user and permission-code. `!permit [@user] [permission-code]`.",
                    Color.decode("#f7c315")
                );
            }

        } else {
            messageService.sendAccessDenied(member, textChannel, "you cannot grant permissions.");
        }
    }

    private void sendError(Member member, TextChannel textChannel, String title, String description) {
        messageService.sendMessageEmbed(member, textChannel,
                title,
                description,
                Color.decode("#f7c315")
        );
    }
}
