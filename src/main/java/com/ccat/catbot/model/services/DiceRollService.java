package com.ccat.catbot.model.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class DiceRollService {
    private Random random;

    public DiceRollService() {
        random = new Random();
    }

    public Integer[] rollDice(int sides, int amount) {
        Integer[] totalRolls = new Integer[amount];

        for (int i = 0; i < amount; i++) {
            totalRolls[i] = (random.nextInt(sides) + 1);
        }

        return totalRolls;
    }

    public Integer[] rollStats() {
        // 4d6, drop lowest
        int sides = 6;
        int amount = 4;
        int totalStats = 6;

        Integer[] totalRolls = new Integer[totalStats];

        for (int i = 0; i < totalStats; i++) {
            List<Integer> statList = Arrays.asList(rollDice(sides, amount));
            Collections.sort(statList);
            statList.remove(0); //drop lowest

            System.out.println("\n ## Total Rolls Debug - Iteration:" + i + " stats: " + statList + "\n");

            totalRolls[i] = statList.stream().mapToInt(Integer::intValue).sum();
        }

        return totalRolls;
    }
}
