package com.ccat.catbot.commands;

import com.ccat.catbot.model.services.DiceRollService;
import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DiceRollCommand implements ServerCommand {

    private final DiceRollService rollService;
    private final MessageService messageService;

    public DiceRollCommand(DiceRollService rollService, MessageService messageService) {
        this.rollService = rollService;
        this.messageService = messageService;
    }

    @Override
    public void executeCommand(Member member, TextChannel textChannel, Message message) {
        // !roll [sides] (amount)
        String[] args = message.getContentDisplay().split(" ");

        if (args.length >= 2) {
            try {
                int sides = Integer.parseInt(args[1]);
                int amount = (args.length != 3) ? 1 : Integer.parseInt(args[2]);

                Integer[] rolls = rollService.rollDice(sides, amount);

                String title = "Rolled: " + amount + "d" + sides;
                StringBuilder resultBuilder = new StringBuilder();

                for (int r = 0; r < rolls.length; r++) {
                    resultBuilder.append(rolls[r]);
                    if (r != (rolls.length - 1)) resultBuilder.append(" | ");
                }

                Color color = member.getColor();
                messageService.sendMessageEmbed(member, textChannel, title, resultBuilder.toString(), color);

            } catch (NumberFormatException e) {
                sendError(member, textChannel, "⚠ | Number-Format Error.", "Please use a valid input number.");
            }
        } else {
            sendError(member, textChannel, "⚠ | Syntax Error.", "The `!roll` command, requires a dice-type (and an amount). \n `!roll [sides] [amount]`.");
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
