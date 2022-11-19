import client.OrderClient;
import client.UserClient;
import dto.OrderCreateRequest;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static generator.CreateOrderRequestGenerator.getIngredients;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderCreateTest {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

    private List<String> ingredientsId;

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
    @DisplayName("Создание заказа со всеми ингредиентами, пользователь авторизован")
    @Description("Позитивный тест ручки /api/orders")
    public void orderWithOneIngredientShouldBeCreated() {
        UserCreateRequest randomUser = getRandomUser();
        token = userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");;

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        userClient.loginUser(userLoginRequest, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue());

        ingredientsId = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("data._id", Matchers.notNullValue())
                .extract()
                .path("data._id");

        OrderCreateRequest randomOrder = getIngredients(ingredientsId);
        orderClient.createOrder(randomOrder, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Антарианский био-марсианский астероидный флюоресцентный альфа-сахаридный spicy минеральный экзо-плантаго метеоритный люминесцентный традиционный-галактический бессмертный space краторный фалленианский бургер"));
    }


    @Test
    @DisplayName("Создание заказа со всеми ингредиентами пользователь не авторизован")
    @Description("Позитивный тест ручки /api/orders")
    public void orderShouldBeCreatedWithUnauthorizedUser() {
        ingredientsId = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("data._id", Matchers.notNullValue())
                .extract()
                .path("data._id");

        OrderCreateRequest randomOrder = getIngredients(ingredientsId);
        orderClient.createOrderUnauthorized(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Антарианский био-марсианский астероидный флюоресцентный альфа-сахаридный spicy минеральный экзо-плантаго метеоритный люминесцентный традиционный-галактический бессмертный space краторный фалленианский бургер"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным id ингредиента")
    @Description("Негативный тест ручки /api/orders")
    public void orderShouldBeNotCreatedWithInvalidIngredient() {
        UserCreateRequest randomUser = getRandomUser();
        token = userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");;

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        userClient.loginUser(userLoginRequest, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue());

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
        token = userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");;

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        userClient.loginUser(userLoginRequest, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue());

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setIngredients(null);
        orderClient.createOrder(orderCreateRequest, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}

