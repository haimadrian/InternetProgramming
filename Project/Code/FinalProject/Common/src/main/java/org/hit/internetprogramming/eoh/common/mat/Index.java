package org.hit.internetprogramming.eoh.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a location at some matrix.<br/>
 * A location is a tuple containing row and column numbers, to access elements at matrix.
 * @author Haim Adrian
 * @since 04-Mar-21
 * @see IMatrix
 */
@EqualsAndHashCode
public class Index {
    @JsonIgnore
    private static final Map<Index, Index> indicesCache = new HashMap<>();

    @Getter
    private final int row;

    @Getter
    private final int column;

    private Index(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ')';
    }

    /**
     * Get or create a new {@link Index} for the specified row and column.
     * @param row Row of an index
     * @param column Column of an index
     * @return The index
     */
    @JsonCreator
    public static Index from(@JsonProperty("row") int row, @JsonProperty("column") int column) {
        // Get an index from cache.
        return indicesCache.computeIfAbsent(new Index(row, column), newIndex -> newIndex);
    }
}

