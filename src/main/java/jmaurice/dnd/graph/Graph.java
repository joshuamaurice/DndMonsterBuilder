package jmaurice.dnd.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
        final AtomicInteger count = new AtomicInteger();
        for (final Node root : roots) {
            count.incrementAndGet();
            executor.execute(() -> fromRoots2(edges, executor, graph, root, completion, count));
        }
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
            final AtomicInteger count
            ) {
        class Pointer { Set<Node> x; }
        final Pointer first = new Pointer();
        graph.edges.compute(node, (k,v) -> {
            if (v != null)
                return v;
            final Set<Node> nextList = edges.apply(node);
            if (nextList == null)
                throw new NullPointerException("nextList");
            first.x = nextList;
            return nextList;
        });
        if (first.x != null) {
            for (final Node nextNode : first.x) {
                count.incrementAndGet();
                executor.execute(() -> fromRoots2(edges, executor, graph, nextNode, completion, count));
            }
        }
        if (0 == count.decrementAndGet()) {
            completion.release();
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
        if (x == null)
            throw new IllegalArgumentException("unknown node: " + node);
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
                if (outEdgesSet == null)
                    throw new IllegalStateException("node in inverse graph has null outEdgesSet: " + out);
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
