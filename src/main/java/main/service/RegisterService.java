package main.service;

import main.Repo.CaptchaRepository;
import main.Repo.UserRepository;
import main.api.request.RegisterRequest;
import main.api.response.RegisterErrorsResponse;
import main.api.response.RegisterResponse;
import main.model.Captcha;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

@Service
public class RegisterService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaRepository captchaRepository;

    public RegisterResponse register(RegisterRequest registerRequest){
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterErrorsResponse registerErrorsResponse = new RegisterErrorsResponse();

        //Тут все проверки на возможность зарегистрироваться
        registerResponse.setResult(true);
        registerResponse.setErrors(null);
        if (userRepository.countAllByEmail(registerRequest.geteMail()) !=0 ){
            registerResponse.setResult(false);
            registerErrorsResponse.setEmail("Этот e-mail уже зарегистрирован");
        }
        if(registerRequest.getPassword().length() < 6){
            registerResponse.setResult(false);
            registerErrorsResponse.setPassword("Пароль короче 6-ти символов");
        }
        registerRequest.setName(registerRequest.getName().trim());
        if (registerRequest.getName().length() < 2 || !Character.isLetter(registerRequest.getName().charAt(0))){
            registerResponse.setResult(false);
            registerErrorsResponse.setName("Имя указано неверно");
        }

        Captcha captcha = captchaRepository.findOneBySecretCode(registerRequest.getSecretCode());

        if (!registerRequest.getCode().equals(captcha.getCode())){
            registerResponse.setResult(false);
            registerErrorsResponse.setCaptcha("Код с картинки введён неверно");
        }
        registerResponse.setErrors(registerErrorsResponse);

        //В случае если все успешно - преобразовываем реквест в пользователя и добавляем в базу
        if (registerResponse.isResult()){
            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.geteMail());
            user.setPassword(registerRequest.getPassword());
            user.setRegTime(new Date());
            user.setPhoto("");

            //! Можем установить модератором
            user.setIsModerator(0);

            //Код для активной сессии - 5 цифр
            SecureRandom random = new SecureRandom();
            int num = random.nextInt(100000);
            String code = String.format("%05d", num);
            user.setCode(code);

            userRepository.save(user);
        }

        return registerResponse;
    }

}
