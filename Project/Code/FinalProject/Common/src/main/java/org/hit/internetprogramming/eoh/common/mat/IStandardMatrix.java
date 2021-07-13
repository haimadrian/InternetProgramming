package org.hit.internetprogramming.eoh.common.mat;

import lombok.Getter;

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
     * Directions to go in case of a standard matrix.
     */
    enum Direction {
        TOP(-1, 0),
        BOTTOM(1, 0),
        LEFT(0, -1),
        RIGHT(0, 1);

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
