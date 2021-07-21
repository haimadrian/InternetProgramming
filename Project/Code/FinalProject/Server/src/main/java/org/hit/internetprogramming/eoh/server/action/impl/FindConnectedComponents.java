package org.hit.internetprogramming.eoh.server.action.impl;

import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.graph.MatrixGraphAdapter;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.action.ActionThreadService;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A class for receiving all the connected components in a graph.<br/>
 * The graph is represented by a matrix.
 *
 * @author Orel Gershonovich
 * @see DFSVisit
 * @since 9-July-21
 */
@Log4j2
public class FindConnectedComponents implements Action {

    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }

        List<Set<Index>> finalListWithAllCCAsSet = new ArrayList<>();
        List<Index> unVisitedVertices = graph.getVertices();
        List<Callable<Void>> tasks = new ArrayList<>();
        DFSVisit<Index> dfsVisit = new DFSVisit<>();
        Set<Set<Index>> allCC = new HashSet<>();
        Lock lock = new ReentrantLock();
        for (Index currentSource : unVisitedVertices) {
            tasks.add(() -> {
                List<Index> traverse = dfsVisit.traverse(new MatrixGraphAdapter<>(graph, currentSource));
                if (traverse != null) {
                    Set<Index> connectedComponent = new HashSet<>(traverse);

                    lock.lock();
                    try {
                        allCC.add(connectedComponent);
                    } finally {
                        lock.unlock();
                    }

                    log.debug(() -> Thread.currentThread().getName() + " collected connected component: " + connectedComponent);
                }
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
        return Response.ok(HttpStatus.OK.getCode(), finalListWithAllCCAsSet, actionContext.getRequest().isHttp());
    }
}
