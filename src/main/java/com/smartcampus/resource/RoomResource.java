package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // Shared in-memory storage for rooms
    public static Map<String, Room> roomStorage = new HashMap<>();

    // GET /api/v1/rooms - Retrieve all rooms
    @GET
    public Response fetchAllRooms() {
        List<Room> allRooms = new ArrayList<>(roomStorage.values());
        return Response.ok(allRooms).build();
    }

    // POST /api/v1/rooms - Add a new room
    @POST
    public Response addRoom(Room room) {
        if (room.getId() == null || room.getId().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required\"}")
                    .build();
        }
        if (roomStorage.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A room with this ID already exists\"}")
                    .build();
        }
        roomStorage.put(room.getId(), room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // GET /api/v1/rooms/{roomId} - Get a specific room
    @GET
    @Path("/{roomId}")
    public Response fetchRoom(@PathParam("roomId") String roomId) {
        Room room = roomStorage.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No room found with ID: "
                            + roomId + "\"}")
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /api/v1/rooms/{roomId} - Remove a room
    @DELETE
    @Path("/{roomId}")
    public Response removeRoom(@PathParam("roomId") String roomId) {
        Room room = roomStorage.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"No room found with ID: "
                            + roomId + "\"}")
                    .build();
        }
        // Block deletion if room still has sensors
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room " + roomId
                + ". It still has "
                + room.getSensorIds().size()
                + " sensor(s) attached to it.");
        }
        roomStorage.remove(roomId);
        return Response.ok()
                .entity("{\"message\":\"Room "
                        + roomId + " has been deleted\"}")
                .build();
    }
}