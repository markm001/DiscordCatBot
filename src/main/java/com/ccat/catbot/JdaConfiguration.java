package com.ccat.catbot;

import com.ccat.catbot.listeners.*;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

@Component
public class JdaConfiguration {
    public static JdaConfiguration INSTANCE;
    private ShardManager shardManager;
    @Autowired
    private Environment env;

    private final CommandListener commandListener;
    private final OnlineStatusListener onlineStatusListener;
    private final MemberJoinListener memberJoinListener;
    private final DeleteReactionListener deleteReactionListener;
    private final VoiceChannelListener voiceChannelListener;
    private final RoleReactionListener roleReactionListener;

    private final EventWaiter eventWaiter;

    private Thread loopThread;
    private int waitTime = 15;
    private final String[] statusDisplay = new String[]{
            "use !help for a full list of commands.",
            "%members"};

    public JdaConfiguration(CommandListener commandListener, OnlineStatusListener onlineStatusListener,
                            MemberJoinListener memberJoinListener, DeleteReactionListener deleteReactionListener,
                            VoiceChannelListener voiceChannelListener, RoleReactionListener roleReactionListener) {
        this.commandListener = commandListener;
        this.onlineStatusListener = onlineStatusListener;
        this.memberJoinListener = memberJoinListener;
        this.deleteReactionListener = deleteReactionListener;
        this.voiceChannelListener = voiceChannelListener;
        this.roleReactionListener = roleReactionListener;

        this.eventWaiter = new EventWaiter();
    }

    @PostConstruct
    @ConfigurationProperties("discord-api")
    private void jdaBuild() {
        INSTANCE = this;
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(env.getProperty("TOKEN"));

        System.out.println("Initializing Bot...");
        builder.setStatus(OnlineStatus.IDLE);
        builder.setActivity(Activity.watching("the world burn."));

        //Intents:
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.enableIntents(GatewayIntent.GUILD_PRESENCES);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        //Caching:
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL); //Cache all Users on Start-up (lazy-loading)
        builder.enableCache(CacheFlag.ONLINE_STATUS);
        builder.enableCache(CacheFlag.ROLE_TAGS);

        //Listeners:
        builder.addEventListeners(eventWaiter);
        builder.addEventListeners(commandListener);
        builder.addEventListeners(onlineStatusListener);
        builder.addEventListeners(memberJoinListener);
        builder.addEventListeners(deleteReactionListener);
        builder.addEventListeners(voiceChannelListener);
        builder.addEventListeners(roleReactionListener);

        shardManager = builder.build();

        System.out.println("Bot initialized. All green.");
        shardManager.setStatus(OnlineStatus.ONLINE);

        runLoop();
        checkShutdown();
    }

    private void checkShutdown() {
        new Thread(() -> {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            try {
                while ((line = reader.readLine()) != null) {
                    if(line.equalsIgnoreCase("shutdown")) {
                        if(shardManager != null){
                            shardManager.shutdown();
                            System.out.println("Shutdown completed.");
                        }
                        if(loopThread.isAlive()) {
                            loopThread.interrupt();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void runLoop() {
        this.loopThread = new Thread(() -> {
            long time = System.currentTimeMillis();

            while (true) {
                if (System.currentTimeMillis() >= time + 1000) {
                    time = System.currentTimeMillis();

                    if (waitTime <= 0) {
                        onTimeout();
                    } else waitTime--;
                }
            }
        });

        loopThread.setName("Loop");
        loopThread.start();
    }

    private void onTimeout() {
        Random r = new Random();
        int i = r.nextInt(statusDisplay.length);

        int activeMembers = onlineStatusListener.getActiveMembersForEachGuild().get();
        String currStatus = statusDisplay[i]
                .replaceAll("%members", (activeMembers > 0) ? activeMembers + "online." : shardManager.getUsers().size() + "users.");

        shardManager.setActivity(Activity.watching(currStatus));
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public EventWaiter getEventWaiter() {
        return eventWaiter;
    }
}
