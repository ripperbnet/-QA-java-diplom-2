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
    @DisplayName("Checking order list on authorized user")
    @Description("Positive test of api /api/orders endpoint")
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

        OrderCreateRequest randomOrder = getOneIngredient();

        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Бессмертный бургер"));

        orderClient.getOrderData(token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("orders._id", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Checking order list on unauthorized user")
    @Description("Negative test of api /api/orders endpoint")
    public void OrderListShouldBeNotDisplayed() {

        orderClient.getOrderDataWithoutToken()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
