package jmaurice.dnd.graph;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Traversal {

    public static <Node> void ordered(
            final Graph<Node> graph,
            final Graph<Node> inverse,
            final Consumer<Node> action,
            final Executor executor
            ) throws InterruptedException {
        final Map<Node, Integer> remaining = new ConcurrentHashMap<>();
        inverse.forEach((node, edges) -> remaining.put(node, edges.size()));
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
        
        for (final Node root : inverse.leafs()) {
            count.incrementAndGet();
            executor.execute(() -> ordered2(graph, action, executor, root, remaining, completion, errors, count));
        }
        count.decrementAndGet();
        if (0 == count.get()) {
            completion.release();
        }
        completion.acquire();
        if ( ! errors.isEmpty()) {
            final RuntimeException e1 = new RuntimeException();
            for (final RuntimeException e2 : errors) {
                e1.addSuppressed(e2);
            }
            throw e1;
        }
    }
    
    private static <Node> void ordered2(
            final Graph<Node> graph,
            final Consumer<Node> action,
            final Executor executor,
            final Node node,
            final Map<Node, Integer> remaining,
            final Semaphore completion,
            final List<RuntimeException> errors,
            final AtomicInteger count
            ) {
        try {
            try {
                action.accept(node);
            } catch (Exception e) {
                errors.add(new RuntimeException("Error while running action on node: " + node + ". Error: " + e.getMessage(), e));
            }
            if ( ! errors.isEmpty())
                return;
            for (final Node nextNode : graph.edges(node)) {
                if (0 == remaining.compute(nextNode, (k,v) -> --v)) {
                    count.incrementAndGet();
                    executor.execute(() -> ordered2(graph, action, executor, nextNode, remaining, completion, errors, count));
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

}
