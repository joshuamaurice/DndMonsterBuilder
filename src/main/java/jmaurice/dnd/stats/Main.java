package jmaurice.dnd.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import jmaurice.dnd.stats.builder.StandardStatsBuilder;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.StatsExecutor;
import jmaurice.dnd.stats.impl.ValuedStat;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Main: starting");
            final StringBuilder outputFileContent = new StringBuilder();
            final Map<String, String> creatureInputs = ParseCreatureInputsFromSheet.read(new File("../data/stats.fods"), "Sheet1");
            for (final Map.Entry<String, String> creatureInput : creatureInputs.entrySet()) {
                final String creatureName = creatureInput.getKey();
                try {
                    final String input = creatureInput.getValue();
                    
                    final Stats stats = new StandardStatsBuilder().build();
                    final Map<String, ValuedStat> valuedStats = stats.statNames().stream()
                            .collect(Collectors.toMap(name -> name, name -> new ValuedStat(stats.getStat(name))));
                    ParseCreatureInput.parseApply(valuedStats, input);
                    StatsExecutor.execute(stats, valuedStats);
                    final String creatureOutput = new TreeSet<>(valuedStats.keySet()).stream()
                            .map(statName -> valuedStats.get(statName))
                            .filter(stat -> stat.getValues().size() > 0)
                            .flatMap(stat -> stat.getValues().stream().map(v -> stat.name() + "=" + v.value))
                            .collect(Collectors.joining(";;"));
                    outputFileContent.append(creatureName);
                    outputFileContent.append("\t");
                    outputFileContent.append("\"");
                    outputFileContent.append(creatureOutput.replace("\"", "\"\""));
                    outputFileContent.append("\"");
                } catch (final Exception e) {
                    System.err.println("error running: " + creatureName);
                    e.printStackTrace();
                }
            }
            try (final Writer fout = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(new File("../data/generated.csv")), StandardCharsets.UTF_8))) {
                fout.write(outputFileContent.toString());
                fout.flush();
            }

        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Main: successful exit");
        System.exit(0);
    }

}
