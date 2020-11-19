package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddCommentResponse {

    private Integer id;

    private Boolean result;

    private ErrorsOnAddingComment errors;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean isResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public ErrorsOnAddingComment getErrors() {
        return errors;
    }

    public void setErrors(ErrorsOnAddingComment errors) {
        this.errors = errors;
    }
}
