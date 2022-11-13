package client;

import dto.OrderCreateRequest;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

public class OrderClient extends RestClient {

    private static final String API_ORDERS = "/api/orders";

    private static final String API_INGREDIENTS = "/api/ingredients";

    @Step("Получение списка ингредиентов")
    public ValidatableResponse getIngredients() {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .get(API_INGREDIENTS)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(OrderCreateRequest orderCreateRequest, String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .body(orderCreateRequest)
                .post(API_ORDERS)
                .then();
    }

    @Step("Создание заказа без авторизации")
    public ValidatableResponse createOrderUnauthorized(OrderCreateRequest orderCreateRequest) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .body(orderCreateRequest)
                .post(API_ORDERS)
                .then();
    }

    @Step("Получение данных заказа")
    public ValidatableResponse getOrderData(String token) {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .header("authorization", token)
                .get(API_ORDERS)
                .then();
    }

    @Step("Получение данных заказа без авторизации")
    public ValidatableResponse getOrderDataWithoutToken() {
        return RestAssured.given()
                .spec(getDefaultRequestSpec())
                .get(API_ORDERS)
                .then();
    }
}
