package DO.mat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class CrossMatrix extends AbstractBinaryMatrix {

    public CrossMatrix(int rows, int cols) {
        super(rows, cols);
    }

    public CrossMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols, isRandom);
    }

    @Override
    public List<Index> neighbors(Index index) {
        List<Index> neighbors = new ArrayList<>();

        if (isIndexValid(index)) {
            if (index.getRow() > 0) {
                if (index.getColumn() > 0) {
                    neighbors.add(new Index(index.getRow() - 1, index.getColumn() - 1));
                }

                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(new Index(index.getRow() - 1, index.getColumn() + 1));
                }
            }

            if (index.getRow() < (rows() - 1)) {
                if (index.getColumn() > 0) {
                    neighbors.add(new Index(index.getRow() + 1, index.getColumn() - 1));
                }

                if (index.getColumn() < (cols() - 1)) {
                    neighbors.add(new Index(index.getRow() + 1, index.getColumn() + 1));
                }
            }
        }

        return neighbors;
    }
}

