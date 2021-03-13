package DO.mat;

import java.util.List;

/**
 *
 * @author Haim Adrian
 * @since 04-Mar-21
 */
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

    String printMatrix();
}
