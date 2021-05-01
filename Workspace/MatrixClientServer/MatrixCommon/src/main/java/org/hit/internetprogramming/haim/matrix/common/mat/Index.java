package org.hit.internetprogramming.haim.matrix.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * A class representing a location at some matrix.<br/>
 * A location is a tuple containing row and column numbers, to access elements at matrix.
 * @author Haim Adrian
 * @since 04-Mar-21
 * @see IMatrix
 */
@EqualsAndHashCode
public class Index {
    @Getter
    private int row;

    @Getter
    private int column;

    @JsonCreator
    public Index(@JsonProperty("row") int row, @JsonProperty("column") int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ')';
    }
}

