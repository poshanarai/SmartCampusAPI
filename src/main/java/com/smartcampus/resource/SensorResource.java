package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    // Shared in-memory storage for sensors
    public static Map<String, Sensor> sensorStorage = new HashMap<>();

    // GET /api/v1/sensors - Get all sensors
    // Optional filter by type
    @GET
    public Response fetchAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(sensorStorage.values());

        // Apply type filter if provided
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = new ArrayList<>();
            for (Sensor sensor : allSensors) {
                if (sensor.getType().equalsIgnoreCase(type)) {
                    filtered.add(sensor);
                }
            }
            return Response.ok(filtered).build();
        }
        return Response.ok(allSensors).build();
    }

    // POST /api/v1/sensors - Register a new sensor
    @POST
    public Response registerSensor(Sensor sensor) {
        // Check sensor ID is provided
        if (sensor.getId() == null || sensor.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Sensor ID is required\"}")
                    .build();
        }

        // Check room ID is provided
        if (sensor.getRoomId() == null || sensor.getRoomId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }

        // Verify room exists
        if (!RoomResource.roomStorage.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "The room with ID "
                + sensor.getRoomId()
                + " was not found in the system.");
        }

        // Check sensor does not already exist
        if (sensorStorage.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A sensor with this ID already exists\"}")
                    .build();
        }

        // Set default status if missing
        if (sensor.getStatus() == null || sensor.getStatus().isEmpty()) {
            sensor.setStatus("ACTIVE");
        }

        // Save sensor and link to room
        sensorStorage.put(sensor.getId(), sensor);
        RoomResource.roomStorage.get(sensor.getRoomId())
                .getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    // GET /api/v1/sensors/{sensorId} - Get a specific sensor
    @GET
    @Path("/{sensorId}")
    public Response fetchSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = sensorStorage.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with ID: "
                            + sensorId + "\"}")
                    .build();
        }
        return Response.ok(sensor).build();
    }

    // Sub-resource locator for readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(
            @PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}