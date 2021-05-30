package org.hit.internetprogramming.eoh.server.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.server.common.ClientInfo;

/**
 * The object that {@link ActionExecutor} passes to {@link Action actions} so can they get
 * the requesting client info, and the {@link Request} itself, and do their job based on the request.
 * @author Haim Adrian
 * @since 23-Apr-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActionContext {
    private ClientInfo clientInfo;
    private Request request;
}

