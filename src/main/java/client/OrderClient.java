package client;

import dto.OrderCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import java.util.List;

public class OrderClient extends RestClient {

    public final String API_ORDERS = "/api/orders";

    public final String API_INGREDIENTS = "/api/ingredients";

    @Step
    public ValidatableResponse getIngredients() {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .get(API_INGREDIENTS)
                .then();
    }

    @Step
    public ValidatableResponse createOrder(OrderCreateRequest orderCreateRequest, String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .body(orderCreateRequest)
                .post(API_ORDERS)
                .then();
    }

    @Step
    public ValidatableResponse createOrderUnauthorized(OrderCreateRequest orderCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(orderCreateRequest)
                .post(API_ORDERS)
                .then();
    }

    @Step
    public ValidatableResponse getOrderData(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .get(API_ORDERS)
                .then();
    }

    @Step
    public ValidatableResponse getOrderDataWithoutToken() {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .get(API_ORDERS)
                .then();
    }
}
