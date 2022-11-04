package client;

import dto.OrderCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class OrderClient extends RestClient {

    public final String CREATE = "/api/orders";

    @Step
    public ValidatableResponse createOrder(OrderCreateRequest orderCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(orderCreateRequest)
                .post(CREATE)
                .then();
    }
}
