package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import jmaurice.dnd.graph.Graph;

public class Stats {
    
    private Map<String, Stat> stats = new LinkedHashMap<>();
    private Graph<String> graph;
    private Graph<String> inverseGraph;
    
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
        final Function<Map<String, ReadOnlyValuedStat>, List<Value>> rule
    ) {
        final InputRule rule2 = new InputRule();
        rule2.inputStatNames = new ArrayList<>(inputStatNames);
        rule2.rule = rule;
        final Stat outputStat = getOrCreateStat(outputStatName);
        outputStat.inputRules.add(rule2);
        rule2.inputStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
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
        final List<String> writableStatNames,
        final List<String> readOnlyStatNames,
        final BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule
    ) {
        final PostRule rule2 = new PostRule();
        rule2.writableStatNames = new ArrayList<>(writableStatNames);
        rule2.readOnlyStatNames = new ArrayList<>(readOnlyStatNames);
        rule2.rule = rule;
        final Stat newStat = getOrCreateStat(newStatName);
        if (newStat.postRule != null)
            throw new RuntimeException("rule already has a post rule: " + newStat.name);
        newStat.setLeaf(true);
        newStat.postRule = rule2;
        rule2.writableStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
        rule2.readOnlyStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
    }
    
    public void initializeGraphAndInverseGraph() {
        final Function<String, Set<String>> next = name -> {
            final Stat stat = stats.get(name);
            final Set<String> r = new LinkedHashSet<>();
            stat.inputRules.forEach(rule -> r.addAll(rule.inputStatNames));
            if (stat.postRule != null) {
                r.addAll(stat.postRule.readOnlyStatNames);
            }
            return r;
        };
        graph = Graph.fromRoots(stats.keySet(), next).inverse();
        
        //createPostLinks
        for (final Stat stat : stats.values()) {
            if (stat.postRule != null) {
                for (final String writableStatName : stat.postRule.writableStatNames) {
                    final Set<String> writableStatOutputs = graph.edges(writableStatName);
                    for (final String writableStatOutput : writableStatOutputs) {
                        //A PostRule stat must run before the outputs of the target stats.
                        graph.addEdge(stat.name, writableStatOutput);
                    }
                }
            }
        }
        for (final Stat stat : stats.values()) {
            if (stat.postRule != null) {
                for (final String postStatName : stat.postRule.writableStatNames) {
                    //Ensure that a stat with a PostRule runs after
                    //the target stats of the PostRule.
                    graph.addEdge(stat.name, postStatName);
                }
            }
        }
        
        //
        inverseGraph = graph.inverse();
        
        //
        for (final String rootName : inverseGraph.leafs()) {
            final Stat root = stats.get(rootName);
            if ( ! root.root) {
                throw new RuntimeException("found unmarked root stat: " + root.name);
            }
        }
        for (final String leafName : graph.leafs()) {
            final Stat leaf = stats.get(leafName);
            if ( ! leaf.leaf) {
                throw new RuntimeException("found unmarked leaf stat: " + leaf.name);
            }
        }
    }

    public Graph<String> graph() { return graph; }
    public Graph<String> inverseGraph() { return inverseGraph; }
    
}
