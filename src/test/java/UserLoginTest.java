import client.UserClient;
import dto.UserCreateRequest;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private UserClient userClient;

    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    public void userShouldBeLogged() {
        // Создание валидного пользователя
        UserCreateRequest randomUser = getRandomUser();
        token =  userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");

        userClient.loginUser(token)
                .assertThat()
                .statusCode(200);


    }
}
