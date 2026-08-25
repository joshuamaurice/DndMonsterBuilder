package jmaurice.dnd.stats;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import jmaurice.dnd.stats.builder.StandardStatsBuilder;
import jmaurice.dnd.stats.impl.Stat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.StatsExecutor;
import jmaurice.dnd.stats.impl.Value;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Main: starting");
            final Stats stats = new StandardStatsBuilder().build();
            final String input = """
                    5 challenge rating
                    N alignment
                    medium size
                    tyranid
                    # 1.3 m
                    # 0.2 ton
                    6
                    10 aberration hit dice
                    0 natural armor bonus
                    4 natural armor bonus
                    30 ft base land speed
                    10 strength
                    8 strength
                    10 dexterity
                    8 dexterity
                    3
                    1
                    10 constitution
                    4 constitution
                    5
                    2 intelligence
                    12 wisdom
                    7 charisma
                    1 bite
                    2 claws ;; "name=claws, secondary natural" weapon properties
                    1 fleshborer
                    1 tyranid upgrades
                    """;
            for (final String input1 : Arrays.asList(input.split("\n", -1)).stream().map(x -> x.trim()).toList()) {
                ParseInput.parseApply(stats, input1);
            }
            new StatsExecutor(stats).execute();

            System.out.println("output:");
            for (final String name : new TreeSet<>(stats.statNames())) {
                final Stat stat = stats.getStat(name);
                if (stat.getValues().size() > 0) {
                    if (stat.isLeaf()) {
                        final List<Value> values = stat.getValues();
                        System.out.println(stat.name() + ": " + values);
                    }
                }
            }

        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Main: successful exit");
        System.exit(0);
    }

}
