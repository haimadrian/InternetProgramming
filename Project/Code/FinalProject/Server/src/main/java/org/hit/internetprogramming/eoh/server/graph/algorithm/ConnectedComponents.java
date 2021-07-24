package org.hit.internetprogramming.eoh.server.graph.algorithm;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Log4j2
public class ConnectedComponents {
    public List<Set<Index>> execute(IGraph<Index> graph) {
        if (graph == null) {
            return null;
        }

        List<Set<Index>> finalListWithAllCCAsSet = new ArrayList<>();
        List<Index> unVisitedVertices = graph.getVertices();
        List<Callable<Void>> tasks = new ArrayList<>();
        DFSVisit<Index> dfsVisit = new DFSVisit<>();
        Set<Set<Index>> allCC = new HashSet<>();
        Lock lock = new ReentrantLock();
        for (Index currentSource : unVisitedVertices) {
            tasks.add(() -> {
                Set<Index> connectedComponent = new HashSet<>(dfsVisit.traverse(new MatrixGraphAdapter<>(graph, currentSource)));

                lock.lock();
                try {
                    allCC.add(connectedComponent);
                } finally {
                    lock.unlock();
                }

                log.debug(() -> Thread.currentThread().getName() + " collected connected component: " + connectedComponent);
                return null;
            });
        }

        try {
            ActionThreadService.getInstance().invokeAll(tasks);
            finalListWithAllCCAsSet.addAll(allCC);
            finalListWithAllCCAsSet.sort(Comparator.comparingInt(Set::size));
        } catch (InterruptedException e) {
            log.error("Failed to collect connected components", e);
        }
        return finalListWithAllCCAsSet;
    }
}
