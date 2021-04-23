package org.hit.internetprogramming.haim.matrix.server.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hit.internetprogramming.haim.matrix.common.comms.Request;
import org.hit.internetprogramming.haim.matrix.server.common.ClientInfo;

/**
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

