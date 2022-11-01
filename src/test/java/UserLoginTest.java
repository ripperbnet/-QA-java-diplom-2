import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private UserClient userClient;

//    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    public void userShouldBeLogged() {
        // Создание валидного пользователя
        UserCreateRequest randomUser = getRandomUser();

        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        UserLoginRequest userLoginRequest = LoginUserRequestGenerator.from(randomUser);

        userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("accessToken", Matchers.notNullValue());
    }


        @Test
        public void userShouldNotBeLogged() {

        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(null);
        userLoginRequest.setPassword("12345");
        userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));

        userLoginRequest.setEmail("test-email@yandex.ru");
        userLoginRequest.setPassword(null);
        userClient.loginUser(userLoginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
        }

      //  userClient.deleteUser(token)
     //           .assertThat()
    //           .statusCode(200);


    }
