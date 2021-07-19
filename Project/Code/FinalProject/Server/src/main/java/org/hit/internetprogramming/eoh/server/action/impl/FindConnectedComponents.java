package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.comms.HttpStatus;
import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.common.graph.IGraph;
import org.hit.internetprogramming.eoh.common.mat.Index;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;
import org.hit.internetprogramming.eoh.server.graph.algorithm.ConnectedComponents;
import org.hit.internetprogramming.eoh.server.graph.algorithm.DFSVisit;
import org.hit.internetprogramming.eoh.server.impl.Graphs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * A class for receiving all the connected components in a graph.<br/>
 * The graph is represented by a matrix.
 * @author Orel Gershonovich
 * @since   9-July-21
 * @see DFSVisit
 */
public class FindConnectedComponents implements Action {

    @Override
    public Response execute(ActionContext actionContext) {
        IGraph<Index> graph = Graphs.getInstance().getGraph(actionContext.getClientInfo());
        if (graph == null) {
            return Response.error(HttpStatus.NOT_FOUND.getCode(), "No graph was initialized. Please put graph or generate one", actionContext.getRequest().isHttp());
        }

        ConnectedComponents<Index> connectedComponents = new ConnectedComponents<>();

        ExecutorService es = Executors.newFixedThreadPool(3);

        Callable<List<Set<Index>>> connectedComponentsItems = () -> connectedComponents.traverse(graph);

        FutureTask<List<Set<Index>>> listOfConnectedComponents = (FutureTask<List<Set<Index>>>) es.submit(connectedComponentsItems);
        List<Set<Index>> response = null;
        try {
            response = listOfConnectedComponents.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //FutureTask<Set<Index>> connectedComponentsItems = new Callable<>()
        //List<Set<Index>> connectedComponentsItems = connectedComponents.traverse(graph);

        return Response.ok(HttpStatus.OK.getCode(), response, actionContext.getRequest().isHttp());
    }
}
