package com.aipaca.rumbo.tutorjourneys;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties={"spring.profiles.active=local", "app.profiles.demo.enabled=true",
                "app.demo.enabled=true", "spring.jpa.hibernate.ddl-auto=update", "server.port=0"})
class TutorJourneyHttpIntegrationTests {
    @Autowired Environment environment;

    private HttpResponse<String> send(String method, String path, String role, String json) throws Exception {
        int port=environment.getRequiredProperty("local.server.port",Integer.class);
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path))
            .timeout(Duration.ofSeconds(15));
        if(role!=null)builder.header("X-Rumbo-Demo-Role",role);
        if(json!=null)builder.header("Content-Type","application/json");
        if("GET".equals(method))builder.GET();
        else builder.method(method,HttpRequest.BodyPublishers.ofString(json==null?"":json));
        try(var client=HttpClient.newHttpClient()) {
            return client.send(builder.build(),HttpResponse.BodyHandlers.ofString());
        }
    }
    private HttpResponse<String> get(String path,String role) throws Exception {
        return send("GET",path,role,null);
    }
    private HttpResponse<String> post(String path,String role,String json) throws Exception {
        return send("POST",path,role,json);
    }
    private void status(int wanted,HttpResponse<String> response) {
        assertEquals(wanted,response.statusCode(),response.body());
    }
    private String value(String body,boolean uuid) {
        String regex=uuid?"\"id\"\\s*:\\s*\"([a-fA-F0-9-]{36})\"":"\"id\"\\s*:\\s*(\\d+)";
        var matcher=Pattern.compile(regex).matcher(body);
        assertTrue(matcher.find(),body);
        return matcher.group(1);
    }

    @Test void familyCanObservePublishedRouteAndExplicitStudentArrival() throws Exception {
        long school=Long.parseLong(value(get("/api/v1/schools","tutor").body(),false));
        long driver=Long.parseLong(value(get("/api/v1/local-demo/drivers","tutor").body(),false));
        var student=post("/api/v1/students","tutor",
            "{\"firstName\":\"Prueba\",\"lastName\":\"Llegada\","
            +"\"birthDate\":\"2018-04-07\",\"schoolId\":"+school+",\"relationshipType\":\"OTHER\"}");
        status(201,student);
        String studentId=value(student.body(),false);
        var enrolled=post("/api/v1/service-enrollments","tutor",
            "{\"studentId\":"+studentId+",\"driverProfileId\":"+driver+"}");
        status(201,enrolled);
        String enrollmentId=value(enrolled.body(),false);
        var waiting=get("/api/v1/service-enrollments/drivers/me","driver");
        status(200,waiting);
        assertTrue(waiting.body().contains("\"id\":"+enrollmentId),waiting.body());
        status(200,get("/api/v1/service-enrollments/drivers/me/students","driver"));
        status(200,post("/api/v1/service-enrollments/"+enrollmentId+"/accept","driver","{}"));

        var vehicle=post("/api/v1/vehicles","driver",
            "{\"brand\":\"Toyota\",\"model\":\"Hiace\",\"licensePlate\":\"RMB"
            +UUID.randomUUID().toString().substring(0,7)+"\",\"capacity\":12,\"year\":2024}");
        status(201,vehicle);
        String vehicleId=value(vehicle.body(),false);
        String stopId=UUID.randomUUID().toString();
        var route=post("/api/v1/routes","driver",
            "{\"name\":\"Ruta escolar mañana\",\"shift\":\"MORNING\",\"vehicleId\":\""+vehicleId+"\","
            +"\"stops\":[{\"id\":\""+stopId+"\",\"address\":\"Av. Escolar 123\"}],"
            +"\"schedule\":[{\"dayOfWeek\":\""+LocalDate.now().getDayOfWeek()+"\","
            +"\"departureTime\":\"07:30\"}],\"scheduleConfirmed\":true}");
        status(201,route);
        String routeId=value(route.body(),true);
        status(200,send("PUT","/api/v1/routes/"+routeId+"/assignments","driver",
            "[{\"studentId\":\""+studentId+"\",\"stopId\":\""+stopId+"\"}]"));
        status(200,send("PATCH","/api/v1/routes/"+routeId+"/status","driver","{\"status\":\"PUBLISHED\""));
        var parentRoute=get("/api/v1/tutor/journeys","tutor");
        status(200,parentRoute);
        assertTrue(parentRoute.body().contains(routeId),parentRoute.body());

        var planned=post("/api/v1/routes/"+routeId+"/trips","driver",
            "{\"date\":\""+LocalDate.now()+"\"}");
        status(201,planned);
        String tripId=value(planned.body(),true);
        status(200,send("PATCH","/api/v1/trips/"+tripId+"/start","driver","{}"));
        status(200,send("PATCH","/api/v1/trips/"+tripId+"/stops/"+stopId+"/served","driver","{}"));
        var before=get("/api/v1/tutor/journeys","tutor");
        status(200,before);
        assertTrue(before.body().contains("\"studentStatus\":\"STOP_SERVED\""),before.body());
        status(200,send("PATCH","/api/v1/trips/"+tripId+"/passengers/"+studentId+"/arrived","driver","{}"));
        var after=get("/api/v1/tutor/journeys","tutor");
        status(200,after);
        assertTrue(after.body().contains("\"studentStatus\":\"ARRIVED\""),after.body());
        var notices=get("/api/v1/tutor/notifications","tutor");
        status(200,notices);
        assertTrue(notices.body().contains("Llegada confirmada"),notices.body());
        assertEquals(403,get("/api/v1/tutor/notifications","driver").statusCode());
    }
}
