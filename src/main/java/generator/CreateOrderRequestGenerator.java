package generator;

import dto.OrderCreateRequest;
import java.util.List;

public class CreateOrderRequestGenerator {

    public static OrderCreateRequest getOneIngredient() {
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredients(List.of("61c0c5a71d1f82001bdaaa6f"));
        return orderCreateRequest;
    }
}
