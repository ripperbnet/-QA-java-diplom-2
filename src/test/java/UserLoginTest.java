import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

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
    @DisplayName("Creating user and logging in")
    @Description("Positive test of api /api/auth/login/ endpoint")
    public void userShouldBeLogged() {

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
    }

    @Test
    @DisplayName("Trying to login without email")
    @Description("Negative test of api /api/auth/login/ endpoint")
    public void emailFieldShouldBeValidated() {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(null);
        userLoginRequest.setPassword("12345");
        userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Trying to login without password")
    @Description("Negative test of api /api/auth/login/ endpoint")
    public void passwordFieldShouldBeValidated() {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail("test-email@yandex.ru");
        userLoginRequest.setPassword(null);
        userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
