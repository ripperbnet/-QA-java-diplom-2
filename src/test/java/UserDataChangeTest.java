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

public class UserDataChangeTest {

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
    @DisplayName("Изменение почты пользователя с авторизацией")
    @Description("Позитивный тест ручки /api/auth/user")
    public void emailFieldShouldBeChanged() {

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

        userLoginRequest.setEmail("changed-email@yandex.ru");
        userClient.updateUser(token, userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.email", equalTo("changed-email@yandex.ru"));
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Позитивный тест ручки /api/auth/user")
    public void nameFieldShouldBeChanged() {
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

        userLoginRequest.setName("changed-name");
        userClient.updateUser(token, userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.name", equalTo("changed-name"));
    }

    @Test
    @DisplayName("Изменение почты и пароля без авторизации по токену")
    @Description("Негативный тест ручки /api/auth/user")
    public void userDataShouldBeNotChanged() {
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

        userLoginRequest.setName("changed-name");
        userLoginRequest.setEmail("changed-email@yandex.ru");
        userClient.updateUserWithoutToken(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
