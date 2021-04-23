package org.hit.internetprogramming.haim.matrix.common.mat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hit.internetprogramming.haim.matrix.common.graph.MatrixGraphAdapter;

import java.util.List;

/**
 * This interface represents a matrix of generic type T.<br/>
 * Matrix elements are retrievable and modifiable using an {@link Index} that tells a location
 * to retrieve / modify.
 * @param <T> The type of elements in the matrix
 * @author Haim Adrian
 * @since 04-Mar-21
 * @see StandardMatrix
 * @see CrossMatrix
 * @see org.hit.internetprogramming.haim.matrix.common.graph.IGraph
 */
// Store type info to json, so we can ease serialization / deserialization
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({ @JsonSubTypes.Type(value = StandardMatrix.class, name = "standardBinaryMatrix"),
    @JsonSubTypes.Type(value = CrossMatrix.class, name = "crossBinaryMatrix") })
public interface IMatrix<T> {
    /**
     * Checks if there is a value at the specified index.
     * @param index The index to check if there is value at
     * @return Whether there is a value or not.
     */
    boolean hasValue(Index index);

    /**
     * Returns the value at the specified index.<br/>
     * In case index refers to null, or it is out of bounds, the result will be null.
     * @param index The location of a value to get
     * @return The value at the specified location, or null in case location is illegal.
     */
    T getValue(Index index);

    /**
     * Set a value at the specified location.
     * @param index The location to set value at
     * @param value The value to set
     */
    void setValue(Index index, T value);

    /**
     * Get neighbor indices for specified index.<br/>
     * In case index refers to null, or it is out of bounds, the result will be empty.
     * @param index The location to get its neighbors
     * @return Neighbors of the specified location, or empty in case location is illegal.
     */
    List<Index> neighbors(Index index);

    /**
     * @return Amount of rows in this matrix
     */
    int rows();

    /**
     * @return Amount of columns in this matrix
     */
    int cols();

    /**
     * Get a string representing this matrix.<br/>
     * The string will be formatted as a matrix, rather than a two dimensional array.<br/>
     * For example:
     * <pre>
     * [0 1 0]
     * [1 1 1]
     * [0 0 1]
     * </pre>
     * Note that each element will be printed using the element's toString method implementation.
     * @return A pretty presentation of this matrix
     * @see #toString()
     */
    String printMatrix();

    /**
     * Get a string representing this matrix as a two dimensional array.<br/>
     * For example:
     * <pre>
     * [[0, 1, 0],
     * [1, 1, 1],
     * [0, 0, 1]]
     * </pre>
     * @return A pretty presentation of this matrix
     * @see #printMatrix()
     */
    String toString();
}
