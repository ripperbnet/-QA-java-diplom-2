package dto;

import java.util.ArrayList;
import java.util.List;

public class OrderCreateRequest {

    List<String> ingredient = new ArrayList<>();



    public List<String> getIngredient() {
        return ingredient;
    }

    public void setIngredients(List<String> ingredient) {
        this.ingredient = ingredient;
    }




    private String ingredients;

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
