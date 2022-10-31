package client;

import dto.CreateUserRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.path.json.JsonPath.given;

public class UserClient extends RestClient {

    public static final String USER = "/api/auth/register";

    @Step
    public ValidatableResponse createUser(CreateUserRequest createUserRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(createUserRequest)
                .post(USER)
                .then();
    }
}
