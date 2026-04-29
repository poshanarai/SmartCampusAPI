package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApiInfo() {
        String json = "{"
                + "\"name\":\"Campus Sensor Management System\","
                + "\"version\":\"1.0\","
                + "\"developer\":\"Poshana Rai\","
                + "\"module\":\"5COSC022W - Client Server Architectures\","
                + "\"contact\":\"raiposhana@gmail.com\","
                + "\"status\":\"running\","
                + "\"links\":{"
                + "\"rooms\":\"/api/v1/rooms\","
                + "\"sensors\":\"/api/v1/sensors\""
                + "}"
                + "}";

        return Response.ok(json).build();
    }
}