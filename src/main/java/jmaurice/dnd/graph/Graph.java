package jmaurice.dnd.graph;

import java.util.ArrayList;
import java.util.Arrays;
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

public class Graph<Node, EdgeValue> {
    
    private Map<Node, Map<Node, EdgeValue>> edges;
    
    public static <Node, EdgeValue> Graph<Node, EdgeValue> fromRoots(
            final Collection<Node> roots, 
            final Function<Node, Map<Node, EdgeValue>> next
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
    
    public static <Node, EdgeValue> Graph<Node, EdgeValue> fromRoots(
            final Collection<Node> roots, 
            final Function<Node, Map<Node, EdgeValue>> edges,
            final Executor executor
            ) throws InterruptedException {
        final Graph<Node, EdgeValue> graph = new Graph<>();
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
    
    private static <Node, EdgeValue> void fromRoots2(
            final Function<Node, Map<Node, EdgeValue>> edges,
            final Executor executor,
            final Graph<Node, EdgeValue> graph,
            final Node node, 
            final Semaphore completion,
            final List<RuntimeException> errors,
            final AtomicInteger count
            ) {
        try {
            if ( ! errors.isEmpty())
                return;
            class Pointer { Map<Node, EdgeValue> x; }
            final Pointer first = new Pointer();
            graph.edges.compute(node, (k,v) -> {
                if (v != null)
                    return v;
                final Map<Node, EdgeValue> nextList;
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
                for (final Map.Entry<Node, EdgeValue> nextNode : first.x.entrySet()) {
                    count.incrementAndGet();
                    executor.execute(() -> fromRoots2(edges, executor, graph, nextNode.getKey(), completion, errors, count));
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
        return Collections.unmodifiableSet(
                edges.entrySet().stream()
                .filter(x -> x.getValue().isEmpty())
                .map(x -> x.getKey())
                .collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public Map<Node, EdgeValue> edges(final Node node) {
        final Map<Node, EdgeValue> nodeOutEdges = edges.get(node);
        if (nodeOutEdges == null) {
            throw new IllegalStateException(
                    "Node missing in edges [" + node + "]."
                    + " Graph nodes " + new TreeSet<>(edges.keySet()) + ".");
        }
        return Collections.unmodifiableMap(nodeOutEdges);
    }
    
    public void forEach(final BiConsumer<Node, Map<Node, EdgeValue>> action) {
        for (final Map.Entry<Node, Map<Node, EdgeValue>> x : edges.entrySet()) {
            action.accept(x.getKey(), Collections.unmodifiableMap(x.getValue()));
        }
    }

    public Graph<Node, EdgeValue> makeInverse() {
        final Graph<Node, EdgeValue> inverse = new Graph<>();
        inverse.edges = new LinkedHashMap<>();
        for (final Node node : edges.keySet()) {
            inverse.edges.put(node, new LinkedHashMap<>());
        }
        for (final Map.Entry<Node, Map<Node, EdgeValue>> node0 : edges.entrySet()) {
            final Node node = node0.getKey();
            for (final Map.Entry<Node, EdgeValue> nodeEdge : node0.getValue().entrySet()) {
                final Map<Node, EdgeValue> inverseOutNodeEdges = inverse.edges.get(nodeEdge.getKey());
                inverseOutNodeEdges.put(node, nodeEdge.getValue());
            }
        }
        return inverse;
    }

    public void addEdge(Node node, Node out) {
        addEdge(node, out, null);
    }
    public void addEdge(Node node, Node out, EdgeValue edgeValue) {
        if (edges.get(node) == null)
            throw new IllegalArgumentException("No such node: " + node);
        if (edges.get(out) == null)
            throw new IllegalArgumentException("No such node: " + out);
        edges.get(node).put(out, edgeValue);
    }

    public Graph<Node, EdgeValue> makeClone(final Function<EdgeValue, EdgeValue> cloneEdgeValue) {
        final Graph<Node, EdgeValue> clone = new Graph<>();
        for (final Map.Entry<Node, Map<Node, EdgeValue>> node0 : edges.entrySet()) {
            final Node node = node0.getKey();
            final Map<Node, EdgeValue> edges = node0.getValue();
            final Map<Node, EdgeValue> edgesClone = new LinkedHashMap<>();
            edges.entrySet().stream().forEach(x -> edgesClone.put(x.getKey(), cloneEdgeValue.apply(x.getValue())));
            clone.edges.put(node, edgesClone);
                    
        }
        return clone;
    }
    
    public static class ClosureEdgeValue<Node> {
        public List<Node> shortestPath;
    }
    
    public Graph<Node, ClosureEdgeValue<Node>> makeClosure() {
        final Graph<Node, ClosureEdgeValue<Node>> closure = new Graph<>();
        closure.edges = new LinkedHashMap<>();
        //add initial edges
        for (final Map.Entry<Node, Map<Node, EdgeValue>> node0 : edges.entrySet()) {
            final Node node = node0.getKey();
            final Map<Node, ClosureEdgeValue<Node>> newEdges = new LinkedHashMap<>();
            for (final Node out : node0.getValue().keySet()) {
                final ClosureEdgeValue<Node> newEdge = new ClosureEdgeValue<>();
                if (node.equals(out)) {
                    newEdge.shortestPath = Arrays.asList(node); //a cycle; the node has an edge to itself
                } else {
                    newEdge.shortestPath = Arrays.asList(node, out);
                }
                newEdges.put(out, newEdge);
            }
            closure.edges.put(node, newEdges);
        }
        //do closure
        final List<Node> nodes = new ArrayList<>(closure.edges.keySet());
        for (int i = 0; i < nodes.size(); ++i) {
            final Node node1 = nodes.get(i);
            final Map<Node, ClosureEdgeValue<Node>> node1Edges = closure.edges.get(node1);
            for (int j = 0; j < i; ++j) {
                final Node node2 = nodes.get(j);
                final Map<Node, ClosureEdgeValue<Node>> node2Edges = closure.edges.get(node2);
                addClosureEdges(closure, node1, node2, node1Edges, node2Edges);
                addClosureEdges(closure, node2, node1, node2Edges, node1Edges);
            }
        }
        return closure;
    }
    
    private static <Node> void addClosureEdges(
                final Graph<Node, ClosureEdgeValue<Node>> closure, 
                final Node node1,
                final Node node2, 
                final Map<Node, ClosureEdgeValue<Node>> node1PartialClosureEdges,
                final Map<Node, ClosureEdgeValue<Node>> node2PartialClosureEdges
                ) {
        final ClosureEdgeValue<Node> pathN1toN2 = node1PartialClosureEdges.get(node2);
        if (pathN1toN2 != null) {
            for (final ClosureEdgeValue<Node> pathN2toN3 : node2PartialClosureEdges.values()) {
                final Node node3 = pathN2toN3.shortestPath.get(pathN2toN3.shortestPath.size() - 1);
                ClosureEdgeValue<Node> pathN1toN3 = node1PartialClosureEdges.get(node3);
                if (pathN1toN3 == null || pathN1toN3.shortestPath.size() > 
                        pathN1toN2.shortestPath.size() + pathN2toN3.shortestPath.size() - 1) {
                    pathN1toN3 = new ClosureEdgeValue<>();
                    pathN1toN3.shortestPath = new ArrayList<>();
                    pathN1toN3.shortestPath.addAll(pathN1toN2.shortestPath);
                    pathN1toN3.shortestPath.addAll(pathN2toN3.shortestPath.subList(1, pathN2toN3.shortestPath.size()));
                    node1PartialClosureEdges.put(node3, pathN1toN3);
                }
            }
        }
    }

    public List<Node> findShortestCycle() {
        return makeClosure().edges.values().stream()
                .flatMap(x -> x.values().stream())
                .map(x -> x.shortestPath)
                .filter(x -> x.get(0).equals(x.get(x.size() - 1)))
                .min((x,y) -> Integer.compare(x.size(), y.size()))
                .orElse(null);
    }
    
}
