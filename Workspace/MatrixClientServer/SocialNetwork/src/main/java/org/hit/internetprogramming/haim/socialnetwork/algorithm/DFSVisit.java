package org.hit.internetprogramming.haim.socialnetwork.algorithm;

import org.hit.internetprogramming.haim.socialnetwork.data.Node;

import java.util.*;
import java.util.stream.Collectors;

public class DFSVisit<T> {
    protected final ThreadLocal<Stack<Node<T>>> workingStack = ThreadLocal.withInitial(Stack::new);
    protected final ThreadLocal<Set<Node<T>>> visitedNodes = ThreadLocal.withInitial(HashSet::new);

    /**
     * Algorithm:<br/>
     * <pre>{@code
     * push origin of the traversable to the stack V
     * while stack is not empty: V
     *     removed = pop operation V
     *     insert to finished V
     *     invoke getReachableNodes on the removed element V
     *     for each reachable node: V
     *         if the current reachable node is not in finished set && is not in workingStack
     *             push
     * }</pre>
     * @param traversable The traversable to traverse
     * @return Visited vertices
     */
    public List<T> traverse(Traversable<T> traversable) {
        Stack<Node<T>> workingStack = this.workingStack.get();
        Set<Node<T>> visitedNodes = this.visitedNodes.get();

        workingStack.clear();
        visitedNodes.clear();

        Node<T> currNode = traversable.getOrigin();
        workingStack.push(currNode);
        visitedNodes.add(currNode);

        while (!workingStack.isEmpty()) {
            currNode = workingStack.pop();
            Collection<Node<T>> reachableVertices = traversable.getReachableNodes(currNode);

            // @formatter:off
            reachableVertices.forEach(node -> {
                // Ensure we push each vertex once, to avoid a situation where we push
                // a vertex, and then pushes its neighbors, although they are already in the stack.
                if (!visitedNodes.contains(node)) {
                    visitedNodes.add(node);
                    workingStack.push(node);
                }
            });
            // @formatter:on
        }

        return visitedNodes.stream().map(Node::getData).collect(Collectors.toList());
    }
}
