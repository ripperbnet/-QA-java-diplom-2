import client.OrderClient;
import client.UserClient;
import dto.OrderCreateRequest;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static generator.CreateOrderRequestGenerator.getOneIngredient;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class OrderCreateTest {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

    private String ingredientId;



    @Before
    public void setUp() {
        orderClient = new OrderClient();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if (token != null) {
            userClient.deleteUser(token)
                    .assertThat()
                    .body("message", equalTo("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Создание заказа с одним ингредиентом, пользователь авторизован")
    @Description("Позитивный тест ручки /api/orders")
    public void orderWithOneIngredientShouldBeCreated() {
        UserCreateRequest randomUser = getRandomUser();
        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        token = userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .extract()
                .path("accessToken");

        ingredientId = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("data[0]._id", Matchers.notNullValue())
                .extract()
                .path("data[0]._id");

        OrderCreateRequest randomOrder = getOneIngredient(ingredientId);
        orderClient.createOrder(randomOrder, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Флюоресцентный бургер"));
    }


    @Test
    @DisplayName("Создание заказа под неавторизованным пользователем")
    @Description("Позитивный тест ручки /api/orders")
    public void orderShouldBeCreatedWithUnauthorizedUser() {
        ingredientId = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("data[0]._id", Matchers.notNullValue())
                .extract()
                .path("data[0]._id");

        OrderCreateRequest randomOrder = getOneIngredient(ingredientId);
        orderClient.createOrderUnauthorized(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Флюоресцентный бургер"));
    }

   @Test
    @DisplayName("Создание заказа с невалидным id ингредиента")
    @Description("Негативный тест ручки /api/orders")
    public void orderShouldBeNotCreatedWithInvalidIngredient() {
        UserCreateRequest randomUser = getRandomUser();
        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        token = userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .extract()
                .path("accessToken");

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredients(List.of("111111111111111111111111"));
        orderClient.createOrder(orderCreateRequest, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("One or more ids provided are incorrect"));

    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Негативный тест ручки /api/orders")
    public void orderShouldBeNotCreatedWithoutIngredients() {
        UserCreateRequest randomUser = getRandomUser();
        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        token = userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .extract()
                .path("accessToken");

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredient(null);
        orderClient.createOrder(orderCreateRequest, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}

