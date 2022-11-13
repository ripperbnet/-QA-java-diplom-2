import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import io.qameta.allure.junit4.DisplayName;
import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private UserClient userClient;

    private String token;

    @Before
    public void setUp() {
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
    @DisplayName("Логин под существующим пользователем")
    @Description("Позитивный тест ручки /api/auth/login/")
    public void userShouldBeLogged() {
        UserCreateRequest randomUser = getRandomUser();
        token = userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);
        userClient.loginUser(userLoginRequest, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Логин без ввода почты")
    @Description("Негативный тест ручки /api/auth/login/")
    public void emailFieldShouldBeValidated() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(null);
        userLoginRequest.setPassword("12345");
        token = userClient.loginUserWithoutToken(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .extract()
                .path("accessToken");
    }

    @Test
    @DisplayName("Логин без ввода пароля")
    @Description("Негативный тест ручки /api/auth/login/")
    public void passwordFieldShouldBeValidated() {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("test-email@yandex.ru");
        userLoginRequest.setPassword(null);
        token = userClient.loginUserWithoutToken(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .extract()
                .path("accessToken");
    }
}
