package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@AllArgsConstructor
@Log4j2
public class Submarines {
    IGraph<Index> graph;

    public int findSubmarines () {
        ConnectedComponents connectedComponents = new ConnectedComponents();
        List<Set<Index>> allCC = connectedComponents.collect(graph);
        List<Callable<Void>> tasks = new ArrayList<>();
        Lock lock = new ReentrantLock();
        List <Boolean> countResults = new ArrayList<>();

        allCC.forEach(connectedComponent -> tasks.add(() -> {
            try {
                lock.lock();
                countResults.add(isRectangle(connectedComponent));
            } finally {
                lock.unlock();
            }
            log.debug(() -> Thread.currentThread().getName() +
                    " Checked connected component is a submarine: " + connectedComponent);
            return null;
        }));

        int submarinesCounter = 0;
        try {
            ActionThreadService.getInstance().invokeAll(tasks);
            for (Boolean currResult: countResults) {
                if (currResult)
                    submarinesCounter++;
            }
        } catch (InterruptedException e) {
            log.error("Failed to check connected component", e);
        }

        return submarinesCounter;
    }

    private boolean isRectangle(Set<Index> connectedComponent) {
        /*
          Checks 4 conditions:
          1. if connected component size is at least 2
          2. if edges of the connected component matches to rectangle
          3. if all expected rectangle indices exists in the connected component
          4. Check if the connected component size equals to the expected size (of rectangle)
          */

        //Submarine must contains at least 2 nodes
        if(connectedComponent.size() < 2)
            return false;

        HashMap<String, Index> edges = getEdges(connectedComponent);

        //check if the edges are as rectangle
        if(!checkEqualEdges(edges))
            return false;

        //check if all the expected indices exist in the connected component
        if(!checkIndices(connectedComponent, edges.get("topLeft"), edges.get("bottomRight")))
            return false;

        //check expected size
        int rowLength = edges.get("topRight").getColumn() - edges.get("topLeft").getColumn() + 1;
        int colLength = edges.get("bottomLeft").getRow() - edges.get("topLeft").getRow() + 1;
        return rowLength * colLength != connectedComponent.size();
    }

    /**
     * Return the edges in a connected component
     * */
    private HashMap<String, Index> getEdges(Set<Index> connectedComponent) {
        HashMap<String, Index> edges = new HashMap<>();

        Index topLeft = connectedComponent.stream().min(Comparator.comparing(Index::getRow).thenComparing(Index::getColumn))
                .orElseThrow(NoSuchElementException::new);

        Set<Index> topRow = connectedComponent.stream().filter(index-> index.getRow() == topLeft.getRow()).collect(Collectors.toSet());

        Index topRight = topRow.stream().max(Comparator.comparing(Index::getColumn))
                .orElseThrow(NoSuchElementException::new);

        Index bottomRight = connectedComponent.stream().max(Comparator.comparing(Index::getRow).thenComparing(Index::getColumn))
                .orElseThrow(NoSuchElementException::new);

        Set<Index> bottomRow = connectedComponent.stream().filter(index->index.getRow() == bottomRight.getRow()).collect(Collectors.toSet());

        Index bottomLeft = bottomRow.stream().min(Comparator.comparing(Index::getColumn))
                .orElseThrow(NoSuchElementException::new);

        edges.put("topLeft",topLeft);
        edges.put("topRight", topRight);
        edges.put("bottomLeft", bottomLeft);
        edges.put("bottomRight", bottomRight);

        return edges;
    }

    /**
     * Check if the edges are as rectangle
     * */
    private boolean checkEqualEdges(HashMap<String, Index> edges) {
        if (edges.get("topLeft").getRow() != edges.get("topRight").getRow())
            return false;
        if (edges.get("topLeft").getColumn() != edges.get("bottomLeft").getColumn())
            return false;
        if (edges.get("bottomRight").getRow() != edges.get("bottomLeft").getRow())
            return false;
        return edges.get("bottomRight").getColumn() == edges.get("topRight").getColumn();
    }

    /**
     * Assuming it is a rectangle, we check if all the indices exist
     * in the connected component
     * */
    private boolean checkIndices(Set<Index> connectedComponent, Index topLeft, Index bottomRight) {
        for(int row = topLeft.getRow(); row <= bottomRight.getRow(); row++)
            for(int col = topLeft.getColumn(); col <= bottomRight.getColumn(); col++)
                if(!connectedComponent.contains( new Index(row, col)))
                    return false;
        return true;
    }
}
