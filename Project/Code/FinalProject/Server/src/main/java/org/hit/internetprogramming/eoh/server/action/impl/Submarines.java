package org.hit.internetprogramming.eoh.server.action.impl;

import org.hit.internetprogramming.eoh.common.comms.Response;
import org.hit.internetprogramming.eoh.server.action.Action;
import org.hit.internetprogramming.eoh.server.action.ActionContext;

public class Submarines implements Action {
    @Override
    public Response execute(ActionContext actionContext) {
        return Response.ok();
    }
}
