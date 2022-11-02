package com.ccat.catbot.listeners;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class OnlineStatusListener extends ListenerAdapter {

    private HashMap<Guild, Integer> activeMembersForGuild;

    public OnlineStatusListener() {
        this.activeMembersForGuild = new HashMap<>();
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        List<Member> activeMembers = event.getGuild().getMembers().stream().filter(m -> {
            OnlineStatus mStatus = m.getOnlineStatus();
            return !(m.getUser().isBot() ||
                    mStatus.equals(OnlineStatus.INVISIBLE) ||
                    mStatus.equals(OnlineStatus.OFFLINE) ||
                    mStatus.equals(OnlineStatus.UNKNOWN));
        }).collect(Collectors.toList());

        activeMembersForGuild.put(event.getGuild(), activeMembers.size());
    }

    public AtomicInteger getActiveMembersForEachGuild() {
        AtomicInteger counter = new AtomicInteger();
        activeMembersForGuild.forEach((guild, integer) -> counter.addAndGet(integer));

        return counter;
    }
}
