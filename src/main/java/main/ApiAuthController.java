package main;

import main.api.request.ChangePasswordRequest;
import main.api.request.LoginRequest;
import main.api.request.PasswordRestoreRequest;
import main.api.request.RegisterRequest;
import main.api.response.*;
import main.service.CaptchaService;
import main.service.LoginService;
import main.service.PasswordService;
import main.service.RegisterService;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
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
    private final PasswordService passwordService;

    public ApiAuthController(CaptchaService captchaService, RegisterService registerService, LoginService loginService, PasswordService passwordService) {
        this.captchaService = captchaService;
        this.registerService = registerService;
        this.loginService = loginService;
        this.passwordService = passwordService;
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
    private ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest data){
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

    @PostMapping("/api/auth/restore")
    private PasswordRestoreResponse passwordRestore(@RequestBody PasswordRestoreRequest passwordRestoreRequest){
        return passwordService.restore(passwordRestoreRequest);
    }

    @PostMapping("/api/auth/password")
    private ChangePasswordResponse changePasswordResponse(@RequestBody ChangePasswordRequest changePasswordRequest){
        return passwordService.change(changePasswordRequest);
    }


}
