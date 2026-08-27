package jmaurice.dnd.stats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        System.out.println("Main: starting");
        try {
            final File inputFile = new File("../data/stats.fods");
            final String inputFileSheetName = "Sheet1";
            final File outputFile = new File("../data/generated.csv");
            long lastModified = 1;
            while (true) {
                final long lastModified2 = inputFile.lastModified();
                if (lastModified != lastModified2) {
                    updateOutputFile(inputFile, inputFileSheetName, outputFile);
                    lastModified = lastModified2;
                }
                Thread.sleep(1000);
            }
        } catch (Exception | AssertionError e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Main: successful exit");
        System.exit(0);
    }
    
    private static void updateOutputFile(final File inputFile, final String inputFileSheetName, final File outputFile) throws IOException {
        System.out.println("updateOutputFile: starting");
        final StringBuilder outputFileContent = new StringBuilder();
        final Map<String, String> creatureInputs = ParseCreatureInputsFromSheet.read(inputFile, inputFileSheetName);
        for (final Map.Entry<String, String> creatureInput : creatureInputs.entrySet()) {
            final String creatureName = creatureInput.getKey();
            System.out.println("updateOutputFile: " + creatureName);
            try {
                final String input = creatureInput.getValue();
                
                final Stats stats = new StandardStatsBuilder().build();
                final Map<String, ValuedStat> valuedStats = stats.statNames().stream()
                        .collect(Collectors.toMap(name -> name, name -> new ValuedStat(stats.getStat(name))));
                ParseCreatureInput.parseApply(valuedStats, input);
                System.out.println(valuedStats.entrySet().stream().filter(x -> x.getValue().getValues().size() > 0).toList());
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
                outputFileContent.append(creatureName);
                outputFileContent.append("\t");
                outputFileContent.append("\"");
                outputFileContent.append(getUsefulMessage(e).replace("\"", "\"\""));
                outputFileContent.append("\"");
            }
            outputFileContent.append("\n");
        }
        try (final Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            fout.write(outputFileContent.toString());
            fout.flush();
        }
    }

    private static String getUsefulMessage(Throwable e) {
        if (e == null)
            return null;
        if (e.getMessage() != null)
            return e.getMessage();
        final String causeUsefulMessage = getUsefulMessage(e.getCause());
        if (causeUsefulMessage != null)
            return causeUsefulMessage;
        if (e.getSuppressed() != null) {
            for (final Throwable suppressed : e.getSuppressed()) {
                final String suppressedUsefulMessage = getUsefulMessage(suppressed);
                if (suppressedUsefulMessage != null)
                    return suppressedUsefulMessage;
            }
        }
        while (e instanceof RuntimeException && e.getCause() != null)
            e = e.getCause();
        return e.getClass().getSimpleName();
    }

}
