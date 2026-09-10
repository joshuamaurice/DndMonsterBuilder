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
    
    public void preAgg(
        final String postStatName, 
        final List<String> readOnlyStatNames,
        final BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule
    ) {
        preAggX(postStatName, Collections.emptyList(), readOnlyStatNames, rule);
    }
    
    public void preAggX(
        final String postStatName, 
        final List<String> additionalWritableStatNames,
        final List<String> readOnlyStatNames,
        final BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule
    ) {
        final PostRule rule2 = new PostRule();
        rule2.additionalWritableStatNames = new ArrayList<>(additionalWritableStatNames);
        rule2.readOnlyStatNames = new ArrayList<>(readOnlyStatNames);
        rule2.rule = rule;
        final Stat newStat = getOrCreateStat(postStatName);
        newStat.preAggRules.add(rule2);
        rule2.additionalWritableStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
        rule2.readOnlyStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
    }
    
    public void postAgg(
        final String postStatName, 
        final List<String> readOnlyStatNames,
        final BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule
    ) {
        postAggX(postStatName, Collections.emptyList(), readOnlyStatNames, rule);
    }
        
    public void postAggX(
        final String postStatName, 
        final List<String> additionalWritableStatNames,
        final List<String> readOnlyStatNames,
        final BiConsumer<Map<String, ValuedStat>, Map<String, ReadOnlyValuedStat>> rule
    ) {
        final PostRule rule2 = new PostRule();
        rule2.additionalWritableStatNames = new ArrayList<>(additionalWritableStatNames);
        rule2.readOnlyStatNames = new ArrayList<>(readOnlyStatNames);
        rule2.rule = rule;
        final Stat newStat = getOrCreateStat(postStatName);
        newStat.postAggRules.add(rule2);
        rule2.additionalWritableStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
        rule2.readOnlyStatNames.stream().forEach(inputStatName -> getOrCreateStat(inputStatName));
    }
    
    public void initializeGraphAndInverseGraph() {
        final Function<String, Set<String>> next = name -> {
            final Stat stat = stats.get(name);
            final Set<String> r = new LinkedHashSet<>();
            stat.inputRules.forEach(rule -> r.addAll(rule.inputStatNames));
            stat.preAggRules.forEach(rule -> r.addAll(rule.readOnlyStatNames));
            stat.postAggRules.forEach(rule -> r.addAll(rule.readOnlyStatNames));
            return r;
        };
        inverseGraph = Graph.fromRoots(stats.keySet(), next);
        
        //
        graph = inverseGraph.inverse();
        
        //
        for (final Stat stat : stats.values()) {
            for (final PostRule rule : stat.preAggRules) {
                for (final String writableInputStatName : rule.additionalWritableStatNames) {
                    for (final String downstreamStatName : new ArrayList<>(graph.edges(writableInputStatName))) {
                        if (stat.name().equals(downstreamStatName))
                            continue;
                        graph.addEdge(stat.name(), downstreamStatName);
                        inverseGraph.addEdge(downstreamStatName, stat.name());
                    }
                }
            }
            for (final PostRule rule : stat.postAggRules) {
                for (final String writableInputStatName : rule.additionalWritableStatNames) {
                    for (final String downstreamStatName : new ArrayList<>(graph.edges(writableInputStatName))) {
                        if (stat.name().equals(downstreamStatName))
                            continue;
                        graph.addEdge(stat.name(), downstreamStatName);
                        inverseGraph.addEdge(downstreamStatName, stat.name());
                    }
                }
            }
        }
        
        //
        for (final Stat stat : stats.values()) {
            for (final PostRule rule : stat.preAggRules) {
                for (final String writableInputStatName : rule.additionalWritableStatNames) {
                    graph.addEdge(writableInputStatName, stat.name());
                    inverseGraph.addEdge(stat.name(), writableInputStatName);
                }
            }
            for (final PostRule rule : stat.postAggRules) {
                for (final String writableInputStatName : rule.additionalWritableStatNames) {
                    graph.addEdge(writableInputStatName, stat.name());
                    inverseGraph.addEdge(stat.name(), writableInputStatName);
                }
            }
        }
        
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
