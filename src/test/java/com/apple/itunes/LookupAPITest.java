package com.apple.itunes;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class LookupAPITest {

    @BeforeAll
    public static void setup() {
        // Set the base URI for the API
        RestAssured.baseURI = "https://itunes.apple.com";
    }

    @Test
    // TC001: Verify that the search API returns relevant results for a valid query
    public void testValidSearchQuery() {
        given()
                .param("id", "909253")
                .when()
                .get("/lookup") // API endpoint for search
                .then()
                .statusCode(200)
                .body("results.size()", greaterThan(0)) // Assert that there are results
                .body("results[0].artistId", equalTo(909253)); // Assert that the first result contains the lookup query

    }
}
