package generator;

import client.UserClient;
import dto.UserCreateRequest;
import dto.UserLoginRequest;

public class LoginUserRequestGenerator {

    public static UserLoginRequest from(UserCreateRequest userCreateRequest) {
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.setEmail(userCreateRequest.getEmail());
        userLoginRequest.setPassword(userCreateRequest.getPassword());
        return userLoginRequest;
   }
}
