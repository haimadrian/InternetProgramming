package org.hit.internetprogramming.eoh.server.common;

import lombok.*;

import java.net.InetAddress;
import java.net.Socket;

/**
 * A class used to identify a client socket, so we can let {@link RequestHandler} to decide what to do with that client
 * @author Haim Adrian
 * @since 17-Apr-21
 * @see #address
 * @see #port
 * @see #localPort
 */
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class ClientInfo {
    /**
     * The IP address of the remote end. (client)
     */
    @Getter
    private final InetAddress address;

    /**
     * The port number on the remote host. (client)<br/>
     */
    @Getter
    private final int port;

    /**
     * The local port number which we use in order to communicate with the remote client
     */
    @Getter
    private final int localPort;

    public static ClientInfo from(Socket socket) {
        return (socket == null) ? null : new ClientInfo(socket.getInetAddress(), socket.getPort(), socket.getLocalPort());
    }
}

