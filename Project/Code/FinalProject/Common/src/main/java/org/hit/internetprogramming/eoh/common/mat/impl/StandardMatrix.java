package org.hit.internetprogramming.eoh.common.mat.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hit.internetprogramming.eoh.common.mat.IStandardMatrix;

/**
 * An implementation of standard matrix.<br/>
 * A standard matrix is a matrix where the neighbors of each element are at the following
 * locations: {@link IStandardMatrix.Direction#LEFT LEFT}, {@link IStandardMatrix.Direction#TOP TOP},
 * {@link IStandardMatrix.Direction#RIGHT RIGHT}, {@link IStandardMatrix.Direction#BOTTOM BOTTOM}
 * @author Haim Adrian
 * @since 06-Mar-21
 * @see AbstractMatrix
 * @see CrossMatrix
 */
public class StandardMatrix<T> extends AbstractMatrix<T> implements IStandardMatrix<T> {
    /**
     * Constructs a new {@link StandardMatrix}, with source values.
     * @param from Source to get values from
     */
    public StandardMatrix(T[][] from) {
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
}

