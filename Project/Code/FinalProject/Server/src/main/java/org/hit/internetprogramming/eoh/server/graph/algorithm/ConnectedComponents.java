package org.hit.internetprogramming.eoh.server.graph.algorithm;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that implements DFS algorithm in order to find an CC (Connected Component) in a graph.<br/>
 * Note that we find CC and not SCC! SCC is related to directed graphs, and we do not make sure that each vertex
 * that we visit, has a path to previously visited vertices. So basically this is only a visiting algorithm.<br/>
 * The class uses {@link ThreadLocal} for data structures, to let multiple threads find CCs in parallel.<br/>
 * That means you can call {@link #traverse(IGraph)}, and each time supply the same graph, using a different
 * root node, so each thread can find an CC relating to its defined root. This way, there can be a parallel
 * algorithm that finds all CCs in a graph.
 *
 * @param <T> Type of elements in an {@link IGraph} (We use the {@link org.hit.internetprogramming.eoh.common.mat.Index} class)
 * @author Orel Gershonovich
 * @since 10-Jul-21
 */
public class ConnectedComponents<T> {
    protected final ThreadLocal<Deque<T>> workingStack = ThreadLocal.withInitial(ArrayDeque::new);
    protected final ThreadLocal<Set<T>> visitedVertices = ThreadLocal.withInitial(LinkedHashSet::new);

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
     *
     * @param graph The graph to traverse
     * @return Visited vertices. (Connected component)
     */
    public List<Set<T>> traverse(IGraph<T> graph) {
        Deque<T> workingStack = new ArrayDeque<>();
        Set<T> visitedVertices = new HashSet<>();
        Set<T> unVisitedVertices;
        List<Set<T>> finalListWithAllCCAsSet = new ArrayList<>();

        T currVertex = graph.getRoot();
        workingStack.push(currVertex);
        visitedVertices.add(currVertex);

        // As long as we haven't reached nowhere (We scanned all reachable vertices)
        while (!workingStack.isEmpty()) {
            currVertex = workingStack.pop();
            Collection<T> reachableVertices = graph.getAdjacentVertices(currVertex);

            reachableVertices.forEach(vertex -> {
                // Ensure we push each vertex once, to avoid a situation where we push
                // a vertex, and then push its neighbors, although they are already in the stack.
                if (!visitedVertices.contains(vertex)) {
                    visitedVertices.add(vertex);
                    workingStack.push(vertex);
                }
            });
        }

        unVisitedVertices = visitedVertices.stream().filter(graph::contains).collect(Collectors.toSet());
        workingStack.clear();
        visitedVertices.clear();

        currVertex = graph.getRoot();
        workingStack.push(currVertex);
        visitedVertices.add(currVertex);

        // As long as we haven't reached nowhere (We scanned all reachable vertices)
        while (!workingStack.isEmpty() || !unVisitedVertices.isEmpty()) {
            if (workingStack.isEmpty()) {
                finalListWithAllCCAsSet.add(new HashSet<>(visitedVertices));
                visitedVertices.clear();

                currVertex = unVisitedVertices.iterator().next();
                unVisitedVertices.remove(currVertex);
                workingStack.push(currVertex);
                visitedVertices.add(currVertex);
            } else {
                currVertex = workingStack.pop();
                unVisitedVertices.remove(currVertex);

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

        finalListWithAllCCAsSet.add(new HashSet<>(visitedVertices));

        finalListWithAllCCAsSet.sort(Comparator.comparingInt(Set::size));
        return finalListWithAllCCAsSet;
    }
}
