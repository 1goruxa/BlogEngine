package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NewPostResponse {
    private boolean result;
    private ErrorsOnPostResponse errors;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public ErrorsOnPostResponse getErrors() {
        return errors;
    }

    public void setErrors(ErrorsOnPostResponse errors) {
        this.errors = errors;
    }
}


//{
//  "result": false,
//  "errors": {
//    "title": "Заголовок не установлен",
//    "text": "Текст публикации слишком короткий"
//  }
//}