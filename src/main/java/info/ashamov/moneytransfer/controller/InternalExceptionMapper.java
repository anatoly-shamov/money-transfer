package info.ashamov.moneytransfer.controller;

import info.ashamov.moneytransfer.dto.ErrorResponse;
import info.ashamov.moneytransfer.exception.InternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InternalExceptionMapper implements ExceptionMapper<InternalException> {
    private static Logger log = LogManager.getLogger(InternalExceptionMapper.class);

    @Override
    public Response toResponse(InternalException exception) {
        log.error(exception.getMessage(), exception);
        return Response
                .status(exception.getStatus())
                .entity(new ErrorResponse(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
    }
}
