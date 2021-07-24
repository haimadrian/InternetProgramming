package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;

import static org.hit.internetprogramming.eoh.server.impl.MatrixClientHandler.*;

/**
 * Special action created for HTTP, so we can list all actions and return them as links, thus
 * we expose the actions at the home page of the app, when accessing it from a browser.
 * @author Haim Adrian
 * @since 21-Jul-21
 */
public class BuildIndexHtml implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        StringBuilder index = new StringBuilder();

        index.append("<ul>");
        index.append("<li><a href=\"").append(GENERATE_GRAPH_REGULAR_PATH).append("?row=30&col=30\" style=\"color:white\">Generate graph (30x30)</a></li>");
        index.append("<li><a href=\"").append(GENERATE_GRAPH_STANDARD_PATH).append("?row=30&col=30\" style=\"color:white\">Generate graph using standard-matrix (30x30)</a></li>");
        index.append("<li><a href=\"").append(GENERATE_GRAPH_CROSS_PATH + "?row=30&col=30\" style=\"color:white\">Generate graph using cross-matrix (30x30)</a></li>");
        index.append("<li><a href=\"").append(PRINT_PATH).append("\" style=\"color:white\">Print my graph</a></li>");
        index.append("<li><a href=\"").append(CONNECTED_COMPONENTS_PATH).append("\" style=\"color:white\">Find connected components</a></li>");
        index.append("<li><a href=\"").append(SHORTEST_PATHS_PATH).append("?srcrow=0&srccol=0&destrow=1&destcol=4\" style=\"color:white\">Find shortest paths</a></li>");
        index.append("</ul>");

        return Response.ok(index.toString());
    }
}
