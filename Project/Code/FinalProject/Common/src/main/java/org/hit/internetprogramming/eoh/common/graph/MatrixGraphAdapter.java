package org.hit.internetprogramming.eoh.common.graph;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
      return matrix.printMatrix().replace("0", " ");
   }

   @Override
   public String toString() {
      return matrix.toString().replace("0", " ");
   }

   public IMatrix<T> getMatrix() {
      return matrix;
   }
}