package com.ccat.catbot;

import com.ccat.catbot.listeners.CommandListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JdaConfiguration {
    private JDABuilder jdaInstance;
    @Autowired
    private Environment env;

    private CommandListener commandListener;

    public JdaConfiguration(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    @PostConstruct
    @ConfigurationProperties("discord-api")
    private void jdaBuild() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(env.getProperty("TOKEN"));

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(commandListener);

        builder.build();
    }
}
