package org.hit.internetprogramming.eoh.common.mat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A class representing a location at some matrix.<br/>
 * A location is a tuple containing row and column numbers, to access elements at matrix.
 * @author Haim Adrian
 * @since 04-Mar-21
 * @see IMatrix
 */
@EqualsAndHashCode
public class Index {
    /**
     * Maintain a weak cache of indices.<br/>
     * This allows us to compare indices using ==. Not only that, we do not create billions of indices, we just
     * get the same indices out of the cache.<br/>
     * We use a weak cache to let the gc free those indices in case their are no longer referenced.
     */
    @JsonIgnore
    static final Map<Index, WeakReference<Index>> indicesCache = new WeakHashMap<>();

    @Getter
    private final int row;

    @Getter
    private final int column;

    public Index(int row, int column) {
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
        return indicesCache.computeIfAbsent(new Index(row, column), WeakReference::new).get();
    }
}

