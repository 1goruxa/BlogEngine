package main.api.response;

import java.util.List;

public class RegisterResponse {

    boolean result;

    RegisterErrorsResponse errors;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public RegisterErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(RegisterErrorsResponse errors) {
        this.errors = errors;
    }
}
