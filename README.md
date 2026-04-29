# Campus Sensor Management System
### Module: 5COSC022W - Client Server Architectures
### Student: Poshana Rai
### University of Westminster

---

## About This Project
This project is a RESTful web service developed using JAX-RS with the 
Jersey framework and an embedded Grizzly HTTP server. The system is 
designed to manage campus rooms and sensors, allowing facilities teams 
to monitor and control sensor devices installed across the university 
buildings.

---

## Tools and Technologies
- Java 11
- JAX-RS with Jersey 2.41
- Grizzly Embedded HTTP Server
- Apache Maven
- In-memory HashMaps for data storage

---

## How to Run the Project

### What You Need
- Java 11 or above installed
- Maven 3.6 or above installed

### Instructions
1. Clone this repository:
git clone https://github.com/poshanarai/SmartCampusAPI.git
2. Open terminal and go to project folder:
cd SmartCampusAPI
3. Build using Maven:
mvn clean install
4. Start the server:
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"
5. The API will be available at:
http://localhost:8081/api/v1

---

## Sample curl Commands

### 1. Check API is running
curl -X GET http://localhost:8081/api/v1

### 2. Add a new room
curl -X POST http://localhost:8081/api/v1/rooms 
-H "Content-Type: application/json" 
-d '{"id":"CS-101","name":"Computer Science Lab","capacity":40}'

### 3. View all rooms
curl -X GET http://localhost:8081/api/v1/rooms

### 4. Register a new sensor
curl -X POST http://localhost:8081/api/v1/sensors 
-H "Content-Type: application/json" 
-d '{"id":"CO2-001","type":"CO2","status":"ACTIVE","currentValue":400.0,"roomId":"CS-101"}'

### 5. Filter sensors by type
curl -X GET http://localhost:8081/api/v1/sensors?type=CO2

### 6. Record a sensor reading
curl -X POST http://localhost:8081/api/v1/sensors/CO2-001/readings 
-H "Content-Type: application/json" 
-d '{"value":450.0}'

### 7. Get reading history
curl -X GET http://localhost:8081/api/v1/sensors/CO2-001/readings

### 8. Try deleting a room that has sensors
curl -X DELETE http://localhost:8081/api/v1/rooms/CS-101

---

## Report - Answers to Questions

### Part 1.1 - JAX-RS Resource Lifecycle
Each time a request comes in to the server, JAX-RS creates a brand new 
instance of the resource class to handle it. This is called per-request 
lifecycle. Because each request gets its own new object, any data stored 
in regular instance variables would disappear after the request finishes. 
To keep data available across multiple requests, I used static HashMaps 
to store rooms, sensors and readings. Static variables belong to the class 
rather than any single object, so the data stays in memory as long as the 
server is running. In a situation where many users are sending requests at 
the same time, using a regular HashMap could cause data corruption. To handle 
this safely, a ConcurrentHashMap would be a better choice since it is built 
to work with multiple threads at once.

### Part 1.2 - HATEOAS
HATEOAS is short for Hypermedia as the Engine of Application State. The idea 
is that when the server sends back a response, it also includes links showing 
the client what actions or resources are available next. For example, my 
discovery endpoint at GET /api/v1 returns links to /api/v1/rooms and 
/api/v1/sensors. This is helpful for developers because they do not need to 
read through pages of documentation just to figure out what URLs to call. 
Instead, they can simply follow the links in the responses, similar to 
clicking links on a website. It also means if URLs change in the future, 
clients that follow links will automatically get the updated paths rather 
than breaking.

### Part 2.1 - IDs vs Full Objects in List Responses
When an API returns only a list of IDs, the response is very small and fast 
to send. However, the client then needs to make a separate request for each 
ID to get the details, which means many more network calls. Returning full 
objects gives the client everything it needs in a single response, which 
reduces the number of requests needed. The downside is that the response 
can be very large if there are many rooms. For a small campus system, 
returning full objects makes more sense. For a very large system with 
thousands of rooms, returning only IDs with pagination would keep things 
efficient and fast.

### Part 2.2 - Is DELETE Idempotent?
Yes, the DELETE operation is idempotent in this implementation. This means 
sending the same DELETE request multiple times will always lead to the same 
end result. The first time a DELETE is sent for a room that exists, the room 
gets removed and the server returns 200 OK. If the same request is sent again, 
the room is already gone so the server returns 404 Not Found. Even though the 
response code is different, the important thing is that the room does not 
exist in both cases, which is the same final outcome. This follows the REST 
standard definition of idempotency where the state of the system ends up the 
same regardless of how many times the request is made.

### Part 3.1 - Wrong @Consumes Format
The @Consumes(MediaType.APPLICATION_JSON) annotation tells the JAX-RS 
framework that this endpoint will only accept requests where the data is 
sent in JSON format. If a client sends a request using a different content 
type such as text/plain or application/xml, the framework will automatically 
reject it before the request even reaches the method code. The server sends 
back an HTTP 415 Unsupported Media Type response to let the client know the 
format is not accepted. This protects the API from receiving data it cannot 
process and makes the expected input format clear to anyone using the API.

### Part 3.2 - Query Param vs Path Param for Filtering
Using a query parameter like /api/v1/sensors?type=CO2 is the better approach 
compared to putting the filter value in the URL path like 
/api/v1/sensors/type/CO2. Query parameters are optional by design, which 
means clients can choose to filter or not without needing a different URL. 
Path parameters are meant to identify a specific resource, not to filter 
a collection of resources. Query parameters also make it easy to combine 
multiple filters together without having to redesign the URL structure. 
This keeps the API clean and easier to understand.

### Part 4.1 - Sub-Resource Locator Pattern Benefits
The Sub-Resource Locator pattern allows large APIs to be split into smaller, 
more focused classes instead of putting everything into one giant file. In 
this project, when a request comes in for sensor readings, the SensorResource 
class passes control over to a separate SensorReadingResource class using a 
locator method. This means each class only handles one area of the API, making 
the code easier to read and maintain. If changes need to be made to how 
readings work, only the SensorReadingResource class needs to be updated. 
In a large API with many resources and nested paths, keeping everything in 
one class would make the code very long and confusing to work with.

### Part 5.2 - Why 422 over 404
A 404 error means the resource being requested could not be found, which 
would suggest the /sensors endpoint itself does not exist. However in this 
case the endpoint is working correctly, the problem is that the roomId 
value inside the request body refers to a room that does not exist in the 
system. The HTTP 422 Unprocessable Entity status is a much better fit 
because it tells the client that the request arrived and was understood, 
but the data inside it failed a validation check. It gives the client much 
clearer feedback about what went wrong and how to correct it.

### Part 5.4 - Security Risks of Exposing Stack Traces
When a Java stack trace is shown to external users, it reveals a lot of 
information about the internal workings of the application. This includes 
class names, method names, line numbers, and the versions of frameworks 
and libraries being used. An attacker can use this to look up known 
security weaknesses in those specific versions and then launch targeted 
attacks against the system. The GlobalExceptionMapper in this project 
prevents this by intercepting all unexpected errors and returning only 
a safe generic message to the client, while the full error details are 
logged privately on the server.

### Part 5.5 - Why Filters over Manual Logging
A JAX-RS filter is a much better way to handle logging compared to manually 
writing Logger.info() calls inside every single resource method. With a 
filter, the logging code is written once in one place and it runs automatically 
for every single request and response without needing to touch any other file. 
If I had added logging manually to each method, the same code would be 
repeated dozens of times across the project, making it harder to maintain. 
It also keeps the resource classes clean and focused on their main job 
rather than mixing in logging concerns.

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1 | API Info |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a room |
| GET | /api/v1/rooms/{roomId} | Get one room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room |
| GET | /api/v1/sensors | Get all sensors |
| POST | /api/v1/sensors | Create a sensor |
| GET | /api/v1/sensors/{sensorId} | Get one sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get readings |
| POST | /api/v1/sensors/{sensorId}/readings | Add a reading |