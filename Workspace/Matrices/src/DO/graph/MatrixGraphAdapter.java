package DO.graph;


import DO.mat.IMatrix;
import DO.mat.Index;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a matrix that adapts the Graph API.<br/>
 * The implementation is according to the Adapter pattern, where we implement one interface and holds a data member of another type.<br/>
 * In matrices, adjacent vertices are neighbors that their value is 1, and not 0.
 */
public class MatrixGraphAdapter<T> implements IGraph<Index> {
   private final IMatrix<T> matrix;
   private final Index root;

   /**
    * Constructs a new {@link MatrixGraphAdapter}
    * @param matrix
    * @param root
    */
   public MatrixGraphAdapter(IMatrix<T> matrix, Index root) {
      this.matrix = matrix;
      this.root = root;
   }

   @Override
   public Index getRoot() {
      return root;
   }

   @Override
   public Collection<Index> getAdjacentVertices(Index vertex) {
      if (matrix.hasValue(vertex)) {
         // Collect neighbors that contain some value in them. For binary matrices, this will collect neighbors with value=1 only.
         return matrix.neighbors(vertex).stream().filter(matrix::hasValue).collect(Collectors.toList());
      } else {
         return new ArrayList<>(); // If there is no vertex at the specified index, return empty list
      }
   }

   @Override
   public String printGraph() {
      return matrix.printMatrix().replace("0", " ");
   }

   @Override
   public String toString() {
      return matrix.toString().replace("0", " ");
   }

   @Override
   public Collection<Collection<Index>> findPaths(Index from, Index to) {
      Collection<Collection<Index>> paths = new ArrayList<>();

      if (matrix.hasValue(from) && matrix.hasValue(to)) {
         paths.addAll(findPaths(from, to, new HashSet<>()).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList()));
      }

      return paths;
   }

   /**
    * A helper method we use in order to find all sub paths from some vertex to destination.<br/>
    * We use a set in order to make sure we do not traverse in circles.<br/>
    * The method will never return null. In case there is no path to destination, the response will be empty.
    * @param from From which vertex to find paths until we reach to destination.
    * @param to The vertex to get to.
    * @param visited Set of vertices to fill in with vertices in each path, so we will not traverse in circles.
    * @return Collection of all paths to destination vertex, or empty if we could not reach to destination.
    */
   private List<List<Index>> findPaths(Index from, Index to, Set<Index> visited) {
      List<List<Index>> paths = new ArrayList<>();

      // In case from and to are the same vertex, return it as the path.
      if (from.equals(to)) {
         // Create a new one and not Arrays.as, cause we need a writable collection
         List<Index> path = new ArrayList<>();
         path.add(from);
         paths.add(path);
      } else {
         // Create a new set so one path will not affect other paths in the recursive calls. (Each path is a different sub-tree)
         Set<Index> newSet = extendSetWith(visited, from);
         Collection<Index> adjacentVertices = getAdjacentVertices(from);

         for (Index adjacentVertex : adjacentVertices) {
            // Avoid from traversing in circles
            if (!visited.contains(adjacentVertex)) {
               paths.addAll(findPaths(adjacentVertex, to, newSet).stream().filter(path -> !path.isEmpty()).collect(Collectors.toList()));
            }
         }

         // For each non-empty path, add 'from' as the first node in the path.
         paths.forEach(currPath -> currPath.add(0, from));
      }

      return paths;
   }

   /**
    * When we traverse a graph and try to find path, we do not want to affect all paths when we are traversing one path,
    * so for each path we use a different set of 'visited' indices.
    * @param set
    * @param index
    * @return
    */
   private static Set<Index> extendSetWith(Set<Index> set, Index index) {
      Set<Index> newSet = new HashSet<>(set);
      newSet.add(index);
      return newSet;
   }
}
