package org.hit.internetprogramming.eoh.common.mat;

import java.security.SecureRandom;

/**
 * An implementation of {@link AbstractMatrix} where the generic type is considered to be Integer.<br/>
 * This is the base class for binary matrices, which exposes common functionality of binary matrices.<br/>
 * For example, a binary matrix can be constructed using random data, or empty (0) data.<br/>
 * In addition, we implement the {@link #hasValue(Index)} method here to check that the value at some
 * {@link Index} is 1.
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractMatrix
 */
abstract class AbstractBinaryMatrix extends AbstractMatrix<Integer> {
    /**
     * Constructs a new {@link AbstractBinaryMatrix}, with source values.
     * @param from Source to get values from
     */
    public AbstractBinaryMatrix(int[][] from) {
        super(from.length, from[0].length);
        for (int row = 0; row < from.length; row++) {
            for (int col = 0; col < from[0].length; col++) {
                setValue(new Index(row, col), from[row][col]);
            }
        }
    }

    /**
     * Constructs a new and empty (all elements are 0) {@link AbstractBinaryMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    public AbstractBinaryMatrix(int rows, int cols) {
        this(rows, cols, false);
    }

    /**
     * Constructs a new, random set of data, {@link AbstractBinaryMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     * @param isRandom Whether to fill in random values, or empty (0) only.
     */
    public AbstractBinaryMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols);

        if (isRandom) {
            SecureRandom rand = new SecureRandom();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    setValue(new Index(row, col), rand.nextInt(2));
                }
            }
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    setValue(new Index(row, col), 0);
                }
            }
        }
    }

    @Override
    public void setValue(Index index, Integer value) {
        if ((value == null) || ((value != 0) && (value != 1))) {
            throw new IllegalArgumentException("A binary matrix accepts 0 or 1 only! Was: " + value);
        }

        super.setValue(index, value);
    }

    /**
     * For binary matrix, we treat the value 1 as an existing value. null and 0 are considered as non-existing
     * @param index The index to check if there is value at
     * @return Whether there is a value at the specified index or not
     */
    @Override
    public boolean hasValue(Index index) {
        return isIndexValid(index) && (getValue(index) != null) && (getValue(index) == 1);
    }
}

