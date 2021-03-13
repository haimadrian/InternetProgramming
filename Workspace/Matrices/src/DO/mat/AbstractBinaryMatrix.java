package DO.mat;

import java.security.SecureRandom;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public abstract class AbstractBinaryMatrix extends AbstractMatrix<Integer> {

    public AbstractBinaryMatrix(int rows, int cols) {
        this(rows, cols, false);
    }

    public AbstractBinaryMatrix(int rows, int cols, boolean isRandom) {
        super(rows, cols);

        if (isRandom) {
            SecureRandom rand = new SecureRandom();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    setValue(new Index(row, col), rand.nextInt(2));
                }
            }
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    setValue(new Index(row, col), 0);
                }
            }
        }
    }

    /**
     * For binary matrix, we treat the value 1 as an existing value. null and 0 are considered as non-existing
     * @param index The index to check if there is value at
     * @return
     */
    @Override
    public boolean hasValue(Index index) {
        return isIndexValid(index) && (getValue(index) != null) && (getValue(index) == 1);
    }
}

