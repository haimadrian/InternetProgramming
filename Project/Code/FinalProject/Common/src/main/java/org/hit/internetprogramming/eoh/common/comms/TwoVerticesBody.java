package org.hit.internetprogramming.eoh.common.comms;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * This class represents a request body that holds two vertices.<br/>
 * We needed this type to support generic body that can pass a source and destination vertices, for
 * SHORTEST_PATHS algorithm.
 * @param <T> Type of a vertex. (Index)
 */
@Data
public class TwoVerticesBody<T> {
    /**
     * First vertex (source)
     */
    private T first;

    /**
     * Second vertex (destination)
     */
    private T second;

    /**
     * Constructs a new {@link TwoVerticesBody}
     * @param first First vertex (source)
     * @param second Second vertex (destination)
     */
    @JsonCreator
    public TwoVerticesBody(@JsonProperty("first") T first, @JsonProperty("second") T second) {
        this.first = first;
        this.second = second;
    }
}
