package main.service;

import main.Repo.PostRepository;
import main.Repo.UserRepository;
import main.Security.UserDetailsServiceImpl;
import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.LoginUserResponse;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    @Autowired
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;


    public LoginService(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }


    public LoginResponse login(LoginRequest loginRequest) throws NullPointerException {

        LoginResponse loginResponse = new LoginResponse();
        //Обрабатываем запрос и формируем ответ

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            User currentUser = userRepository.findOneByEmail(userDetails.getUsername()).orElseThrow(() -> new NullPointerException("USER is NULL"));
            LoginUserResponse loginUserResponse = mapUserToLoginUserResponse(currentUser);
            loginResponse.setUser(loginUserResponse);
            loginResponse.setResult(true);

        return loginResponse;
    }

    //Как-то возвращать isModerator. Все обработки запросов PreAuthorized (@PreAuthorize("hasAuthority('user:moderate')"))

    public LoginResponse authCheck(){
        //Проверка авторизованности пользователя с помощью Spring Security

        LoginResponse loginResponse = new LoginResponse();
        return loginResponse;
    }

    //Маппинг логин и аутентиф пользователя
    private LoginUserResponse mapUserToLoginUserResponse (User user){
        LoginUserResponse loginUserResponse = new LoginUserResponse();

        loginUserResponse.setId(user.getId());
        loginUserResponse.setName(user.getName());
        loginUserResponse.setPhoto(user.getPhoto());
        loginUserResponse.setEmail(user.getEmail());
        if(user.getIsModerator() == 1){
            loginUserResponse.setModeration(true);
        }
        else{
            loginUserResponse.setModeration(false);
        }
        // Посты требующие модерации
        int countModeration = postRepository.countAllByModerationStatus("NEW");
        if(loginUserResponse.isModeration()){
            loginUserResponse.setModerationCount(countModeration);
        }
        else{
            loginUserResponse.setModerationCount(0);
        }
        //По настройки дефолту true. Уточнить где меняется
        loginUserResponse.setSettings(true);
        return loginUserResponse;
    }


}


