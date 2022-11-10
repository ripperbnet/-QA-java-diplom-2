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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static generator.CreateOrderRequestGenerator.getIngredients;
import static generator.CreateOrderRequestGenerator.getOneIngredient;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class OrderCreateTestParameterized {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

    private List<String> order;

    private List<String> ingredientsId;

    public OrderCreateTestParameterized(List<String> order) {
        this.order = order;
    }

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

    @Parameterized.Parameters
    public static List<List<String>> order() {
        return List.of (
                List.of("data[0]._id", "data[1]._id", "data[2]._id", "data[3]._id"),
                List.of("data[4]._id", "data[5]._id", "data[6]._id"),
                List.of("data[7]._id", "data[8]._id", "data[9]._id"),
                List.of("data[10]._id", "data[11]._id"),
                List.of("data[12]._id", "data[13]._id")
        );
    }

    @Test
    @DisplayName("Параметризованный тест, создание заказов с разным кол-вом ингредиентов")
    @Description("Позитивный тест ручки /api/orders")
    public void orderShouldBeCreated() {

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

        ingredientsId = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("data[5]._id", Matchers.notNullValue())
                .extract()
                .path("data._id");

        OrderCreateRequest randomOrder = getIngredients(ingredientsId);
        OrderCreateRequest orderCreateRequest  = new OrderCreateRequest();
        orderCreateRequest.setIngredients(order);
        orderClient.createOrder(randomOrder, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }
}
