package main;

import main.api.response.AuthResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {

    @GetMapping("/api/auth/check")
    private AuthResponse authCheck(){
        //Убрать это отсюда потом в сервис
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(false);

        return authResponse;
    }
}
