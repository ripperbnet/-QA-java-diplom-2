package generator;

import dto.CreateUserRequest;
import org.apache.commons.lang3.RandomStringUtils;

public class CreateUserRequestGenerator {

    public static CreateUserRequest getRandomUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName("test-client" + RandomStringUtils.randomNumeric(3));
        createUserRequest.setEmail("test-email" + RandomStringUtils.randomNumeric(3) + "@yandex.ru");
        createUserRequest.setPassword("12345");
        return createUserRequest;
    }
}
