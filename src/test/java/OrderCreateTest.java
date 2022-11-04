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

import static generator.CreateOrderRequestGenerator.getRandomOrder;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class OrderCreateTest {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

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
    @DisplayName("Creating an order with 1 ingredient")
    @Description("Positive test of api /api/orders endpoint")
    public void orderWithOneIngredientShouldBeCreated() {
        UserCreateRequest randomUser = getRandomUser();
        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("refreshToken");

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);

        token = userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .extract()
                .path("accessToken");


        OrderCreateRequest randomOrder = getRandomOrder();

        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Бессмертный бургер"));
    }

    @Test
    @DisplayName("Creating an order with unauthorized user")
    @Description("Positive test of api /api/orders endpoint")
    public void orderShouldBeCreatedWithUnauthorizedUser() {
        OrderCreateRequest randomOrder = getRandomOrder();

        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

    }

    @Test
    @DisplayName("Creating an order with invalid ingredient")
    @Description("Negative test of api /api/orders endpoint")
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
        orderCreateRequest.setIngredients(List.of("61c0c5a71d1f82221bdaaa6f"));
        orderClient.createOrder(orderCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("One or more ids provided are incorrect"));

    }

    @Test
    @DisplayName("Creating an order without ingredients")
    @Description("Negative test of api /api/orders endpoint")
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
        orderCreateRequest.setIngredients(List.of());
        orderClient.createOrder(orderCreateRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}

