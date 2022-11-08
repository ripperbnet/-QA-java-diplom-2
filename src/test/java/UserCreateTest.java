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
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserCreateTest {

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
    @DisplayName("Создание валидного пользователя, попытка создать уже зарегистрированного пользователя")
    @Description("Позитивный и негативный тесты для ручки /api/auth/register")
    public void userShouldBeCreated() {

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

       userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));

    }

    @Test
    @DisplayName("Создание невалидного пользователя без почты")
    @Description("Негативный тест ручки /api/auth/register")
    public void emailFieldShouldBeValidated() {

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail(null);
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword("12345");
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание невалидного пользователя без имени")
    @Description("Негативный тест ручки /api/auth/register")
    public void nameFieldShouldBeValidated() {

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("test-email333@yandex.ru");
        userCreateRequest.setName(null);
        userCreateRequest.setPassword("12345");
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание невалидного пользователя без пароля")
    @Description("Негативный тест ручки /api/auth/register")
    public void passwordFieldShouldBeValidated() {

        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("test-email444@yandex.ru");
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword(null);
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
