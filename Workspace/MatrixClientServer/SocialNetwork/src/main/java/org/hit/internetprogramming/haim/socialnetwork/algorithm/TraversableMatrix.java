package org.hit.internetprogramming.haim.socialnetwork.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hit.internetprogramming.haim.matrix.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.haim.matrix.common.mat.Index;
import org.hit.internetprogramming.haim.socialnetwork.data.Node;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TraversableMatrix<T> implements Traversable<Index> {
    @NonNull
    private MatrixGraphAdapter<T> matrix;

    private Index originIndex;

    @Override
    public Node<Index> getOrigin() {
        if (originIndex == null) {
            throw new NullPointerException("Origin index has not been set");
        }

        return new Node<>(originIndex);
    }

    @Override
    public Collection<Node<Index>> getReachableNodes(Node<Index> someNode) {
        return matrix.getReachableVertices(someNode.getData()).stream().map(vertex -> new Node<>(someNode, vertex)).collect(Collectors.toList());
    }
}
