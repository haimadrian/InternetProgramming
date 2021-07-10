package org.hit.internetprogramming.eoh.common.mat;

import java.util.ArrayList;
import java.util.List;

/**
 * A cross binary matrix is a binary matrix where the neighbors of each element are at the following
 * locations: TOP-LEFT, TOP-RIGHT, BOTTOM-LEFT, BOTTOM-RIGHT
 * @author Haim Adrian
 * @since 09-Jul-21
 * @see IMatrix
 * @see IStandardMatrix
 */
public interface ICrossMatrix<T> extends IMatrix<T>  {

    default List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        // Instead of falling into potential exceptions, which might affect the performance,
        // just validate bounds.
        if ((index != null) && (index.getRow() >= 0) && (index.getRow() < rows()) && (index.getColumn() >= 0) && (index.getColumn() < cols())) {
            if (index.getRow() > 0) {
                // Top Left
                if (index.getColumn() > 0) {
                    neighbors.add(Index.from(index.getRow() - 1, index.getColumn() - 1));
                }

                // Top Right
                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(Index.from(index.getRow() - 1, index.getColumn() + 1));
                }
            }

            if (index.getRow() < (rows() - 1)) {
                // Bottom Left
                if (index.getColumn() > 0) {
                    neighbors.add(Index.from(index.getRow() + 1, index.getColumn() - 1));
                }

                // Bottom Right
                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(Index.from(index.getRow() + 1, index.getColumn() + 1));
                }
            }
        }

        return neighbors;
    }
}
