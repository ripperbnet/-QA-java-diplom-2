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
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class GetOrdersDataTest {

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
    @DisplayName("Получение списка заказов под авторизованным пользователем")
    @Description("Позитивный тест ручки /api/orders")
    public void orderListShouldBeDisplayed() {
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
