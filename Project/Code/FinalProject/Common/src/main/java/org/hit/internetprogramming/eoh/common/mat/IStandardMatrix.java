package org.hit.internetprogramming.eoh.common.mat;

import java.util.ArrayList;
import java.util.List;

/**
 * A standard binary matrix is a binary matrix where the neighbors of each element are at the following
 * locations: LEFT, TOP, RIGHT, BOTTOM
 * @author Haim Adrian
 * @since 09-Jul-21
 * @see IMatrix
 * @see IStandardMatrix
 */
public interface IStandardMatrix<T> extends IMatrix<T> {

    default List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        // Instead of falling into potential exceptions, which might affect the performance,
        // just validate bounds.
        if ((index != null) && (index.getRow() >= 0) && (index.getRow() < rows()) && (index.getColumn() >= 0) && (index.getColumn() < cols())) {
            // Top
            if (index.getRow() > 0) {
                neighbors.add(new Index(index.getRow() - 1, index.getColumn()));
            }

            // Bottom
            if (index.getRow() < (rows() - 1)) {
                neighbors.add(new Index(index.getRow() + 1, index.getColumn()));
            }

            // Left
            if (index.getColumn() > 0) {
                neighbors.add(new Index(index.getRow(), index.getColumn() - 1));
            }

            // Right
            if (index.getColumn() < (cols() - 1)) {
                neighbors.add(new Index(index.getRow(), index.getColumn() + 1));
            }
        }

        return neighbors;
    }
}
