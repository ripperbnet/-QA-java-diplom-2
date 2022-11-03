import client.OrderClient;
import client.UserClient;
import dto.OrderCreateRequest;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.List;

import static generator.CreateOrderRequestGenerator.getRandomOrder;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;


public class OrderCreateTest {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

    private String refreshToken;


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


    @Test // заказ с одним валидным ингредиентом
    public void orderShouldBeCreated() {
        UserCreateRequest randomUser = getRandomUser();
        refreshToken = userClient.createUser(randomUser)
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

    @Test // Заказ с невалидным ингредиентом
    public void orderShouldNotBeCreated() {
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

    @Test // Попытка заказа неавторизованным пользователем
    public void orderWithUnauthorizedUser() {
        OrderCreateRequest randomOrder = getRandomOrder();

        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK);

    }
}

