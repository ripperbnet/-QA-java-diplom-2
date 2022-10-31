package client;

import dto.UserCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import static io.restassured.path.json.JsonPath.given;

public class UserClient extends RestClient {

    public static final String USER = "/api/auth/register";

    public static final String USER_LOGIN = "/api/auth/login";

    @Step
    public ValidatableResponse createUser(UserCreateRequest userCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userCreateRequest)
                .post(USER)
                .then();
    }

    @Step
    public ValidatableResponse loginUser(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .auth().oauth2(token)
                .post(USER_LOGIN)
                .then();
    }
}
