package main.service;

import main.repo.CaptchaRepository;
import main.repo.SettingsRepository;
import main.repo.UserRepository;
import main.api.request.RegisterRequest;
import main.api.response.RegisterErrorsResponse;
import main.api.response.RegisterResponse;
import main.model.Captcha;
import main.model.GlobalSettings;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class RegisterService {
    @Value("${hashUserLength}")
    long hashLength;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CaptchaRepository captchaRepository;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private SettingsRepository settingsRepository;

    public ResponseEntity<RegisterResponse> register(RegisterRequest registerRequest){
        RegisterResponse registerResponse = new RegisterResponse();
        RegisterErrorsResponse registerErrorsResponse = new RegisterErrorsResponse();
        HttpStatus httpStatus = HttpStatus.OK;;

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
            //пароль зашифрован BCrypt
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRegTime(new Date());
            user.setPhoto("");

            //! Можем установить модератором
            user.setIsModerator(0);

            //Код для активной сессии - 5 цифр. Проверка на уникальность. Менять алгоритм на hash тут
            SecureRandom random = new SecureRandom();
            int bound = 10;
            for(int i=1;i<hashLength;i++){
                bound = bound*10;
            }
            Optional<User> duplicateCodeUser = null;
            String code;
            do {
                int num = random.nextInt(bound);
                code = String.format("%05d", num);
                duplicateCodeUser = userRepository.findOneByCode(code);
            }
            while (duplicateCodeUser.isPresent());

            user.setCode(code);

            //Проверка на глобальные настройки разрешения регистрировать
            GlobalSettings globalSettings = settingsRepository.findOneByName("MULTIUSER_MODE");
            String multiUserModeSettngs = globalSettings.getValue();

            if (multiUserModeSettngs.equals("0")){
                httpStatus = HttpStatus.NOT_FOUND;
                registerResponse.setResult(false);
            }
            else{
                httpStatus = HttpStatus.OK;
                userRepository.save(user);
            }
        }

        return new ResponseEntity(registerResponse, httpStatus);
    }

}
