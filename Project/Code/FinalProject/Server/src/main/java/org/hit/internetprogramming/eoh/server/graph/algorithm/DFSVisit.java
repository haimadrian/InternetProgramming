package org.hit.internetprogramming.eoh.server.graph.algorithm;

import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that implements DFS algorithm in order to find an CC (Connected Component) in a graph.<br/>
 * Note that we find CC and not SCC! SCC is related to directed graphs, and we do not make sure that each vertex
 * that we visit, has a path to previously visited vertices. So basically this is only a visiting algorithm.<br/>
 * The class uses {@link ThreadLocal} for data structures, to let multiple threads find CCs in parallel.<br/>
 * That means you can call {@link #traverse(IGraph)}, and each time supply the same graph, using a different
 * root node, so each thread can find an CC relating to its defined root. This way, there can be a parallel
 * algorithm that finds all CCs in a graph.
 * <p>
 *     <b>Note:</b><br/>
 *     DFSVisit contains a state member we have implemented in order to improve the performance when calling
 *     {@link #traverse(IGraph)} over and over with different roots, such that DFSVisit will not scan the
 *     same connected component again and again. (This is very important when we have a 100x100 matrix or bigger.)
 * </p>
 * @param <T> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Nathan Dillbary, Haim Adrian
 * @since 20-May-21
 */
public class DFSVisit<T> {
    protected final ThreadLocal<Deque<T>> workingStack = ThreadLocal.withInitial(ArrayDeque::new);
    protected final ThreadLocal<Set<T>> visitedVertices = ThreadLocal.withInitial(LinkedHashSet::new);

    /**
     * A thread safe data structure that contains every visited vertex.<br/>
     * This helps us to avoid of a full-search from some vertex, when we already have its connected
     * component in memory.<br/>
     * This member is shared to all threads.
     */
    private final Set<T> allVisitedVertices = ConcurrentHashMap.newKeySet();

    /**
     * Algorithm:<br/>
     * <pre>{@code
     * Push root of the graph to the current thread's associated stack
     * While stack is not empty:
     *     currVertex = pop from stack.
     *     Insert currVertex to visited vertices set, associated to current thread.
     *     Invoke getReachableVertices on currVertex.
     *     For each reachableVertex do:
     *         If the current reachableVertex is not in visited set && not in stack
     *             push reachableVertex to stack
     * }</pre>
     * @param graph The graph to traverse
     * @return Visited vertices. (Connected component) or {@code null} when a visited vertex was already visited before
     */
    public List<T> traverse(IGraph<T> graph) {
        Deque<T> workingStack = this.workingStack.get();
        Set<T> visitedVertices = this.visitedVertices.get();

        workingStack.clear();
        visitedVertices.clear();

        T currVertex = graph.getRoot();

        workingStack.push(currVertex);
        visitedVertices.add(currVertex);

        // As long as we haven't reached nowhere (We scanned all reachable vertices)
        while (!workingStack.isEmpty()) {
            currVertex = workingStack.pop();

            // If we have already computed the connected component of this vertex, return null to tell the caller
            // that the relevant connected component is already in his hands, or it is currently being computed by another thread.
            // In addition, in case server was instructed to shutdown now, we should stop current execution.
            if (allVisitedVertices.contains(currVertex) || ActionThreadService.getInstance().isShutdownNow()) {
                return null;
            }

            Collection<T> reachableVertices = graph.getReachableVertices(currVertex);

            reachableVertices.forEach(vertex -> {
                // Ensure we push each vertex once, to avoid a situation where we push
                // a vertex, and then push its neighbors, although they are already in the stack.
                if (!visitedVertices.contains(vertex)) {
                    visitedVertices.add(vertex);
                    workingStack.push(vertex);
                }
            });
        }

        allVisitedVertices.addAll(visitedVertices);
        return new ArrayList<>(visitedVertices);
    }

    /**
     * Reset the state (clear all references) of this class
     */
    public void reset() {
        allVisitedVertices.clear();
    }
}
