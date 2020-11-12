package main.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

//@JsonIgnore()
public class LoginResponse {
    boolean result;
    LoginUserResponse user;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public LoginUserResponse getUser() {
        return user;
    }

    public void setUser(LoginUserResponse user) {
        this.user = user;
    }
}
