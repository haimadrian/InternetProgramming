package DO.graph;

import java.util.Collection;

/**
 * This interface is the primary data type for Graph hierarchy<br/>
 * It represents the basic functionality required from a traversable graph.
 * @param <T>
 */
public interface IGraph<T> {
   T getRoot();
   Collection<T> getAdjacentVertices(T vertex);
   Collection<Collection<T>> findPaths(T from, T to);
   String printGraph();
}
