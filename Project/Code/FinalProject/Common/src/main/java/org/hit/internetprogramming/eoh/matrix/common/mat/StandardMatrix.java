package org.hit.internetprogramming.eoh..common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of standard binary matrix.<br/>
 * A standard binary matrix is a binary matrix where the neighbors of each element are at the following
 * locations: LEFT, TOP, RIGHT, BOTTOM
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractBinaryMatrix
 * @see CrossMatrix
 */
public class StandardMatrix extends AbstractBinaryMatrix {
    /**
     * Constructs a new {@link StandardMatrix}, with source values.
     * @param from Source to get values from
     */
    public StandardMatrix(int[][] from) {
        super(from);
    }

    /**
     * Constructs a new and empty (all elements are 0) {@link StandardMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    @JsonCreator
    public StandardMatrix(@JsonProperty("rows") int rows, @JsonProperty("cols") int cols) {
        super(rows, cols);
    }

    /**
     * Constructs a new, random set of data, {@link StandardMatrix}.
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     * @param isRandom Whether to fill in random values, or empty (0) only.
     */
    public StandardMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols, isRandom);
    }

    @Override
    public List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        if (isIndexValid(index)) {
            if (index.getRow() > 0) {
                neighbors.add(new Index(index.getRow() - 1, index.getColumn()));
            }

            if (index.getRow() < (rows() - 1)) {
                neighbors.add(new Index(index.getRow() + 1, index.getColumn()));
            }

            if (index.getColumn() > 0) {
                neighbors.add(new Index(index.getRow(), index.getColumn() - 1));
            }

            if (index.getColumn() < (cols() - 1)) {
                neighbors.add(new Index(index.getRow(), index.getColumn() + 1));
            }
        }

        return neighbors;
    }
}

