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
import org.junit.runners.Parameterized;

import java.util.List;

import static generator.CreateOrderRequestGenerator.getRandomOrder;
import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
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
        orderClient.createOrder(randomOrder)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("name", equalTo("Бессмертный бургер"));
    }
}

