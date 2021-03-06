package DO;

import java.security.SecureRandom;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public abstract class AbstractBinaryMatrix extends AbstractMatrix<Integer> {

    public AbstractBinaryMatrix(int rows, int cols) {
        super(rows, cols);

        SecureRandom rand = new SecureRandom();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                setValue(row, col, rand.nextInt(2));
            }
        }
    }
}

