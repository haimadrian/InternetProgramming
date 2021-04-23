package org.hit.internetprogramming.haim.matrix.server.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;

/**
 * This exception created so we will be able to detect when browser asks for favicon, and notify {@link org.hit.internetprogramming.haim.matrix.server.common.ClientHandler}
 * to write the favicon in return.
 * @author Haim Adrian
 * @since 17-Apr-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FavIconException extends IOException {

}

