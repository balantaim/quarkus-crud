package com.martinatanasov.controllers;

import com.martinatanasov.models.UserDetailsDto;
import com.martinatanasov.results.UserResult;
import com.martinatanasov.services.UserService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;

import static io.restassured.RestAssured.given;

@QuarkusTest
class UserControllerTest {

    @InjectMock
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.when(userService.findByEmail(Mockito.anyString())).thenReturn(
                new UserResult.Success(new UserDetailsDto("123",
                        "abv@abv.bg",
                        "Ami go",
                        new HashSet<>(),
                        LocalDateTime.now())
                )
        );
    }

    @Test
    void getByEmail() {
        Response response = given()
                .when().get("/api/v1/users/email/abv@abv.bg")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("123", jsonPath.getString("userId"));
        Assertions.assertEquals("abv@abv.bg", jsonPath.getString("email"));
        Assertions.assertNotNull(jsonPath.getString("fullName"));
        Assertions.assertNotNull(jsonPath.getString("createdDate"));
    }

}