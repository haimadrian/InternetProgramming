package DO.mat;

import java.util.Objects;

/**
 * @author Haim Adrian
 * @since 04-Mar-21
 */
public class Index {
    private int row;
    private int column;

    public Index(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Index)) {
            return false;
        }

        Index index = (Index) o;
        return row == index.row && column == index.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Integer.valueOf(row), Integer.valueOf(column));
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ')';
    }
}

