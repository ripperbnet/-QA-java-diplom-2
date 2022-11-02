package client;

import dto.OrderCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class OrderClient extends RestClient {

    public final String ORDER_CREATE = "/api/orders";

    @Step
    public ValidatableResponse createOrder(OrderCreateRequest orderCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(orderCreateRequest)
                .post(ORDER_CREATE)
                .then();
    }
}
