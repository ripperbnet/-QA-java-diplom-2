import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

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

       userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание невалидного пользователя без почты")
    @Description("Негативный тест ручки /api/auth/register")
    public void emailFieldShouldBeValidated(){
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail(null);
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword("12345");
        Response response = userClient.createUserResponse(userCreateRequest);
        token = response.path("accessToken");
        assertEquals(403, response.statusCode());
        assertEquals("Email, password and name are required fields",response.path("message"));
    }

    @Test
    @DisplayName("Создание невалидного пользователя без имени")
    @Description("Негативный тест ручки /api/auth/register")
    public void nameFieldShouldBeValidated() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("test-email333@yandex.ru");
        userCreateRequest.setName(null);
        userCreateRequest.setPassword("12345");
        Response response = userClient.createUserResponse(userCreateRequest);
        token = response.path("accessToken");
        assertEquals(403, response.statusCode());
        assertEquals("Email, password and name are required fields",response.path("message"));
    }

    @Test
    @DisplayName("Создание невалидного пользователя без пароля")
    @Description("Негативный тест ручки /api/auth/register")
    public void passwordFieldShouldBeValidated() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setEmail("test-email444@yandex.ru");
        userCreateRequest.setName("test-name");
        userCreateRequest.setPassword(null);
        Response response = userClient.createUserResponse(userCreateRequest);
        token = response.path("accessToken");
        assertEquals(403, response.statusCode());
        assertEquals("Email, password and name are required fields",response.path("message"));
    }
}
