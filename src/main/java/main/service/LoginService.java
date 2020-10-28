package main.service;

import main.Repo.PostRepository;
import main.Repo.UserRepository;
import main.api.request.LoginRequest;
import main.api.response.LoginResponse;
import main.api.response.LoginUserResponse;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    public Map<String,Integer> sessionList = new HashMap<>();

    public LoginResponse login(LoginRequest loginRequest) throws NullPointerException {

        LoginResponse loginResponse = new LoginResponse();
        //Обрабатываем запрос и формируем ответ
     try {
         User user = userRepository.findOneByEmail(loginRequest.getEmail());
         if (!user.getPassword().equals(loginRequest.getPassword())) {
             loginResponse.setResult(false);
         }
         else{
             loginResponse.setResult(true);
             //Маппинг User - LoginUserResponse
             LoginUserResponse loginUserResponse = mapUserToLoginUserResponse(user);
             loginResponse.setUser(loginUserResponse);
             //Добавляем в список активных сессий
             sessionList.put(user.getCode(),1);
         }
     }
     catch (Exception ex){
         ex.printStackTrace();
         loginResponse.setResult(false);
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


