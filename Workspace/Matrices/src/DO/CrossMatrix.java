package DO;

import java.util.List;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public class CrossMatrix extends AbstractBinaryMatrix {

    public CrossMatrix(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public List<Index> neighbors(Index index) {
        return null;
    }
}

