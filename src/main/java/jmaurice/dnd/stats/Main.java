package jmaurice.dnd.stats;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import jmaurice.dnd.stats.builder.StandardStatsBuilder;
import jmaurice.dnd.stats.fods.ReadFodsXml;
import jmaurice.dnd.stats.impl.Stat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Main: starting");
            final Map<String, String> creatureInputs = ReadFodsXml.readCombinedInputColumn(new File("../data/stats.fods"), "Sheet1");
            for (final Map.Entry<String, String> creatureInput : creatureInputs.entrySet()) {
                final String creatureName = creatureInput.getKey();
                final String input = creatureInput.getValue();
                
                final Stats stats = StandardStatsBuilder.run(input);

                System.out.println("output: " + creatureName);
                for (final String statName : new TreeSet<>(stats.statNames())) {
                    final Stat stat = stats.getStat(statName);
                    if (stat.getValues().size() > 0) {
                        if (stat.isLeaf()) {
                            final List<Value> values = stat.getValues();
                            System.out.println(stat.name() + ": " + values);
                        }
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
