package generator;

import dto.OrderCreateRequest;

import java.util.List;

public class CreateOrderRequestGenerator {

    public static OrderCreateRequest getOneIngredient(String ingredientId) {
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredient(ingredientId);
        return orderCreateRequest;
    }

    public static OrderCreateRequest getIngredients(List<String> ingredientsId) {
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredients(ingredientsId);
        return orderCreateRequest;
    }
}
