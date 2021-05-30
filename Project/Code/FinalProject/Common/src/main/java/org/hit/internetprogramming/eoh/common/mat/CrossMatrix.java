package org.hit.internetprogramming.eoh.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of cross binary matrix.<br/>
 * A cross binary matrix is a binary matrix where the neighbors of each element are at the following
 * locations: TOP-LEFT, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-RIGHT
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractBinaryMatrix
 * @see StandardMatrix
 */
public class CrossMatrix extends AbstractBinaryMatrix {
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

    @Override
    public List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        // Instead of falling into potential exceptions, which might affect the performance,
        // just validate bounds.
        if (isIndexValid(index)) {
            if (index.getRow() > 0) {
                // Top Left
                if (index.getColumn() > 0) {
                    neighbors.add(new Index(index.getRow() - 1, index.getColumn() - 1));
                }

                // Top Right
                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(new Index(index.getRow() - 1, index.getColumn() + 1));
                }
            }

            if (index.getRow() < (rows() - 1)) {
                // Bottom Left
                if (index.getColumn() > 0) {
                    neighbors.add(new Index(index.getRow() + 1, index.getColumn() - 1));
                }

                // Bottom Right
                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(new Index(index.getRow() + 1, index.getColumn() + 1));
                }
            }
        }

        return neighbors;
    }
}

