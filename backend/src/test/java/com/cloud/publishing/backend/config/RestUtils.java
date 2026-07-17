package com.cloud.publishing.backend.config;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;

public class RestUtils {
    private static final String BASE_URL = TestConfig.getBaseUrl();

    private RestUtils() {
    }

    public static Response get(String url, String token) {
        return prepareRequest(token)
                .when()
                .get(url);
    }

    public static Response post(String url, Object body, String token) {
        return prepareRequest(token)
                .body(body)
                .when()
                .post(url);
    }

    public static void verifyStatus(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
                .as("Unexpected response status code")
                .isEqualTo(expectedStatusCode);
    }

    private static RequestSpecification prepareRequest(String token) {
        RequestSpecification request = RestAssured.given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON);
        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return request;
    }
}