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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static generator.CreateOrderRequestGenerator.getRandomOrder;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class OrderCreateTestParameterized {

    private OrderClient orderClient;

    private UserClient userClient;

    private String token;

    private List<String> order;

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
                List.of("61c0c5a71d1f82001bdaaa6f")
        );
    }

    @Test
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


        OrderCreateRequest randomOrder = getRandomOrder();
        OrderCreateRequest orderCreateRequest  = new OrderCreateRequest();
        orderCreateRequest.setIngredients(order);
        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Бессмертный бургер"));
    }
}
