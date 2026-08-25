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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsExecutor {
    
    private AtomicInteger enqueuedCount = new AtomicInteger();
    private Semaphore finished = new Semaphore(0);
    private List<Throwable> errors = new ArrayList<>();
    private final Stats stats;
    private final ExecutorService executor = Executors.newFixedThreadPool(16);
    
    public StatsExecutor(final Stats stats) {
        this.stats = stats;
    }
    
    public void execute() {
        try (Closeable shutdownExecutor = () -> shutdownAndWait(executor, 1, TimeUnit.MINUTES)) {
            for (final Stat stat : stats.stats().values().stream().filter(x -> x.inputStats.isEmpty()).toList()) {
//                synchronized (this) {
//                    System.out.println(Thread.currentThread().getName() + ": "
//                            + "Enqueued count " + enqueuedCount.get()
//                            + ". Enqueuing Stat " + stat.name);
//                }
                enqueuedCount.incrementAndGet();
                executor.submit(() -> execute(stat));
            }
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Just after submitting all initial Stats");
//            }
            finished.tryAcquire(1, 1, TimeUnit.MINUTES);
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Just after finished.tryAcquire");
//            }
            executor.awaitTermination(1, TimeUnit.MINUTES);
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Just after executor.awaitTermination");
//            }
            if (errors.size() > 0) {
                final RuntimeException e = new RuntimeException();
                errors.forEach(x -> e.addSuppressed(x));
                throw e;
            }
            final List<Stat> unstartedStats = stats.stats().values().stream().filter(x -> ! x.started).toList();
            if (unstartedStats.size() > 0)
                throw new RuntimeException("unstarted stats: " + unstartedStats.stream().map(x -> x.name).toList());
            final List<Stat> unfinishedStats = stats.stats().values().stream().filter(x -> ! x.finished).toList();
            if (unfinishedStats.size() > 0)
                throw new RuntimeException("unfinished stats: " + unfinishedStats.stream().map(x -> x.name).toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void shutdownAndWait(final ExecutorService executor, final int timeout, TimeUnit unit) {
        executor.shutdownNow(); 
        try {
            executor.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    
    private void execute(final Stat stat) {
        try {
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Starting " + stat.name);
//            }
            synchronized (stat) {
                if (stat.started)
                    throw new RuntimeException();
                stat.started = true;
            }
            for (final InputRule rule : stat.inputRules) {
                execute(stat, rule);
            }
            if (stat.aggRule == null) {
                if (stat.values.size() >= 2)
                    throw new RuntimeException("implicit agg rule failed for stat " + stat.name + " because there are two or more values: " + stat.values);
            } else {
                execute(stat, stat.aggRule);
            }
            if (stat.postRule != null) {
                execute(stat, stat.postRule);
            }
            synchronized (stat) {
                stat.finished = true;
            }
            for (final Stat outputStat : stat.outputStats) {
                synchronized (outputStat) {
                    if ( ! outputStat.inputStats.remove(stat))
                        throw new RuntimeException();
                    if (outputStat.inputStats.isEmpty()) {
//                        synchronized (this) {
//                            System.out.println(Thread.currentThread().getName() + ": "
//                                    + "Enqueued count " + enqueuedCount.get()
//                                    + ". Finishing Stat " + stat.name
//                                    + ". Enqueuing output Stat " + outputStat.name
//                                    );
//                        }
                        enqueuedCount.incrementAndGet();
                        executor.submit(() -> execute(outputStat));
                    } else {
//                        synchronized (this) {
//                            System.out.println(Thread.currentThread().getName() + ": "
//                                    + "Enqueued count " + enqueuedCount.get()
//                                    + ". Finishing Stat " + stat.name
//                                    + ". Output Stat " + outputStat.name
//                                    + ". Output Stat remaining inputs: " + outputStat.inputStats.stream().map(x -> x.name).toList()
//                                    );
//                        }
                    }
                }
            }
            enqueuedCount.decrementAndGet();
//            synchronized (this) {
//                System.out.println(Thread.currentThread().getName() + ": "
//                        + "Enqueued count " + enqueuedCount.get()
//                        + ". Finished " + stat.name);
//            }
            if (0 == enqueuedCount.get()) {
                finished.release();
                executor.shutdown();
            }
        } catch (Exception | AssertionError e) {
            synchronized (errors) {
                errors.add(new RuntimeException("failed execute for Stat " + stat.name + ". Cause: " + e.getMessage(), e));
            }
            synchronized (stat) {
                stat.finished = true;
            }
            enqueuedCount.decrementAndGet();
            if (0 == enqueuedCount.get()) {
                finished.release();
                executor.shutdown();
            }
            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + ": "
                        + "Enqueued count " + enqueuedCount.get()
                        + "> failed execute for Stat " + stat.name);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    
    private void execute(final Stat stat, final InputRule rule) {
        final Map<String, ReadOnlyStat> input = new LinkedHashMap<>();
        rule.inputStatNames.forEach(name -> input.put(name, newReadOnlyStat(stats.stats().get(name))));
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
    
    private void execute(final Stat stat, final AggRule rule) {
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

    private void execute(final Stat stat, final PostRule rule) {
        //PostRules require some mutual exclusion because they might modify the same stat at the same time.
        //I could be fancier with my locking and be more fine-grain, but this is fine for now.
        synchronized (this) {
            final Map<String, Stat> writeableStats = new LinkedHashMap<>();
            rule.postStatNames.forEach(name -> writeableStats.put(name, stats.stats().get(name)));
            final Map<String, ReadOnlyStat> readOnlyStats = new LinkedHashMap<>();
            rule.otherInputStatNames.forEach(name -> readOnlyStats.put(name, newReadOnlyStat(stats.stats().get(name))));
            rule.rule.accept(writeableStats, readOnlyStats);
        }
    }

    private static ReadOnlyStat newReadOnlyStat(Stat stat) {
        return new ReadOnlyStat() {
            @Override public String name() { return stat.name; }
            @Override public List<Value> getValues() { return stat.getValues(); }
            @Override public Optional<Value> val01() { return stat.val01(); }
            @Override public Value val1() { return stat.val1(); }
            @Override public String toString() { return "readOnlyStat(" + stat.name() + "=" + stat.getValues() + ")"; }
        };
    }

}
