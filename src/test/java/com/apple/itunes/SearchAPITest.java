package com.apple.itunes;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class SearchAPITest {

    @BeforeAll
    public static void setup() {
        // Set the base URI for the API
        RestAssured.baseURI = "https://itunes.apple.com";
    }

    @Test
    // TC001: Verify that the search API returns relevant results for a valid query
    public void testValidSearchQuery() {
        given()
                .param("term", "jack johnson")
                .param("country", "us")
                .when()
                .get("/search") // API endpoint for search
                .then()
                .statusCode(200)
                .body("results.size()", greaterThan(0)) // Assert that there are results
                .body("results[0].country", containsString("USA")); // Assert that the first result contains the search query

    }

    @Test
    // TC002: Verify that the search API handles requests without required parameters gracefully
    // The test failed because of DEF-0002
    public void testMissingParameters() {
        given()
                // Don't specify required parameters
                .when()
                .get("/search") // API endpoint for search
                .then()
                .statusCode(400); // Assert that the status code is 400 Bad Request
    }

    @Test
    // TC003: Verify that the search API handles invalid queries gracefully
    public void testInvalidQuery() {
        given()
                .param("term", "jack johnson")
                .param("country", "yyy") // Specify an invalid query
                .when()
                .get("/search") // API endpoint for search
                .then()
                .statusCode(400); // Assert that the status code is 400 Bad Request
    }

    @Test
    // TC004: Verify that the search API supports various parameters
    public void testSearchWithVariousParameters() {
        given()
                .param("term", "jack johnson")
                .param("country", "us")
                .param("media", "music")
                .param("entity", "song")
                .param("attribute", "songTerm")
                .param("limit", "7")
                .param("lang", "en_us")
                .param("version", "1")
                .param("explicit", "Yes")
                .when()
                .get("/search") // API endpoint for search
                .then()
                .statusCode(200) // Assert that the status code is 200 OK
                .body("results.size()", greaterThan(0)) // Assert that there are results
                .body("resultCount", equalTo(7))
                .body("results[0].country", equalTo("USA")) // Assert that the results match the specified parameters
                .body("results[0].mediaType", equalTo("song")); // Assert that the results match the specified parameters
    }

    @Test
    // TC005: Verify that each search parameter works as expected and influences the search results appropriately
    public void testSearchWithParameters() {
        given()
                .param("term", "jack johnson")
                .param("entity", "musicVideo")
                .when()
                .get("/search") // API endpoint for search
                .then()
                .statusCode(200) // Assert that the status code is 200 OK
                .body("results.size()", greaterThan(0)) // Assert that there are results
                .body("results[0].kind", equalTo("music-video")); // Assert that the results match the specified parameter
    }

    @Test
    // TC006: Verify that the search API returns proper error responses for invalid requests
    // The test failed because of DEF-0002
    public void testInvalidRequests() {
        // Don't specify required parameters
        given()
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .body("errorMessage", containsString("Query parameter 'term' is required"));

        // Test invalid country
        given()
                .param("term", "jim")
                .param("country", "invalidCountry")
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .body("errorMessage", containsString("Invalid value(s) for key(s): [country]"));
    }

    @Test
    // TC007: Verify that error responses include meaningful error messages and appropriate HTTP status codes.
    // The test failed because of DEF-0003
    public void testErrorResponses() {

        // Test invalid limit
        given()
                .param("term", "jim")
                .param("limit", "invalidLimit")
                .when()
                .get("/search")
                .then()
                .statusCode(400)
                .body("errorMessage", containsString("Invalid value(s) for key(s): [limit]"));

    }
}
