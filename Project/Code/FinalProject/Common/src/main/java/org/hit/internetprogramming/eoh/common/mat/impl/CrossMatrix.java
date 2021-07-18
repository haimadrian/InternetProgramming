package org.hit.internetprogramming.eoh.common.mat.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hit.internetprogramming.eoh.common.mat.ICrossMatrix;

/**
 * An implementation of cross matrix.<br/>
 * A cross matrix is a matrix where the neighbors of each element are at the following
 * locations: {@link ICrossMatrix.Direction#TOP_LEFT TOP-LEFT}, {@link ICrossMatrix.Direction#TOP_RIGHT TOP-RIGHT},
 * {@link ICrossMatrix.Direction#BOTTOM_LEFT BOTTOM-LEFT}, {@link ICrossMatrix.Direction#BOTTOM_RIGHT BOTTOM-RIGHT}
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractMatrix
 * @see StandardMatrix
 */
public class CrossMatrix<T> extends AbstractMatrix<T> implements ICrossMatrix<T> {
    /**
     * Constructs a new {@link CrossMatrix}, with source values.
     * @param from Source to get values from
     */
    public CrossMatrix(T[][] from) {
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
}

