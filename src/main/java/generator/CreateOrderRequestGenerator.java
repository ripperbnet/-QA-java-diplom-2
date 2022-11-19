package generator;

import dto.OrderCreateRequest;

import java.util.List;

public class CreateOrderRequestGenerator {

    public static OrderCreateRequest getIngredients(List<String> ingredientsId) {
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredients(ingredientsId);
        return orderCreateRequest;
    }
}
