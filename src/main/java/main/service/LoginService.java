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

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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


    public LoginResponse login(LoginRequest loginRequest){

        LoginResponse loginResponse = new LoginResponse();

        //Обрабатываем запрос и формируем ответ

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(auth);
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findOneByEmail(userDetails.getUsername()).orElseThrow(() -> new NullPointerException("USER is NULL"));
        if (currentUser != null) {
            LoginUserResponse loginUserResponse = mapUserToLoginUserResponse(currentUser);
            loginResponse.setUser(loginUserResponse);
            loginResponse.setResult(true);
        }
        else{
            loginResponse.setResult(false);
        }

        return loginResponse;
    }

    //Все обработки запросов PreAuthorized (@PreAuthorize("hasAuthority('user:moderate')"))

    public LoginResponse authCheck(Principal principal){
        //Проверка авторизованности пользователя с помощью Spring Security

        LoginResponse loginResponse = new LoginResponse();
        LoginUserResponse loginUserResponse = new LoginUserResponse();

        if (principal == null){
            loginResponse.setResult(false);
            return loginResponse;
        }
        else{
            Optional<User> optUser = userRepository.findOneByEmail(principal.getName());
            User currentUser = optUser.get();
            loginUserResponse = mapUserToLoginUserResponse(currentUser);
            loginResponse.setUser(loginUserResponse);
            loginResponse.setResult(true);

        }
        return loginResponse;
    }

    public LoginResponse logout(Principal principal){
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        if (principal != null){
            SecurityContextHolder.clearContext();
        }
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


