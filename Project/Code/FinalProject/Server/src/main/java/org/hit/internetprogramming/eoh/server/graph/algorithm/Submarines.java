package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@AllArgsConstructor
@Log4j2
public class Submarines {
    public int findSubmarines(IGraph<Index> graph) {
        ConnectedComponents connectedComponents = new ConnectedComponents();
        List<Set<Index>> allCC = connectedComponents.collect(graph);

        if (allCC == null)
            return 0;

        List<Callable<Void>> tasks = new ArrayList<>();
        Lock lock = new ReentrantLock();
        List<Boolean> countResults = new ArrayList<>();

        allCC.forEach(connectedComponent -> tasks.add(() -> {
            lock.lock();
            try {
                countResults.add(checkSubmarine(connectedComponent));
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
            for (Boolean currResult : countResults)
                if (currResult)
                    submarinesCounter++;
        } catch (InterruptedException e) {
            log.error(" Failed to check connected component", e);
        }

        return submarinesCounter;
    }

    /**
     * Submarine: Submarine is a full rectangle (without holes).
     * Check if a connected component is a submarine
     *
     * @param connectedComponent, a set of reachable indices
     * @return true if submarine, else- false
     */
    private boolean checkSubmarine(Set<Index> connectedComponent) {
        if (connectedComponent.size() < 2)
            return false;

        int left = Integer.MAX_VALUE, top = Integer.MAX_VALUE, right = -1, bottom = -1;
        for (Index vertex : connectedComponent) {
            int row = vertex.getRow(), col = vertex.getColumn();
            if (col > right)
                right = col;
            if (col < left)
                left = col;
            if (row < top)
                top = row;
            if (row > bottom)
                bottom = row;
        }
        return connectedComponent.size() == ((right - left + 1) * (bottom - top + 1));
    }
}
