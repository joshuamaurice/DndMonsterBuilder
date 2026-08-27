package jmaurice.dnd.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Graph<Node> {
    
    private Map<Node, Set<Node>> edges;
    
    public static <Node> Graph<Node> fromRoots(
            final Collection<Node> roots, 
            final Function<Node, Set<Node>> next
            ) {
        try {
            final Executor executor = ForkJoinPool.commonPool();
            return fromRoots(roots, next, executor);
        } catch (final InterruptedException e) {
            //Should not happen.
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
    
    public static <Node> Graph<Node> fromRoots(
            final Collection<Node> roots, 
            final Function<Node, Set<Node>> edges,
            final Executor executor
            ) throws InterruptedException {
        final Graph<Node> graph = new Graph<>();
        graph.edges = new ConcurrentHashMap<>();
        final Semaphore completion = new Semaphore(0);
        final List<RuntimeException> errors = new CopyOnWriteArrayList<>();
        
        //We need to increment the count one extra time here,
        //and decrement it after the initial loop in this method,
        //to avoid a really nasty race condition.
        //Consider: If the first root has no out-nodes,
        //then that task might start and finish before the other tasks can even be registered in this method,
        //which will lead to the count being decremented for the first task
        //before we get to the increment of the second task.
        final AtomicInteger count = new AtomicInteger(1);
        
        for (final Node root : roots) {
            count.incrementAndGet();
            executor.execute(() -> fromRoots2(edges, executor, graph, root, completion, errors, count));
        }
        count.decrementAndGet();
        if (0 == count.get()) {
            completion.release();
        }
        completion.acquire();
        return graph;
    }
    
    private static <Node, Value> void fromRoots2(
            final Function<Node, Set<Node>> edges,
            final Executor executor,
            final Graph<Node> graph,
            final Node node, 
            final Semaphore completion,
            final List<RuntimeException> errors,
            final AtomicInteger count
            ) {
        try {
            if ( ! errors.isEmpty())
                return;
            class Pointer { Set<Node> x; }
            final Pointer first = new Pointer();
            graph.edges.compute(node, (k,v) -> {
                if (v != null)
                    return v;
                final Set<Node> nextList;
                try {
                    nextList = edges.apply(node);
                } catch (Exception e) {
                    throw new RuntimeException("Error while getting edges for node: " + node + ". Error: " + e.getMessage(), e);
                }
                if (nextList == null)
                    throw new NullPointerException("null edges returned for node: " + node);
                first.x = nextList;
                return nextList;
            });
            if (first.x != null) {
                for (final Node nextNode : first.x) {
                    count.incrementAndGet();
                    executor.execute(() -> fromRoots2(edges, executor, graph, nextNode, completion, errors, count));
                }
            }
        } catch (Exception e) {
            errors.add(new RuntimeException("Error while running node: " + node + ". Error: " + e.getMessage(), e));
        } finally {
            final int c = count.decrementAndGet();
            if (c == 0) {
                completion.release();
            }
        }
    }

    
    public Set<Node> nodes() {
        return Collections.unmodifiableSet(edges.keySet());
    }
    
    public Set<Node> leafs() {
        return edges.entrySet().stream().filter(x -> x.getValue().isEmpty()).map(x -> x.getKey()).collect(Collectors.toSet());
    }

    public Set<Node> edges(final Node node) {
        final Set<Node> x = edges.get(node);
        if (x == null) {
            throw new IllegalStateException(
                    "Node missing in edges [" + node + "]."
                    + " Graph nodes " + new TreeSet<>(edges.keySet()) + ".");
        }
        return Collections.unmodifiableSet(x);
    }
    
    public void forEach(final BiConsumer<Node, Set<Node>> action) {
        for (final Map.Entry<Node, Set<Node>> x : edges.entrySet()) {
            action.accept(x.getKey(), Collections.unmodifiableSet(x.getValue()));
        }
    }

    public Graph<Node> inverse() {
        final Graph<Node> inverse = new Graph<>();
        inverse.edges = new LinkedHashMap<>();
        for (final Node node : edges.keySet()) {
            inverse.edges.put(node, new LinkedHashSet<>());
        }
        for (final Map.Entry<Node, Set<Node>> node : edges.entrySet()) {
            final Node node2 = node.getKey();
            for (final Node out : node.getValue()) {
                final Set<Node> outEdgesSet = inverse.edges.get(out);
                if (outEdgesSet == null) {
                    throw new IllegalStateException(
                            "Node missing in inverse edges [" + out + "]."
                            + " Graph nodes " + new TreeSet<>(edges.keySet()) + "."
                            + " Inverse graph nodes " + new TreeSet<>(inverse.edges.keySet()) + ".");
                }
                outEdgesSet.add(node2);
            }
        }
        return inverse;
    }

    public void addEdge(Node node, Node out) {
        if (edges.get(node) == null)
            throw new IllegalArgumentException("No such node: " + node);
        if (edges.get(out) == null)
            throw new IllegalArgumentException("No such node: " + out);
        edges.get(node).add(out);
    }

}
