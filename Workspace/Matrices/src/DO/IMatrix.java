package DO;

import java.util.List;

/**
 *
 * @author Haim Adrian
 * @since 04-Mar-21
 */
public interface IMatrix<T> {
    /**
     * Returns the value at the specified index.<br/>
     * In case index refers to null, or it is out of bounds, the result will be null.
     * @param index The location of a value to get
     * @return The value at the specified location, or null in case location is illegal.
     */
    T val(Index index);

    /**
     * Get neighbor indices for specified index.<br/>
     * In case index refers to null, or it is out of bounds, the result will be empty.
     * @param index The location to get its neighbors
     * @return Neighbors of the specified location, or empty in case location is illegal.
     */
    List<Index> neighbors(Index index);
    String printMatrix();
}
