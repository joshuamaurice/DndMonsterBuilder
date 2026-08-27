package jmaurice.dnd.stats;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jmaurice.dnd.stats.impl.Value;
import jmaurice.dnd.stats.impl.ValuedStat;

public class ParseCreatureInput {
    
    private static Pattern numberPattern = Pattern.compile("^[-+0-9.]+$");
    private static Pattern p1 = Pattern.compile("^([^\"]+)$");
    private static Pattern p2 = Pattern.compile("^ *\"([^\"]*)\" +([^\"]+)$");
    private static Pattern p3 = Pattern.compile("^ *([^\" ]+) +([^\"]+)$");
    
    public static void parseApply(final Map<String, ValuedStat> stats, String input1) {
        if (input1 == null)
            throw new NullPointerException();
        input1 = input1.replace('”', '\"');
        input1 = input1.replace('“', '\"');
        final List<String> input1s = Arrays.asList(input1.split(";;;", -1)).stream().toList();
        for (final String input2 : input1s) {
            if (input2.isEmpty())
                continue;
            if (input2.startsWith("##"))
                return;
            for (final String input3 : Arrays.asList(input2.split(";;", -1)).stream().map(x -> x.trim()).toList()) {
                if (input3.isEmpty())
                    continue;
                if (input3.startsWith("#"))
                    continue;
                if (numberPattern.matcher(input3).matches())
                    continue;
                Matcher matcher;
                matcher = p1.matcher(input3);
                if (matcher.matches()) {
                    final String name = input3.trim();
                    final ValuedStat stat = stats.get(name);
                    if (stat != null) {
                        stat.addInitialValue(new Value(true, "input"));
                        continue;
                    }
                }
                matcher = p2.matcher(input3);
                if (matcher.matches()) {
                    final String name = matcher.group(2).trim();
                    final String value = matcher.group(1);
                    final ValuedStat stat = stats.get(name);
                    if (stat == null)
                        throw new RuntimeException("unrecognized stat name: " + name);
                    stat.addInitialValue(new Value(value, "input"));
                    continue;
                }
                matcher = p3.matcher(input3);
                if (matcher.matches()) {
                    final String name = matcher.group(2).trim();
                    final String value = matcher.group(1);
                    final ValuedStat stat = stats.get(name);
                    if (stat != null) {
                        stat.addInitialValue(new Value(value, "input"));
                        continue;
                    }
                }
                throw new RuntimeException("Cannot parse input >>" + input3 + "<< from inputLine: " + input2);
            }
        }
    }

}
