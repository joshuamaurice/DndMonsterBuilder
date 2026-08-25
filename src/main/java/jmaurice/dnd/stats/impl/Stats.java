package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Stats {
    
    private Map<String, Stat> stats = new LinkedHashMap<>();
    
    public Set<String> statNames() {
        return Collections.unmodifiableSet(stats.keySet());
    }

    public Stat getOrCreateStat(String name) {
        return stats.compute(name, (k,v) -> {
            if (v != null)
                return v;
            v = new Stat();
            v.name = k;
            return v;
        });
    }
    
    public Stat getStat(String name) {
        return stats.get(name);
    }

    public void input(
        final String outputStatName, 
        final List<String> inputStatNames, 
        final Function<Map<String, ReadOnlyStat>, List<Value>> rule
    ) {
        final InputRule rule2 = new InputRule();
        rule2.inputStatNames = new ArrayList<>(inputStatNames);
        rule2.rule = rule;
        final Stat outputStat = getOrCreateStat(outputStatName);
        outputStat.inputRules.add(rule2);
        rule2.inputStatNames.stream().forEach(inputStatName -> {
            final Stat inputStat = getOrCreateStat(inputStatName);
            inputStat.outputStats.add(outputStat);
            outputStat.inputStats.add(inputStat);
        });
    }
    
    public void agg(
        final String statName, 
        final Function<List<Value>, List<Value>> rule
    ) {
        final AggRule rule2 = new AggRule();
        rule2.rule = rule;
        final Stat stat = getOrCreateStat(statName);
        if (stat.aggRule != null)
            throw new RuntimeException("rule already has an agg rule: " + stat.name);
        stat.aggRule = rule2;
    }
    
    public void post(
        final String newStatName, 
        final List<String> postStatNames,
        final List<String> otherInputStatNames,
        final BiConsumer<Map<String, Stat>, Map<String, ReadOnlyStat>> rule
    ) {
        final PostRule rule2 = new PostRule();
        rule2.postStatNames = new ArrayList<>(postStatNames);
        rule2.otherInputStatNames = new ArrayList<>(otherInputStatNames);
        rule2.rule = rule;
        final Stat newStat = getOrCreateStat(newStatName);
        if (newStat.postRule != null)
            throw new RuntimeException("rule already has a post rule: " + newStat.name);
        newStat.setLeaf(true);
        newStat.postRule = rule2;
        rule2.postStatNames.stream().forEach(inputStatName -> {
            final Stat inputStat = getOrCreateStat(inputStatName);
            inputStat.preOutputStats.add(newStat);
        });
        rule2.otherInputStatNames.stream().forEach(inputStatName -> {
            final Stat inputStat = getOrCreateStat(inputStatName);
            inputStat.outputStats.add(newStat);
            newStat.inputStats.add(inputStat);
        });
    }
    
    public void createPostLinks() {
        for (final Stat stat : stats.values()) {
            for (final Stat preOutputStat : stat.preOutputStats) {
                for (final Stat outputStat : stat.outputStats) {
                    outputStat.inputStats.add(preOutputStat);
                    preOutputStat.outputStats.add(outputStat);
                }
            }
        }
        for (final Stat stat : stats.values()) {
            for (final Stat preOutputStat : stat.preOutputStats) {
                stat.outputStats.add(preOutputStat);
                preOutputStat.inputStats.add(stat);
            }
        }
    }
    
    public void verifyNoUnmarkedRootsLeafs() {
        for (final Stat stat : stats.values()) {
            if ( ! stat.leaf && stat.outputStats.isEmpty()) {
                throw new RuntimeException("found unmarked leaf stat: " + stat.name);
            }
            if ( ! stat.root && stat.inputStats.isEmpty()) {
                throw new RuntimeException("found unmarked root stat: " + stat.name);
            }
        }
    }
    
    Map<String, Stat> stats() {
        return stats;
    }
    
}
