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

//{
//   "result": false,
//   "errors": {
//   	"email": "Этот e-mail уже зарегистрирован",
//   	"photo": "Фото слишком большое, нужно не более 5 Мб",
//   	"name": "Имя указано неверно",
//   	"password": "Пароль короче 6-ти символов",
//    }
//}