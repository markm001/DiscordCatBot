package com.ccat.catbot.model.services.implementations;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiceRollService {
    private final Random random;

    public DiceRollService() {
        random = new Random();
    }

    private int roll(int sides) {
        return random.nextInt(sides) + 1;
    }

    public Integer[] rollDice(int sides, int amount) {
        Integer[] totalRolls = new Integer[amount];

        for (int i = 0; i < amount; i++) {
            totalRolls[i] = roll(sides);
        }
        return totalRolls;
    }

    public Integer[] rollStats(int sides,
                               int amount,
                               boolean drop_lowest,
                               boolean allow_reroll,
                               boolean use_highest,
                               int tries,
                               int roll_bonus) {

        int totalStats = 6;
        assert tries > 0;

        Integer[] finalRolls = new Integer[6];

        for (int i = 0; i < totalStats; i++) {

            //rolls 4d6 | 3d6:
            List<Integer> results = Arrays.stream(rollDice(sides, amount)).collect(Collectors.toList());


            if (use_highest) { //drop all but highest
                List<Integer> tmpResults = new ArrayList<>();

                for (int n = 0; n < tries; n++) {
                    Optional<Integer> highestNumber = Arrays
                            .stream(rollDice(sides, amount))
                            .max(Integer::compareTo);

                    highestNumber.ifPresent(tmpResults::add);
                }
                // 3 | 6 | 4 ->
                results = tmpResults;
            }

            //rerolls 1s:
            if (allow_reroll) {
                results = results.stream()
                        .map(integer -> (integer == 1) ? roll(sides) : integer)
                        .collect(Collectors.toList());
            }

            Collections.sort(results);

            //remove lowest:
            if (drop_lowest) {
                results.remove(0); //drop lowest
            }

            System.out.println("\n ## Total Rolls Debug - Iteration:" + i + " stats: " + results + "\n");
            //sum:
            finalRolls[i] = results.stream().mapToInt(Integer::intValue).sum() + roll_bonus;
        }

        return finalRolls;
    }
}
