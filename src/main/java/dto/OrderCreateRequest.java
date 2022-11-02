package dto;

import java.util.ArrayList;
import java.util.List;

public class OrderCreateRequest {


    List<String> ingredients = new ArrayList<>();

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
