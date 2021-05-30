package org.hit.internetprogramming.eoh.common.comms;

/**
 * Some general HTTP statuses. For example:
 * <ul>
 *     <li>{@link #OK 200 OK}</li>
 *     <li>{@link #BAD_REQUEST 400 Bad Request}</li>
 *     <li>{@link #NOT_FOUND 404 Not Found}</li>
 *     <li>{@link #METHOD_NOT_ALLOWED 405 Method Not Allowed}</li>
 *     <li>{@link #INTERNAL_SERVER_ERROR 500 Internal Server Error}</li>
 * </ul>
 * @author Haim Adrian
 * @since 17-Apr-21
 */
public enum HttpStatus {
    /**
     * This status indicates a successful response.
     * @see <a href="https://httpstatuses.com/200">200 OK</a>
     */
    OK(200),

    /**
     * This status indicates that client has sent an illegal request that the server could not serve.
     * @see <a href="https://httpstatuses.com/400">400 BAD REQUEST</a>
     */
    BAD_REQUEST(400),

    /**
     * This status indicates that the requested web page (service / action) could not be found.
     * @see <a href="https://httpstatuses.com/404">404 NOT FOUND</a>
     */
    NOT_FOUND(404),

    /**
     * This status indicates that the requested method is illegal. e.g. when sending a POST request and the
     * server supports GET only.
     * @see <a href="https://httpstatuses.com/405">405 METHOD NOT ALLOWED</a>
     */
    METHOD_NOT_ALLOWED(405),

    /**
     * This status indicates that the request has timed out so its response should be discarded.
     * @see <a href="https://httpstatuses.com/408">408 TIME OUT</a>
     */
    TIME_OUT(408),

    /**
     * This status indicates that something went wrong at the server.
     * @see <a href="https://httpstatuses.com/500">500 INTERNAL SERVER ERROR</a>
     */
    INTERNAL_SERVER_ERROR(500),

    /**
     * This status indicates that the server is not available.
     * @see <a href="https://httpstatuses.com/503">503 SERVICE UNAVAILABLE</a>
     */
    SERVICE_UNAVAILABLE(503);

    /**
     * The HTTP code
     */
    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public static HttpStatus valueOf(int httpStatus) {
        HttpStatus result = null;

        for (int i = 0; (i < values().length) && (result == null); i++) {
            if (httpStatus == values()[i].code) {
                result = values()[i];
            }
        }

        return result == null ? INTERNAL_SERVER_ERROR : result;
    }

    /**
     * @return The HTTP code corresponding to this HTTP status. e.g. 500
     */
    public int getCode() {
        return code;
    }

    /**
     * @return The HTTP message corresponding to this HTTP status. e.g. "500 INTERNAL SERVER ERROR"
     */
    public String getMessage() {
        return "" + code + " " + name().replace('_', ' ');
    }
}

