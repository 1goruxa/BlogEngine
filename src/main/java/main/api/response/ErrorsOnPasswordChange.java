package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsOnPasswordChange {
    private String code;
    private String password;
    private String captcha;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}


//"code": "Ссылка для восстановления пароля устарела.
//				<a href=
//				\"/auth/restore\">Запросить ссылку снова</a>",
//		"password": "Пароль короче 6-ти символов",
//		"captcha": "Код с картинки введён неверно"