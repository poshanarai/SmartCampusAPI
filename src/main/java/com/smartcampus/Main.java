package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import java.net.URI;
import java.io.IOException;

public class Main {

    public static final String BASE_URI = "http://0.0.0.0:8081/api/v1/";

    public static HttpServer startServer() {
        final ResourceConfig config = new ResourceConfig()
                .packages(
                    "com.smartcampus.resource",
                    "com.smartcampus.exception",
                    "com.smartcampus.filter"
                )
                .register(JacksonFeature.class);
        return GrizzlyHttpServerFactory.createHttpServer(
                URI.create(BASE_URI), config);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println("*********************************************");
        System.out.println(" Campus Sensor Management System is live!");
        System.out.println("*********************************************");
        System.out.println(" Endpoint : http://localhost:8081/api/v1");
        System.out.println(" Developer: Poshana Rai");
        System.out.println("*********************************************");
        System.out.println(" Press ENTER to shut down the server...");
        System.in.read();
        server.stop();
        System.out.println(" Server has been stopped.");
    }
}