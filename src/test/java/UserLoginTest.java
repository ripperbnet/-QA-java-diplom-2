import client.UserClient;
import dto.CreateUserRequest;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import static generator.CreateUserRequestGenerator.getRandomUser;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;

public class UserLoginTest {

    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @Test
    public void userShouldBeCreated() {

        CreateUserRequest randomUser = getRandomUser();

        userClient.createUser(randomUser)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }
}
