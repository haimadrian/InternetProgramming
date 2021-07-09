package org.hit.internetprogramming.eoh.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An implementation of cross binary matrix.<br/>
 * A cross binary matrix is a binary matrix where the neighbors of each element are at the following
 * locations: TOP-LEFT, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-RIGHT
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractBinaryMatrix
 * @see StandardMatrix
 */
public class CrossMatrix extends AbstractBinaryMatrix implements ICrossMatrix<Integer> {
    /**
     * Constructs a new {@link CrossMatrix}, with source values.
     * @param from Source to get values from
     */
    public CrossMatrix(int[][] from) {
        super(from);
    }

    /**
     * Constructs a new and empty (all elements are 0) {@link CrossMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    @JsonCreator
    public CrossMatrix(@JsonProperty("rows") int rows, @JsonProperty("cols") int cols) {
        super(rows, cols);
    }

    /**
     * Constructs a new, random set of data, {@link CrossMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     * @param isRandom Whether to fill in random values, or empty (0) only.
     */
    public CrossMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols, isRandom);
    }
}

