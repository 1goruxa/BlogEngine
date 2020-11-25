package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageResponse {
    private boolean result;
    private ErrorsOnImageLoad errors;


    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsOnImageLoad getErrors() {
        return errors;
    }

    public void setErrors(ErrorsOnImageLoad errors) {
        this.errors = errors;
    }
}

