package org.hit.internetprogramming.eoh.common.mat.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.hit.internetprogramming.eoh.common.mat.IMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;

import java.util.Arrays;

/**
 * Abstract implementation of {@link IMatrix} for generic type T.<br/>
 * This class responsible for managing the underlying data structure and exposes the
 * access to the data structure according to the generic type T.<br/>
 * We also implement a pretty print of a matrix. See {@link #printMatrix()} and {@link #toString()}
 * @author Haim Adrian
 * @since 06-Mar-21
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

    /**
     * Constructs a new {@link AbstractMatrix}, with source values.
     * @param copyFrom Source to get values from
     */
    public AbstractMatrix(T[][] copyFrom) {
        this(copyFrom.length, copyFrom[0].length);
        for (int row = 0; row < copyFrom.length; row++) {
            for (int col = 0; col < copyFrom[0].length; col++) {
                setValue(Index.from(row, col), copyFrom[row][col]);
            }
        }
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

    @Override
    public boolean hasValue(Index index) {
        return isIndexValid(index) && (getValue(index) != null);
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
        StringBuilder sb = new StringBuilder();

        // 1 space from right, to separate between values
        int longestValue = findLongestValue() + 1;
        String emptyCell = StringUtils.repeat(' ', longestValue);

        for (Object[] currRow : data) {
            if (sb.length() > 0) {
                sb.append(System.lineSeparator());
            }

            for (Object currCell : currRow) {
                String currCellAsString;
                if (currCell != null) {
                    currCellAsString = StringUtils.center(currCell.toString(), longestValue);
                } else {
                    currCellAsString = emptyCell;
                }

                sb.append(currCellAsString);
            }
        }

        return sb.toString();
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

    /**
     * In order to format a matrix that might have values longer than 1, we need to find the longest value so all
     * values will have a fixed width.
     * @return The length of the longest value in this matrix.
     */
    private int findLongestValue() {
        int maxLength = 0;

        for (Object[] currRow : data) {
            for (Object currCell : currRow) {
                if (currCell != null) {
                    maxLength = Math.max(maxLength, currCell.toString().length());
                }
            }
        }

        return maxLength;
    }
}

