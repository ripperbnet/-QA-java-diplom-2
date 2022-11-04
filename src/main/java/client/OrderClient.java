package client;

import dto.OrderCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class OrderClient extends RestClient {

    public final String API_ORDERS = "/api/orders";

    @Step
    public ValidatableResponse createOrder(OrderCreateRequest orderCreateRequest) {
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
