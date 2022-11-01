package client;

import dto.UserCreateRequest;
import dto.UserLoginRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.path.json.JsonPath.given;

public class UserClient extends RestClient {

    public static final String USER = "/api/auth/register";

    public static final String USER_LOGIN = "/api/auth/login/";


    public static final String USER_DELETE = "/api/auth/user";

    public static final String USER_GET = "/api/auth/user";

    @Step
    public ValidatableResponse createUser(UserCreateRequest userCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userCreateRequest)
                .post(USER)
                .then();
    }

    @Step
    public ValidatableResponse loginUser(UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userLoginRequest)
                .post(USER_LOGIN)
                .then();
    }

    @Step
    public ValidatableResponse deleteUser(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .delete(USER_DELETE)
                .then();
    }



    @Step
    public ValidatableResponse tokenAuth(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .get(USER_GET)
                .then();
    }
}
