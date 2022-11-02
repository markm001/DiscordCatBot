package com.ccat.catbot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PurgeCommand implements ServerCommand{
    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        if(member.hasPermission(textChannel, Permission.MESSAGE_MANAGE)) {
            String[] args = message.getContentDisplay().split(" ");

            assert args.length == 2;
            try {
                AtomicInteger amount = new AtomicInteger(Integer.parseInt(args[1]));
                List<Message> channelMessages = purgeMessageAmount(textChannel, amount);

                textChannel.purgeMessages(channelMessages);
                int purgedAmount = channelMessages.size();
                textChannel
                        .sendMessage(purgedAmount > 1 ? purgedAmount + "messages have been purged." : purgedAmount + "message has been purged.")
                        .complete().delete().queueAfter(5, TimeUnit.SECONDS);

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
          textChannel.sendMessage("You lack permissions to delete messages in this channel.").queue();
        }
    }

    private List<Message> purgeMessageAmount(TextChannel textChannel, AtomicInteger amount) {
        List<Message> messagesToPurge = new ArrayList<>();

        for(Message message : textChannel.getIterableHistory().cache(false)) {
            if(!message.isPinned()) {
                messagesToPurge.add(message);
            }
            if(amount.decrementAndGet() <= 0) break;
        }
        return messagesToPurge;
    }
}
