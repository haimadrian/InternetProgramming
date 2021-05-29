package org.hit.internetprogramming.eoh.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.log4j.Log4j2;
import org.hit.internetprogramming.eoh.common.action.ActionType;
import org.hit.internetprogramming.eoh.common.comms.Request;
import org.hit.internetprogramming.eoh.common.comms.Response;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Log4j2
public class GraphWebService {
    private static final GraphWebService instance = new GraphWebService();
    private Socket clientSocket;
    private BufferedWriter outToServer;
    private BufferedReader inFromServer;

    /**
     * Jackson object mapper to convert json string to bean and vice versa
     */
    private final ObjectMapper objectMapper;

    private GraphWebService() {
        objectMapper = initializeObjectMapper();
    }

    private ObjectMapper initializeObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        // Do not enable standard indentation ("pretty-printing"), cause the client depends on the new line character
        // objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Allow serialization of "empty" POJOs (no properties to serialize)
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        return objectMapper;
    }

    public static GraphWebService getInstance() {
        return instance;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    private void connect() {
        try {
            String ip = "127.0.0.1";
            int port = 1234;
            log.info("Connecting to server at: " + ip + ":" + port);

            clientSocket = new Socket(ip, port);
            clientSocket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(2));
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (Exception e) {
            log.error("Error has occurred: ", e);
        }
    }

    public void disconnect() {
        log.info("Disconnecting from server.");
        executeRequest(new Request(ActionType.DISCONNECT, null, false));
        closeStreams();
    }

    private void closeStreams() {
        log.info("Closing communication with server.");

        try {
            if (outToServer != null) {
                outToServer.close();
                outToServer = null;
            }
        } catch (IOException ignore) {
        }

        try {
            if (inFromServer != null) {
                inFromServer.close();
                inFromServer = null;
            }
        } catch (IOException ignore) {
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        } catch (IOException ignore) {
        }
    }

    public Response executeRequest(Request request) {
        Response response = null;
        try {
            if (outToServer == null) {
                connect();
            }

            if (outToServer != null) {
                String requestJson = objectMapper.writeValueAsString(request);
                log.info("Sending request: " + requestJson);

                outToServer.write(requestJson + "\n\n");
                outToServer.flush();

                try {
                    String responseLine = inFromServer.readLine();
                    log.info("Response: " + responseLine);

                    if ((responseLine == null) || !responseLine.trim().startsWith("{")) {
                        disconnect();
                        response = new Response(500, "500 INTERNAL SERVER ERROR", new ArrayList<>(), false);
                    } else {
                        response = objectMapper.readValue(responseLine, Response.class);
                    }
                } catch (SocketTimeoutException e) {
                    closeStreams();
                    response = new Response(408, "408 TIME OUT", new ArrayList<>(), false);
                }
            } else {
                response = new Response(503, "503 SERVICE UNAVAILABLE", new ArrayList<>(), false);
                log.warn("Not connected. Unable to send requests.");
            }
        } catch (Exception e) {
            closeStreams();
            log.error("Error has occurred: " + e + ". Request=" + request, e);
        }

        return response;
    }
}
