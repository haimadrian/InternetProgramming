package org.hit.internetprogramming.eoh.common.graph;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a matrix that adapts the Graph API.<br/>
 * The implementation is according to the Adapter pattern, where we implement one interface and holds a data member of another type.<br/>
 * In matrices, reachable vertices are neighbors that have {@link IMatrix#hasValue(Index) value} at their location.
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see IGraph
 * @see IMatrix
 */
public class MatrixGraphAdapter<T> implements IGraph<Index> {
   /**
    * The underlying matrix, which will adapt the Graph API.
    */
   private final IMatrix<T> matrix;

   /**
    * Root of the graph. Must be a valid {@link Index} at the specified matrix.
    */
   private final Index root;

   /**
    * Constructs a new {@link MatrixGraphAdapter}
    * @param matrix The underlying matrix, which will adapt the Graph API.
    * @param root Root of the graph. Must be a valid {@link Index} at the specified matrix.
    */
   @JsonCreator
   public MatrixGraphAdapter(@JsonProperty("matrix") IMatrix<T> matrix, @JsonProperty("root") Index root) {
      this.matrix = matrix;
      this.root = root;
   }

   /**
    * A constructor used to copy another graph's matrix, and use a new root.<br/>
    * This constructor is used by algorithms that start their work from a graph's root, and
    * we do not want to modify the original graph's root.<br/>
    * Note that the underlying matrix is the same one, and not a copy.
    * @param graph The graph to copy
    * @param newRoot A root to use as the new root
    */
   public MatrixGraphAdapter(IGraph<T> graph, Index newRoot) {
      this.matrix = ((MatrixGraphAdapter<T>)graph).matrix;
      this.root = newRoot;
   }

   @Override
   public Index getRoot() {
      return root;
   }

   @Override
   public List<Index> getAdjacentVertices(Index vertex) {
      return matrix.neighbors(vertex);
   }

   @Override
   public List<Index> getReachableVertices(Index vertex) {
      if (matrix.hasValue(vertex)) {
         // Collect neighbors that contain some value in them. For binary matrices, this will collect neighbors with value=1 only.
         return matrix.neighbors(vertex).stream().filter(matrix::hasValue).collect(Collectors.toList());
      } else {
         // If there is no vertex at the specified index, return empty list
         return new ArrayList<>();
      }
   }

   @Override
   public String printGraph() {
      return matrix.printMatrix().replace("null", " ");
   }

   @Override
   public String toString() {
      return matrix.toString().replace("null", " ");
   }

   @Override
   public int getGraphSize() {
      return matrix.rows() * matrix.cols();
   }

   @Override
   public List<Index> getVertices() {
      List<Index> vertices = new ArrayList<>();

      for (int i = 0; i < matrix.rows(); i++) {
         for (int j = 0; j < matrix.cols(); j++) {
            Index currVertex = Index.from(i, j);
            if (matrix.hasValue(currVertex)) {
               vertices.add(currVertex);
            }
         }
      }

      return vertices;
   }

   @Override
   public List<Pair<Index, Index>> getEdges() {
      List<Index> vertices = getVertices();
      List<Pair<Index, Index>> edges = new ArrayList<>(vertices.size());

      for (Index vertex : vertices) {
         for (Index neighbor : getReachableVertices(vertex)) {
            edges.add(MutablePair.of(vertex, neighbor));
         }
      }

      return edges;
   }

   @Override
   public boolean contains(Index vertex) {
      return matrix.hasValue(vertex);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T getValue(Index vertex) {
      return matrix.getValue(vertex);
   }
}
