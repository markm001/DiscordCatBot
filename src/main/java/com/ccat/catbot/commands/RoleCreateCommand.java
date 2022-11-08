package com.ccat.catbot.commands;

import com.ccat.catbot.model.entities.GuildMember;
import com.ccat.catbot.model.services.MemberPermissionService;
import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.regex.Pattern;

public class RoleCreateCommand implements ServerCommand{

    private final MessageService messageService;
    private final MemberPermissionService permissionService;

    public RoleCreateCommand(MessageService messageService, MemberPermissionService permissionService) {
        this.messageService = messageService;
        this.permissionService = permissionService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        // !createrole <name> #color
        String[] args = message.getContentDisplay().split(" ");
        Guild guild = textChannel.getGuild();

        GuildMember guildMemberRequest = new GuildMember(
                member.getIdLong(),
                textChannel.getGuild().getIdLong(),
                Permission.MANAGE_ROLES.getRawValue()
        );

        boolean hasPermission = false;
        if((member.hasPermission(Permission.MANAGE_ROLES)) ||
                permissionService.checkMemberPermissions(guildMemberRequest)) {
            hasPermission = true;
        }

        if(hasPermission) {
            if (args.length > 1) {
                if (args.length == 3) {
                    guild.createRole().queue(role -> {
                        Pattern hexPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");

                        if (hexPattern.matcher(args[2]).matches()) {
                            Color hexColor = Color.decode(args[2]);

                            role.getManager()
                                    .setColor(hexColor)
                                    .setPermissions(3214336L)
                                    .setName(args[1]).queue();

                            //success:
                            sendSuccess(member, textChannel, role, hexColor);
                        } else {
                            textChannel.sendMessage("Error creating role. Please use a valid Color in hexadecimal format." + member.getAsMention()).queue();
                        }
                    });
                } else {
                    guild.createRole().queue(role -> {
                        Color color = new Color(255, 255, 255);

                        role.getManager()
                                .setColor(color)
                                .setPermissions(3214336L)
                                .setName(args[1]).queue();

                        //success:
                        sendSuccess(member, textChannel, role, color);
                    });
                }
            } else {
                messageService.sendMessageEmbed(member, textChannel,
                        "⚠ | Syntax Error.",
                        "The `!createrole` command, requires a role name (and a color) to be specified. `!createrole [name] [#colorcode]`.",
                        Color.decode("#f7c315")
                );
            }
        } else {
            textChannel.sendMessage(member.getAsMention() + " you lack permission to create any roles.").queue();
        }
    }

    private void sendSuccess(Member member, TextChannel textChannel, Role role, Color color) {
        String title = "Success!";
        String text = "The role: " + role.getAsMention() + " has been successfully created by: " + member.getAsMention();
        messageService.sendMessageEmbed(member, textChannel, title, text, color);
    }
}
