package org.hit.internetprogramming.eoh.common.mat;

import lombok.Getter;

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
    @Override
    default List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        // Instead of falling into potential exceptions, which might affect the performance,
        // just validate bounds.
        if ((index != null) && (index.getRow() >= 0) && (index.getRow() < rows()) && (index.getColumn() >= 0) && (index.getColumn() < cols())) {
            // Go over every direction to check if it is within bounds, and if so, add it as a neighbor.
            for (Direction direction : Direction.values()) {
                Index neighbor = Index.from(index.getRow() + direction.getRowDirection(), index.getColumn() + direction.getColumnDirection());
                if ((neighbor.getRow() >= 0) && (neighbor.getRow() < rows()) && (neighbor.getColumn() >= 0) && (neighbor.getColumn() < cols())) {
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    /**
     * Directions to go in case of a cross matrix.
     */
    enum Direction {
        TOP_LEFT(-1, -1),
        TOP_RIGHT(-1, 1),
        BOTTOM_LEFT(1, -1),
        BOTTOM_RIGHT(1, 1);

        @Getter
        private final int rowDirection;

        @Getter
        private final int columnDirection;

        Direction(int rowDirection, int columnDirection) {
            this.rowDirection = rowDirection;
            this.columnDirection = columnDirection;
        }
    }
}
