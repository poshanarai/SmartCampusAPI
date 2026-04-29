package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    // Shared storage for all sensor readings
    public static Map<String, List<SensorReading>> readingStorage
            = new HashMap<>();

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    // Fetch all readings for a sensor
    @GET
    public Response fetchReadings() {
        Sensor sensor = SensorResource.sensorStorage.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with ID: "
                            + sensorId + "\"}")
                    .build();
        }
        List<SensorReading> history =
                readingStorage.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(history).build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    // Record a new reading for a sensor
    @POST
    public Response recordReading(SensorReading reading) {
        Sensor sensor = SensorResource.sensorStorage.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No sensor found with ID: "
                            + sensorId + "\"}")
                    .build();
        }

        // Block readings for sensors under maintenance
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor " + sensorId
                + " is currently under MAINTENANCE "
                + "and cannot record new readings.");
        }

        // Auto generate ID and timestamp if missing
        if (reading.getId() == null || reading.getId().isEmpty()) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Save reading
        readingStorage.computeIfAbsent(
                sensorId, k -> new ArrayList<>()).add(reading);

        // Update sensor current value
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}