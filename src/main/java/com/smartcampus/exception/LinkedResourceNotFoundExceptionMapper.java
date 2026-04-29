package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper
        implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", 422);
        errorDetails.put("error", "Unprocessable Entity");
        errorDetails.put("message", exception.getMessage());
        errorDetails.put("hint",
                "Make sure the Room ID you provided "
                + "actually exists in the system.");

        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorDetails)
                .build();
    }
}