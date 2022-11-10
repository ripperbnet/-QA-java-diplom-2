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

import static generator.CreateOrderRequestGenerator.getOneIngredient;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrdersDataTest {

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
    @DisplayName("Получение списка заказов под авторизованным пользователем")
    @Description("Позитивный тест ручки /api/orders")
    public void orderListShouldBeDisplayed() {
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

        orderClient.getOrderData(token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("orders._id", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов под неавторизованным пользователем")
    @Description("Негативный тест /api/orders ручки")
    public void OrderListShouldBeNotDisplayed() {
        orderClient.getOrderDataWithoutToken()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
