import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
    public void userShouldNotBeCreated() {

        // Регистрация пользователя без email
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail(null);
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword("12345");
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));

        // Регистрация пользователя без name
        userCreateRequest.setEmail("test-email@yandex.ru");
        userCreateRequest.setName(null);
        userCreateRequest.setPassword("12345");
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));

        // Регистрация пользователя без password
        userCreateRequest.setEmail("test-email@yandex.ru");
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword(null);
        userClient.createUser(userCreateRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
