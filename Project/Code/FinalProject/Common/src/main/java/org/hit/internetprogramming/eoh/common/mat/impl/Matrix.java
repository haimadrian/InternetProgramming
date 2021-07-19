package org.hit.internetprogramming.eoh.common.mat.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hit.internetprogramming.eoh.common.mat.ICrossMatrix;
import org.hit.internetprogramming.eoh.common.mat.IStandardMatrix;
import org.hit.internetprogramming.eoh.common.mat.Index;

import java.util.List;

/**
 * An implementation of a matrix.<br/>
 * A matrix is a two dimensional array where the neighbors of each element are at the following
 * locations: {@link IStandardMatrix.Direction#LEFT LEFT}, {@link IStandardMatrix.Direction#TOP TOP},
 * {@link IStandardMatrix.Direction#RIGHT RIGHT}, {@link IStandardMatrix.Direction#BOTTOM BOTTOM},
 * {@link ICrossMatrix.Direction#TOP_LEFT TOP-LEFT}, {@link ICrossMatrix.Direction#TOP_RIGHT TOP-RIGHT},
 * {@link ICrossMatrix.Direction#BOTTOM_LEFT BOTTOM-LEFT}, {@link ICrossMatrix.Direction#BOTTOM_RIGHT BOTTOM-RIGHT}
 * @author Haim Adrian, Orel Gershonovich
 * @since 06-Mar-21
 * @see AbstractMatrix
 */
public class Matrix<T> extends AbstractMatrix<T> implements ICrossMatrix<T>, IStandardMatrix<T> {
    /**
     * Constructs a new {@link Matrix}, with source values.
     *
     * @param from Source to get values from
     */
    public Matrix(T[][] from) {
        super(from);
    }

    /**
     * Constructs a new and empty (all elements are 0) {@link Matrix}.
     *
     * @param rows Amount of rows in matrix
     * @param cols Amount of columns in matrix
     */
    @JsonCreator
    public Matrix(@JsonProperty("rows") int rows, @JsonProperty("cols") int cols) {
        super(rows, cols);
    }

    @Override
    public List<Index> neighbors(Index index) {
        List<Index> neighbors = IStandardMatrix.super.neighbors(index);
        neighbors.addAll(ICrossMatrix.super.neighbors(index));
        return neighbors;
    }
}
