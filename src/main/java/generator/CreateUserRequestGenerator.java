package generator;

import dto.UserCreateRequest;
import org.apache.commons.lang3.RandomStringUtils;

public class CreateUserRequestGenerator {

    public static UserCreateRequest getRandomUser() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("test-client" + RandomStringUtils.randomNumeric(3));
        userCreateRequest.setEmail("test-email" + RandomStringUtils.randomNumeric(3) + "@yandex.ru");
        userCreateRequest.setPassword("12345");
        return userCreateRequest;
    }
}
