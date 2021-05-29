package org.hit.internetprogramming.haim.socialnetwork.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * This class encapsulates a Domain Object (DO) and enables it to connect with other concrete nodes
 *
 * @author Nathan Dillbary
 */
public class Node<T> {
    private final UUID uniqueId; // unique identifier for the Node
    private final T data; // concrete data
    private final Node<T> parent; // concrete data

    /*
    We'll create a map of maps (whose key is the desired class)
    Outer map: maps a Class to the class's corresponding inner map such that
    an inner map gets a key representing the identifier of an  object
    then returns the actual object

    For example, a Node that encapsulates a Profile
    Get Profiles associated with a profile (friends) ->  return Map<String,Profile>
    Get Groups that a profile is a member of -> Map<String,Group>
    Generally:
    Get Class of some type -> Map<String,Class<V>>
     */
    private final Map<Class<?>, Map<String, ?>> typeMap;

    public Node(T data) {
        this(null, data);
    }

    public Node(Node<T> parent, T data) {
        this.parent = parent;
        this.data = data;
        this.uniqueId = UUID.randomUUID();
        this.typeMap = new HashMap<>();
    }

    public T getData() {
        return data;
    }

    @SuppressWarnings("unchecked")
    protected <V> Map<String, V> getInnerMap(Class<V> someClass) {
        if (this.typeMap.containsKey(someClass)) {
            return (Map<String, V>) this.typeMap.get(someClass);
        } else {
            return null;
        }
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @SuppressWarnings("unchecked")
    public <V> V putObject(String key, Node<V> value) {
        Class<?> vClass = value.getData().getClass();
        return ((Map<String, V>) this.typeMap.computeIfAbsent(vClass,
            aClass -> new HashMap<String, V>())).put(key, value.getData());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(data, node.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }
}
