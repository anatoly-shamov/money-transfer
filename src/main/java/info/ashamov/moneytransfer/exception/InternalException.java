package info.ashamov.moneytransfer.exception;

import javax.ws.rs.core.Response;

public class InternalException extends RuntimeException {

    private Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public InternalException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }
}
