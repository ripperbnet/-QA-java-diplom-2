package generator;

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
