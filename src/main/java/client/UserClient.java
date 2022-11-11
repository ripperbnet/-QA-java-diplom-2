package client;

import dto.UserCreateRequest;
import dto.UserLoginRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class UserClient extends RestClient {

    public static final String AUTH_REGISTER = "/api/auth/register";

    public static final String AUTH_LOGIN = "/api/auth/login/";

    public static final String AUTH_USER = "/api/auth/user";


    @Step
    public ValidatableResponse createUser(UserCreateRequest userCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userCreateRequest)
                .post(AUTH_REGISTER)
                .then();
    }

    @Step
    public ValidatableResponse loginUser(UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userLoginRequest)
                .post(AUTH_LOGIN)
                .then();
    }

    @Step
    public ValidatableResponse deleteUser(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .delete(AUTH_USER)
                .then();
    }

    @Step
    public ValidatableResponse updateUser(String token, UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .body(userLoginRequest)
                .patch(AUTH_USER)
                .then();
    }

    @Step
    public ValidatableResponse updateUserWithoutToken(UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userLoginRequest)
                .patch(AUTH_USER)
                .then();
    }
}
