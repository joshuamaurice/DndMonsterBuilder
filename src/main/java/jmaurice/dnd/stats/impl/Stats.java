package jmaurice.dnd.stats.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

import jmaurice.dnd.graph.Graph;

public class Stats {
    
    private Map<String, Stat> stats = new LinkedHashMap<>();
    private Graph<String, Void> graph;
    private Graph<String, Void> inverseGraph;
    
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
        final Function<String, Map<String, Void>> next = name -> {
            final Stat stat = stats.get(name);
            final Map<String, Void> r = new LinkedHashMap<>();
            stat.inputRules.forEach(rule -> rule.inputStatNames.forEach(n -> r.put(n, null)));
            stat.preAggRules.forEach(rule -> rule.readOnlyStatNames.forEach(n -> r.put(n, null)));
            stat.preAggRules.forEach(rule -> rule.additionalWritableStatNames.forEach(n -> r.put(n, null)));
            stat.postAggRules.forEach(rule -> rule.readOnlyStatNames.forEach(n -> r.put(n, null)));
            stat.postAggRules.forEach(rule -> rule.additionalWritableStatNames.forEach(n -> r.put(n, null)));
            return r;
        };
        inverseGraph = Graph.fromRoots(stats.keySet(), next);
        
        //
        graph = inverseGraph.makeInverse();
        
        //
        for (final Stat stat : stats.values()) {
            for (final String inputStatName : iterable(
                    stat.inputRules.stream().flatMap(x -> x.inputStatNames.stream()),
                    stat.preAggRules.stream().flatMap(x -> x.readOnlyStatNames.stream()),
                    stat.postAggRules.stream().flatMap(x -> x.readOnlyStatNames.stream())
                    )) {
                final Stat inputStat = getStat(inputStatName);
                for (final String postStatName : iterable(
                        inputStat.preAggRules.stream().flatMap(x -> x.additionalWritableStatNames.stream()),
                        inputStat.postAggRules.stream().flatMap(x -> x.additionalWritableStatNames.stream())
                        )) {
                    if (stat.name().equals(postStatName))
                        continue;
                    graph.addEdge(postStatName, stat.name());
                    inverseGraph.addEdge(stat.name(), postStatName);
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

    public Graph<String, Void> graph() { return graph; }
    public Graph<String, Void> inverseGraph() { return inverseGraph; }
    
    @SafeVarargs
    private static <E> Iterable<E> iterable(Stream<E>... inputs) {
        return Arrays.asList(inputs).stream().flatMap(x -> x).toList();
    }
    
}
