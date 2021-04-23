package org.hit.internetprogramming.haim.matrix.server.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hit.internetprogramming.haim.matrix.common.comms.HttpStatus;

import java.io.IOException;

/**
 * An exception we use in order to return error responses to an HTTP client (e.g. browser) as HTML text, rather than json
 * @author Haim Adrian
 * @since 17-Apr-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WebException extends IOException {
    private final HttpStatus httpStatus;

    public WebException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return httpStatus.getCode() + " " + httpStatus.name() + ": " + message;
    }
}

