package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditMyProfileResponse {
    private boolean result;
    private ErrorsOnProfileEdit errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsOnProfileEdit getErrors() {
        return errors;
    }

    public void setErrors(ErrorsOnProfileEdit errors) {
        this.errors = errors;
    }
}
