package main;

import main.api.request.LoginRequest;
import main.api.request.RegisterRequest;
import main.api.response.CaptchaResponse;
import main.api.response.LoginResponse;
import main.api.response.RegisterResponse;
import main.service.CaptchaService;
import main.service.LoginService;
import main.service.RegisterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;

@RestController
public class ApiAuthController {

    private final CaptchaService captchaService;
    private final RegisterService registerService;
    private final LoginService loginService;

    public ApiAuthController(CaptchaService captchaService, RegisterService registerService, LoginService loginService) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.loginService = loginService;
    }

    @GetMapping("/api/auth/check")
    private LoginResponse authCheck(Principal principal){
        return loginService.authCheck(principal);
    }

    @GetMapping("/api/auth/captcha")
    private CaptchaResponse getCaptcha() throws IOException {
        return captchaService.getCaptcha();
    }

    @PostMapping("/api/auth/register")
    private RegisterResponse register(@RequestBody RegisterRequest data){
        return registerService.register(data);
    }

    @PostMapping("/api/auth/login")
    private LoginResponse loginResponse(@RequestBody LoginRequest data){
        return loginService.login(data);
    }

    @GetMapping("/api/auth/logout")

    private LoginResponse logoutResponse(Principal principal){
        return loginService.logout(principal);
    }

}
