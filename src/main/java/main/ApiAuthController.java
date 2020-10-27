package main;

import main.api.response.AuthResponse;
import main.api.response.CaptchaResponse;
import main.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ApiAuthController {

    private final CaptchaService captchaService;

    public ApiAuthController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/api/auth/check")
    private AuthResponse authCheck(){
        //Убрать это отсюда потом в сервис
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(false);

        return authResponse;
    }

    @GetMapping("/api/auth/captcha")
    private CaptchaResponse getCaptcha() throws IOException {

        return captchaService.getCaptcha();
    }
}
