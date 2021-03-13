package DO.mat;

import java.util.Arrays;

/**
 * @author Haim Adrian
 * @since 06-Mar-21
 */
public abstract class AbstractMatrix<T> implements IMatrix<T> {
    private final Object[][] data;

    public AbstractMatrix(int rows, int cols) {
        data = new Object[rows][cols];
    }

    @Override
    public final int rows() {
        return data.length;
    }

    @Override
    public final int cols() {
        return data[0].length;
    }

    @Override
    public void setValue(Index index, T value) {
        if (isIndexValid(index)) {
            data[index.getRow()][index.getColumn()] = value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue(Index index) {
        if (!isIndexValid(index)) {
            return null;
        }

        return (T)data[index.getRow()][index.getColumn()];
    }

    protected final int cols(int row) {
        return data[row].length;
    }

    /**
     * Helper method used to make sure a specified index is valid. (Differs from null, and inside matrix bounds.)
     * @param index The index to validate.
     * @return True in case index differs from null, and inside matrix bounds. False otherwise.
     */
    protected final boolean isIndexValid(Index index) {
        return (index != null && isIndexValid(index.getRow(), index.getColumn()));
    }

    /**
     * Helper method used to make sure a specified index is valid. (Differs from null, and inside matrix bounds.)
     * @param row The row index to validate.
     * @param column The col index to validate.
     * @return True in case index differs from null, and inside matrix bounds. False otherwise.
     */
    protected final boolean isIndexValid(int row, int col) {
        return isInRange(row, 0, rows()) && isInRange(col, 0, cols(row));
    }

    private static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    @Override
    public String printMatrix() {
        String toString = toString();
        return toString.substring(1, toString.length() - 1).replace(",", "");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int row = 0; row < data.length; row++) {
            if (sb.length() > 1) {
                sb.append(',').append(System.lineSeparator());
            }

            sb.append(Arrays.toString(data[row]));
        }
        sb.append("]");
        return sb.toString();
    }
}

