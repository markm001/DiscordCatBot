package com.ccat.catbot.commands;

import com.ccat.catbot.model.services.DiceRollService;
import com.ccat.catbot.model.services.MessageService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.awt.*;

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
            if(args[1].equalsIgnoreCase("stats")) {
                rollForStats(member,textChannel,args);
            } else {
                rollForValues(member, textChannel, args);
            }
        } else {
            sendError(member, textChannel, "⚠ | Syntax Error.", "The `!roll` command, requires a dice-type (and an amount). \n `!roll [sides] [amount]`.");
        }
    }

    private void rollForStats(Member member, TextChannel textChannel, String[] args) {
        Integer[] rolls;

        String title;
        StringBuilder resultBuilder = new StringBuilder();

        String diceEmoji = Emoji.fromUnicode("U+1F3B2").getFormatted();

        if(args.length > 2) {
            switch(args[2]) {
                case "4reroll": //4d6 drop lowest, reroll 1s
                    title = " | Rule - 4d6, drop lowest, reroll 1s: ";
                    rolls = rollService.rollStats(6,4,false,true,false,1,0);
                    break;
                case "3times": //3d6 3-times - use highest
                    title = " | Rule - 3d6, use highest: ";
                    rolls = rollService.rollStats(6,3,false,false,true,3,0);
                    break;
                case "3strict": //3d6 strict
                    title = " | Rule - 3d6 strict: ";
                    rolls = rollService.rollStats(6,3,false,false,false,1,0);
                    break;
                case "plus6": // 2d6 + 6:
                    title = " | Rule - 2d6, add 6: ";
                    rolls = rollService.rollStats(6,2,false,false,false,1,6);
                    break;
                default: //4d6 drop lowest
                    title = " | Default Rule - 4d6, drop lowest: ";
                    rolls = rollService.rollStats(6,4,true,false,false,1,0);
            }
        } else {
            title = "Default Rule - 4d6, drop lowest: ";
            rolls = rollService.rollStats(6,4,true,false,false,1,0);
        }

        for (int r = 0; r < rolls.length; r++) {
            resultBuilder.append(rolls[r]);
            if (r != (rolls.length - 1)) resultBuilder.append(" | ");
        }

        Color color = member.getColor();
        messageService.sendMessageEmbed(member, textChannel, diceEmoji + title, resultBuilder.toString(), color);
    }

    private void rollForValues(Member member, TextChannel textChannel, String[] args) {
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
    }

    private void sendError(Member member, TextChannel textChannel, String title, String description) {
        messageService.sendMessageEmbed(member, textChannel,
                title,
                description,
                Color.decode("#f7c315")
        );
    }
}
