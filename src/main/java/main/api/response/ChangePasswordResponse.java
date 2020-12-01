package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangePasswordResponse {
    private boolean result;
    private ErrorsOnPasswordChange errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsOnPasswordChange getErrors() {
        return errors;
    }

    public void setErrors(ErrorsOnPasswordChange errors) {
        this.errors = errors;
    }
}

