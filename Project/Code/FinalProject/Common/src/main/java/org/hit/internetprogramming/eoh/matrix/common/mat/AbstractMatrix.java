package org.hit.internetprogramming.eoh..common.mat;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Abstract implementation of {@link IMatrix} for generic type T.<br/>
 * This class responsible for managing the underlying data structure and exposes the
 * access to the data structure according to the generic type T.<br/>
 * We also implement a pretty print of a matrix. See {@link #printMatrix()} and {@link #toString()}
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractBinaryMatrix
 */
abstract class AbstractMatrix<T> implements IMatrix<T> {
    @JsonProperty("data")
    private final Object[][] data;

    /**
     * Constructs a new {@link AbstractMatrix}
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    public AbstractMatrix(int rows, int cols) {
        data = new Object[rows][cols];
    }

    @JsonGetter("rows")
    @Override
    public final int rows() {
        return data.length;
    }

    @JsonGetter("cols")
    @Override
    public final int cols() {
        return data[0].length;
    }

    @Override
    public void setValue(Index index, T value) {
        if (isIndexValid(index)) {
            data[index.getRow()][index.getColumn()] = value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(Index index) {
        if (!isIndexValid(index)) {
            return null;
        }

        return (T)data[index.getRow()][index.getColumn()];
    }

    /**
     * Helper method used to make sure a specified index is valid. (Differs from null, and inside matrix bounds.)
     * @param index The index to validate.
     * @return True in case index differs from null, and inside matrix bounds. False otherwise.
     */
    protected final boolean isIndexValid(Index index) {
        return (index != null && isIndexValid(index.getRow(), index.getColumn()));
    }

    /**
     * Helper method used to make sure a specified index is valid. (Differs from null, and inside matrix bounds.)
     * @param row The row index to validate.
     * @param col The col index to validate.
     * @return True in case index differs from null, and inside matrix bounds. False otherwise.
     */
    protected final boolean isIndexValid(int row, int col) {
        return isInRange(row, 0, rows()) && isInRange(col, 0, cols());
    }

    private static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    @Override
    public String printMatrix() {
        String toString = toString();
        return toString.substring(1, toString.length() - 1).replace(",", "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Object[] currRow : data) {
            if (sb.length() > 1) {
                sb.append(',').append(System.lineSeparator());
            }

            sb.append(Arrays.toString(currRow));
        }
        sb.append("]");
        return sb.toString();
    }
}

