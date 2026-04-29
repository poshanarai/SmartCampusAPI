package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class RoomNotEmptyExceptionMapper
        implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 409);
        errorDetails.put("error", "Conflict");
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("hint",
                "Remove all sensors from the room first "
                + "before attempting to delete it.");

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}