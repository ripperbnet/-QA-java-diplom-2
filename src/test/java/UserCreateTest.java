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
    @DisplayName("Creating a valid user then trying to create the same user")
    @Description("Positive and negative test of api /api/auth/register endpoint")
    public void userShouldBeCreated() {

        // Регистрация валидного пользователя
        UserCreateRequest randomUser = getRandomUser();
        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);

        // логин с сохранением токена
        token = userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue())
                .extract()
                .path("accessToken");

       // Попытка создать уже созданного пользователя
       userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));

    }

    @Test
    @DisplayName("Creating an invalid user without email")
    @Description("Negative test of api /api/auth/register endpoint")
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
    @DisplayName("Creating an invalid user without name")
    @Description("Negative test of api /api/auth/register endpoint")
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
    @DisplayName("Creating an invalid user without password")
    @Description("Negative test of api /api/auth/register endpoint")
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
