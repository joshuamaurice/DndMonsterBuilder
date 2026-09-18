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
import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

public class Main {

    public static void main(String[] args) {
        System.out.println("Main: starting");
        try {
            final File inputFile = new File("../data/stats.fods");
            final String inputFileSheetName = "Sheet1";
            final File outputStatsListCsv = new File("../data/generated.csv");
            final File outputStatBlockHtml = new File("../data/stats.html");
            long lastModified = 1;
            while (true) {
                final long lastModified2 = inputFile.lastModified();
                if (lastModified != lastModified2) {
                    updateOutputFiles(inputFile, inputFileSheetName, outputStatsListCsv, outputStatBlockHtml);
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
    
    private static void updateOutputFiles(
            final File inputFile, 
            final String inputFileSheetName, 
            final File outputStatsListCsv,
            final File outputStatBlockHtml
            ) throws IOException {
        System.out.println("updateOutputFile: starting");
        final StringBuilder outputStatsListCsvContent = new StringBuilder();
        final StringBuilder outputStatBlockHtmlContent = new StringBuilder();
        outputStatBlockHtmlContent.append(HtmlHeadStyle);
        outputStatBlockHtmlContent.append("<p>");
        final Map<String, String> creatureInputs = ParseCreatureInputsFromSheet.read(inputFile, inputFileSheetName);
        for (final Map.Entry<String, String> creatureInput : creatureInputs.entrySet()) {
            final String creatureName = creatureInput.getKey();
            try {
                final String input = creatureInput.getValue();
                
                final Stats stats = new StandardStatsBuilder().build();
                final Map<String, ValuedStat> valuedStats = stats.statNames().stream()
                        .collect(Collectors.toMap(name -> name, name -> new ValuedStat(stats.getStat(name))));
                ParseCreatureInput.parseApply(valuedStats, input);
                StatsExecutor.execute(stats, valuedStats);
                for (final ValuedStat stat : valuedStats.values()) {
                    if (stat.getValues().contains(null)) {
                        throw new RuntimeException("Stat " + stat.name() + " contains a null Value");
                    }
                }
                final String creatureOutput = new TreeSet<>(valuedStats.keySet()).stream()
                        .map(statName -> valuedStats.get(statName))
                        .filter(stat -> stat.getValues().size() > 0)
                        .flatMap(stat -> stat.getValues().stream().map(v -> stat.name() + "=" + serialize(v)))
                        .collect(Collectors.joining(";;"));
                outputStatsListCsvContent.append(creatureName);
                outputStatsListCsvContent.append("\t");
                outputStatsListCsvContent.append("\"");
                outputStatsListCsvContent.append(";;");
                outputStatsListCsvContent.append(creatureOutput.replace("\"", "\"\""));
                outputStatsListCsvContent.append(";;");
                outputStatsListCsvContent.append("\"");
                
                outputStatBlockHtmlContent.append(valuedStats.get("stat block PFSRD").getStringValue());
                outputStatBlockHtmlContent.append("<br/><br/>");
                
            } catch (final Exception e) {
                new RuntimeException("Error running creature " + creatureName, e).printStackTrace();;
                outputStatsListCsvContent.append(creatureName);
                outputStatsListCsvContent.append("\t");
                outputStatsListCsvContent.append("\"");
                outputStatsListCsvContent.append(getUsefulMessage(e).replace("\"", "\"\""));
                outputStatsListCsvContent.append("\"");
            }
            outputStatsListCsvContent.append("\n");
        }
        outputStatBlockHtmlContent.append("</p>");
        try (final Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputStatsListCsv), StandardCharsets.UTF_8))) {
            fout.write(outputStatsListCsvContent.toString());
            fout.flush();
        }
        try (final Writer fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputStatBlockHtml), StandardCharsets.UTF_8))) {
            fout.write(outputStatBlockHtmlContent.toString());
            fout.flush();
        }
    }

    private static Object serialize(final Value value) {
        if (value.source != null && ! value.source.isBlank())
            return value.value + " (" + value.source + ")";
        return value.value.toString();
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
    
    private static final String HtmlHeadStyle = """
            <head>
                <meta charset="UTF-8">
                <style>
                    * {
                        white-space: normal;
                    }
                    p {
                        margin-top: 0;
                        margin-bottom: 0;
                    }
                    ul {
                        margin-top: 0;
                        margin-bottom: 0;
                    }
                    li {
                        margin-top: 0;
                        margin-bottom: 0;
                    }
                </style>
            </head>
            """;

}
