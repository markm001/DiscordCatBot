package com.ccat.catbot.listeners;

import com.ccat.catbot.model.entities.ReactRole;
import com.ccat.catbot.model.services.ReactRoleService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class RoleReactionListener extends ListenerAdapter {
    ReactRoleService reactRoleService;

    public RoleReactionListener(ReactRoleService reactRoleService) {
        this.reactRoleService = reactRoleService;
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.isFromType(ChannelType.TEXT)) {
            if (!Objects.requireNonNull(event.getUser()).isBot()) {
                Guild guild = event.getGuild();

                long channelId = event.getChannel().asTextChannel().getIdLong();
                long guildId = guild.getIdLong();
                long messageIdLong = event.getMessageIdLong();
                EmojiUnion emoji = event.getEmoji();

                String formattedEmoji = emoji.getFormatted();

                if(emoji.getType() == Emoji.Type.CUSTOM) {
                    // <:emote:1035899139189387284> <-> emote:1035899139189387284
                    formattedEmoji = formattedEmoji.replace("<","").replace(">","").substring(1);
                }

                ReactRole reactRoleRequest = new ReactRole(
                        guildId,
                        channelId,
                        messageIdLong,
                        formattedEmoji);

                Optional<ReactRole> reactRoleResponse = reactRoleService.findReactRole(reactRoleRequest);

                if (reactRoleResponse.isPresent()) {
                    Long roleId = reactRoleResponse.get().getRoleId();
                    Role roleRequest;
                    if ((roleRequest = guild.getRoleById(roleId)) != null) {
                        guild.addRoleToMember(event.getUser(), roleRequest).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if(event.isFromType(ChannelType.TEXT)) {
            Guild guild = event.getGuild();

            EmojiUnion emoji = event.getEmoji();

            String formattedEmoji = emoji.getFormatted();

            if(emoji.getType() == Emoji.Type.CUSTOM) {
                // <:emote:1035899139189387284> <-> emote:1035899139189387284
                formattedEmoji = formattedEmoji.replace("<","").replace(">","").substring(1);
            }

            ReactRole reactRoleRequest = new ReactRole(
                    guild.getIdLong(),
                    event.getChannel().asTextChannel().getIdLong(),
                    event.getMessageIdLong(),
                    formattedEmoji);
            Optional<ReactRole> reactRoleResponse = reactRoleService.findReactRole(reactRoleRequest);

            reactRoleResponse.ifPresent(
                    reactRole -> guild.retrieveMemberById(event.getUserIdLong()).queue(member -> {
                Role role = guild.getRoleById(reactRole.getRoleId());
                if (role != null) {
                    guild.removeRoleFromMember(member, role).queue();
                }
            }));
        }
    }
}
