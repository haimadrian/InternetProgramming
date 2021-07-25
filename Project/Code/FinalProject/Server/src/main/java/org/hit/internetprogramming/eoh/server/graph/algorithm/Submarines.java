package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Log4j2
public class Submarines {

    public int findSubmarines (IGraph<Index> graph) {
        AtomicInteger submarinesCounter = new AtomicInteger(0);

        ConnectedComponents connectedComponents = new ConnectedComponents();
        List<Set<Index>> allCC = connectedComponents.collect(graph);

        allCC.forEach(connectedComponent -> {
            if(isRectangle(connectedComponent))
                submarinesCounter.incrementAndGet();
        });
        return submarinesCounter.get();
    }

    private boolean isRectangle(Set<Index> connectedComponent) {
        /*
          Checks 3 conditions:
          1. if connected component size is at least 2
          2. if corners of the connected component matches to rectangle
          3. if all expected rectangle indices exists in the connected component
          */

        if(connectedComponent.size() < 2)
            return false;

        HashMap<String, Index> corners = getCorners(connectedComponent);

        //check if the corners are as expected
        if(!checkEqualCorners(corners))
            return false;

        //check if all the expected indices exist in the connected component
        return checkIndices(connectedComponent, corners.get("topLeft"), corners.get("bottomRight"));
    }


    private HashMap<String, Index> getCorners(Set<Index> connectedComponent) {
        HashMap<String, Index> corners = new HashMap<>();

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

        corners.put("topLeft",topLeft);
        corners.put("topRight", topRight);
        corners.put("bottomLeft", bottomLeft);
        corners.put("bottomRight", bottomRight);

        return corners;
    }

    private boolean checkEqualCorners(HashMap<String, Index> corners) {
        if (corners.get("topLeft").getRow() != corners.get("topRight").getRow())
            return false;
        if (corners.get("topLeft").getColumn() != corners.get("bottomLeft").getColumn())
            return false;
        if (corners.get("bottomRight").getRow() != corners.get("bottomLeft").getRow())
            return false;
        return corners.get("bottomRight").getColumn() == corners.get("topRight").getColumn();
    }

    private boolean checkIndices(Set<Index> connectedComponent, Index topLeft, Index bottomRight) {
        for(int i = topLeft.getRow(); i <= bottomRight.getRow(); i++) {
            for(int j = topLeft.getColumn(); j <= bottomRight.getColumn(); j++) {
                if(!connectedComponent.contains( new Index(i,j)))
                    return false;
            }
        }
        return true;
    }

    private boolean checkIsland(Set<Index> connectedComponent) {
        return true;
    }
}
