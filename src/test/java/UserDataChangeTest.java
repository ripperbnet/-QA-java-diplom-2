import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;
import generator.LoginUserRequestGenerator;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserDataChangeTest {

    private UserClient userClient;

    private String token;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    public void userDataShouldBeChanged() {
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

                userClient.tokenAuth(token)
                        .assertThat()
                        .statusCode(SC_OK)
                        .and()
                        .body("user.email", Matchers.notNullValue());
    }
}
