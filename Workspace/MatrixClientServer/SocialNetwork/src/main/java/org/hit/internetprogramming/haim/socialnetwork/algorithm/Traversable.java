package org.hit.internetprogramming.haim.socialnetwork.algorithm;

import org.hit.internetprogramming.haim.socialnetwork.data.Node;

import java.util.Collection;

/**
 * This interface defines the functionality required for a traversable graph
 */
public interface Traversable<T> {
    public Node<T> getOrigin();
    public Collection<Node<T>> getReachableNodes(Node<T> someNode);
}
