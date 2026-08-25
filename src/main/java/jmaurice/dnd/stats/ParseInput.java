package jmaurice.dnd.stats;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jmaurice.dnd.stats.impl.Stat;
import jmaurice.dnd.stats.impl.Stats;
import jmaurice.dnd.stats.impl.Value;

public class ParseInput {
    
    public static void parseApply(final Stats stats, final String inputLine) {
        if (inputLine.startsWith("##"))
            return;
        for (final String input : Arrays.asList(inputLine.split(";;", -1)).stream().map(x -> x.trim()).toList()) {
            if (input.startsWith("#"))
                continue;
            if (input.isBlank())
                continue;
            if (input.matches("^[-+0-9.]+$"))
                continue;
            Matcher matcher = Pattern.compile("^\"([^\"]*)\" +([^\"]+)$").matcher(input);
            if (matcher.matches()) {
                final String name = matcher.group(2).trim();
                final String value = matcher.group(1);
                final Stat stat = stats.getStat(name);
                if (stat == null)
                    throw new RuntimeException("unrecognized stat name: " + name);
                stat.addInitialValue(new Value(value, "input"));
                continue;
            }
            matcher = Pattern.compile("^([^ ]+) +([^\"]+)$").matcher(input);
            if (matcher.matches()) {
                final String name = matcher.group(2).trim();
                final String value = matcher.group(1);
                final Stat stat = stats.getStat(name);
                if (stat == null)
                    throw new RuntimeException("unrecognized stat name: " + name);
                stat.addInitialValue(new Value(value, "input"));
                continue;
            }
            matcher = Pattern.compile("^([^\"]+)$").matcher(input);
            if (matcher.matches()) {
                final Stat stat = stats.getStat(input);
                if (stat != null) {
                    stat.addInitialValue(new Value(true, "input"));
                    continue;
                }
            }
            throw new RuntimeException("Cannot parse input >>" + input + "<< from inputLine: " + inputLine);
        }
    }

}
