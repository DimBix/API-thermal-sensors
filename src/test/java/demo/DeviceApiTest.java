package demo;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import it.univr.track.SmartTrackApplication;
import it.univr.track.entity.Device;
import it.univr.track.entity.UserRegistered;
import it.univr.track.entity.enumeration.Origin;
import it.univr.track.repository.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@SpringBootTest(classes = SmartTrackApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class DeviceApiTest {
    @Autowired
    UserService userService;

    @BeforeEach
    public void beforeAll() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void deviceApiPing() {
        when()
                .get("/api/test")
                .then()
                .statusCode(200)
                .contentType("text/plain")
                .body(Matchers.is("test"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void addNewDevice() {
        Optional<UserRegistered> user = userService.getUserByUsername("AdaL");
        if (user.isPresent()) {
            Device device = new Device(null, user.get(), true, true, Origin.ASSEMBLED, false, 10000f);

            given()
                    .contentType(ContentType.JSON)
                    .body(device)
                    .post("/api/device")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body(Matchers.is("true"));

            given()
                    .get("/api/device/8")
                    .then()
                    .statusCode(200)
                    .body("price", Matchers.is(10000f));
        } else {
            throw new RuntimeException("User AdaL not found");
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void deleteDevice() {
        given()
                .contentType(ContentType.JSON)
                .param("device", 3L)
                .delete("/api/device")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body(Matchers.is("true"));

        given()
                .get("/api/device/3")
                .then()
                .statusCode(404);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void getAllDevicesByUser() {
        Optional<UserRegistered> user1 = userService.getUserByUsername("JhonD");
        Optional<UserRegistered> user2 = userService.getUserByUsername("AdaL");
        if (user1.isPresent() && user2.isPresent()) {
            Device deviceJhon = new Device(null, user1.get(), true, true, Origin.ASSEMBLED, false, 10000f);
            Device deviceAda = new Device(null, user2.get(), true, false, Origin.ASSEMBLED, false, 20000f);

            given()
                    .param("username", user1.get().getUsername())
                    .get("/api/devices")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("", Matchers.hasSize(1));

            given()
                    .contentType(ContentType.JSON)
                    .body(deviceJhon)
                    .post("/api/device")
                    .then()
                    .statusCode(200);

            given()
                    .contentType(ContentType.JSON)
                    .body(deviceAda)
                    .post("/api/device")
                    .then()
                    .statusCode(200);

            given()
                    .param("username", user1.get().getUsername())
                    .get("/api/devices")
                    .then()
                    .statusCode(200)
                    .contentType("application/json")
                    .body("", Matchers.hasSize(2));
        } else {
            throw new RuntimeException("User AdaL and JhonD not found");
        }
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void updateDevice() {
        Device device = given()
                .when()
                .get("/api/device/3")
                .then()
                .statusCode(200)
                .extract().body().as(Device.class);

        device.setPrice(10000f);

        given()
                .contentType(ContentType.JSON)
                .body(device)
                .put("/api/device")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .body(device)
                .get("/api/device/3")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("price", Matchers.is(10000f));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void unauthorizedAccess() {
        given()
                .param("username", "FakeUsername")
                .get("/api/devices")
                .then()
                .statusCode(401);
    }
}
