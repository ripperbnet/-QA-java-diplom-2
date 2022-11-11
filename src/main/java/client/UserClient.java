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


    @Step("Создание пользователя")
    public ValidatableResponse createUser(UserCreateRequest userCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userCreateRequest)
                .post(AUTH_REGISTER)
                .then();
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(UserLoginRequest userLoginRequest, String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .body(userLoginRequest)
                .post(AUTH_LOGIN)
                .then();
    }

    @Step("Логин пользователя без токена")
    public ValidatableResponse loginUserWithoutToken(UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userLoginRequest)
                .post(AUTH_LOGIN)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .delete(AUTH_USER)
                .then();
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse updateUser(String token, UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .body(userLoginRequest)
                .patch(AUTH_USER)
                .then();
    }

    @Step("Обновления данных пользователя без токена")
    public ValidatableResponse updateUserWithoutToken(UserLoginRequest userLoginRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(userLoginRequest)
                .patch(AUTH_USER)
                .then();
    }
}
