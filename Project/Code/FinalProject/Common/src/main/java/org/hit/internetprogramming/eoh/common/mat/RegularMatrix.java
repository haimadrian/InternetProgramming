package org.hit.internetprogramming.eoh.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RegularMatrix extends AbstractBinaryMatrix implements ICrossMatrix<Integer>, IStandardMatrix<Integer> {
    /**
     * Constructs a new {@link RegularMatrix}, with source values.
     *
     * @param from Source to get values from
     */
    public RegularMatrix(int[][] from) {
        super(from);
    }

    /**
     * Constructs a new and empty (all elements are 0) {@link RegularMatrix}.
     *
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    @JsonCreator
    public RegularMatrix(@JsonProperty("rows") int rows, @JsonProperty("cols") int cols) {
        super(rows, cols);
    }

    /**
     * Constructs a new, random set of data, {@link StandardMatrix}.
     *
     * @param rows     Amount of rows in matrix
     * @param cols     Amount of columns in matrix
     * @param isRandom Whether to fill in random values, or empty (0) only.
     */
    public RegularMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols, isRandom);
    }

    @Override
    public List<Index> neighbors(Index index) {
        List<Index> neighbors = IStandardMatrix.super.neighbors(index);
        neighbors.addAll(ICrossMatrix.super.neighbors(index));
        return neighbors;
    }
}
