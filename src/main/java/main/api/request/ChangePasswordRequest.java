package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangePasswordRequest {
    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;

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

    public String getCaptchaSecret() {
        return captchaSecret;
    }

    public void setCaptchaSecret(String captchaSecret) {
        this.captchaSecret = captchaSecret;
    }
}

//{
//        "code":"b55ca6ea6cb103c6384cfa366b7ce0bdcac092be26bc0",
//        "password":"123456",
//        "captcha":"3166",
//        "captcha_secret":"eqKIqurpZs"
//        }