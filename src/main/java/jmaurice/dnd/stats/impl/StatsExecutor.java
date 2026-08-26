package jmaurice.dnd.stats.impl;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jmaurice.dnd.graph.Traversal;

public class StatsExecutor {
    
    private Stats stats;
    private Map<String, ValuedStat> valued;
    
    public static void execute(final Stats stats, final Map<String, ValuedStat> valuedStats) {
        final StatsExecutor x = new StatsExecutor();
        x.stats = stats;
        x.valued= valuedStats;
        x.execute2();
    }
    
    private void execute2() {
        valued.get("default").addInitialValue(new Value("true", "default"));
        final ExecutorService executor = Executors.newFixedThreadPool(16);
        try (Closeable shutdownExecutor = () -> shutdownAndWait(executor, 1, TimeUnit.MINUTES)) {
            Traversal.ordered(stats.graph(), stats.inverseGraph(), name -> execute(valued.get(name)), executor);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void shutdownAndWait(final ExecutorService executor, final int timeout, TimeUnit unit) {
        executor.shutdownNow(); 
        try {
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    
    private void execute(final ValuedStat stat) {
        try {
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Starting " + stat.name);
//            }
            for (final InputRule rule : stat.stat.inputRules) {
                execute(stat, rule);
            }
            if (stat.stat.aggRule == null) {
                if (stat.values.size() >= 2)
                    throw new RuntimeException("implicit agg rule failed for stat " + stat.stat.name + " because there are two or more values: " + stat.values);
            } else {
                execute(stat, stat.stat.aggRule);
            }
            if (stat.stat.postRule != null) {
                execute(stat, stat.stat.postRule);
            }
        } catch (Exception e) {
            throw new RuntimeException("failed execute for Stat " + stat.stat.name + ". Cause: " + e.getMessage(), e);
        }
    }
    
    private void execute(final ValuedStat stat, final InputRule rule) {
        final Map<String, ReadOnlyValuedStat> input = new LinkedHashMap<>();
        rule.inputStatNames.forEach(name -> input.put(name, newReadOnlyStat(valued.get(name))));
//        synchronized (this) {
//            System.out.println(Thread.currentThread().getName() + ": "
//                    + "Executing Stat " + stat.name
//                    + ". Starting InputRule"
//                    + ". input: " + input.values()
//                    );
//        }
        final List<Value> output = rule.rule.apply(input);
//        synchronized (this) {
//            System.out.println(Thread.currentThread().getName() + ": "
//                    + "Executing Stat " + stat.name
//                    + ". Finished InputRule"
//                    + ". Output: " + output
//                    );
//        }
        if (output != null)
            stat.values.addAll(output);
    }
    
    private void execute(final ValuedStat stat, final AggRule rule) {
//        synchronized (this) {
//            System.out.println(Thread.currentThread().getName() + ": "
//                    + "Executing Stat " + stat.name
//                    + ". Starting AggRule"
//                    + ". input: " + stat.values
//                    );
//        }
        final List<Value> output = rule.rule.apply(stat.values);
//        synchronized (this) {
//            System.out.println(Thread.currentThread().getName() + ": "
//                    + "Executing Stat " + stat.name
//                    + ". Finished AggRule"
//                    + ". output: " + output
//                    );
//        }
        if (output == null)
            stat.values = new ArrayList<>();
        else
            stat.values = new ArrayList<>(output);
    }

    private void execute(final ValuedStat stat, final PostRule rule) {
        //PostRules require some mutual exclusion because they might modify the same stat at the same time.
        //I could be fancier with my locking and be more fine-grain, but this is fine for now.
        synchronized (this) {
            final Map<String, ValuedStat> writableStats = new LinkedHashMap<>();
            rule.writableStatNames.forEach(name -> writableStats.put(name, valued.get(name)));
            final Map<String, ReadOnlyValuedStat> readOnlyStats = new LinkedHashMap<>();
            rule.readOnlyStatNames.forEach(name -> readOnlyStats.put(name, newReadOnlyStat(valued.get(name))));
            rule.rule.accept(writableStats, readOnlyStats);
        }
    }

    private ReadOnlyValuedStat newReadOnlyStat(final ValuedStat stat) {
        return new ReadOnlyValuedStat() {
            @Override public String name() { return stat.stat.name; }
            @Override public List<Value> getValues() { return stat.getValues(); }
            @Override public Optional<Value> val01() { return stat.val01(); }
            @Override public Value val1() { return stat.val1(); }
            @Override public String toString() { return "readOnlyStat(" + stat.stat.name + "=" + stat.getValues() + ")"; }
        };
    }
    
}
