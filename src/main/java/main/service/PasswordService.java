package main.service;

import main.Repo.CaptchaRepository;
import main.Repo.UserRepository;
import main.api.request.ChangePasswordRequest;
import main.api.request.PasswordRestoreRequest;
import main.api.response.ChangePasswordResponse;
import main.api.response.ErrorsOnPasswordChange;
import main.api.response.PasswordRestoreResponse;
import main.model.Captcha;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PasswordService {
    @Value("${linkttl}")
    long linkTTL;

    @Value("${hashUserLength}")
    long hashLength;

    @Value("${hostname}")
    String mainPath;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaptchaRepository captchaRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public PasswordRestoreResponse restore(PasswordRestoreRequest passwordRestoreRequest){
        String email = passwordRestoreRequest.getEmail();
        PasswordRestoreResponse passwordRestoreResponse = new PasswordRestoreResponse();
        passwordRestoreResponse.setResult(false);

        Optional<User> optionalUser = userRepository.findOneByEmail(email);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();

            String codeAndDate = user.getCode() + System.currentTimeMillis();
            //Путь к восстановлению хранить где-нибудь в настройках
            String restore = "To restore your password please follow the link: \n\n <a href=" + mainPath +"/login/change-password/" + codeAndDate + "> LINK </a>";

            sendMail("no-reply@blogengine.com",
                    email,
                    "Восстановление пароля blogEngine",
                    restore);

            passwordRestoreResponse.setResult(true);
        }
        return passwordRestoreResponse;
    }

    public void sendMail(String from, String to, String subject, String msg) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setHeader("Content-Type", "text/plain; charset=UTF-8");
            message.setSubject(subject, "UTF-8");
            message.setText(msg, "UTF-8");
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(msg, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            Logger.getLogger(PasswordService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ChangePasswordResponse change(ChangePasswordRequest changePasswordRequest){
        ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse();
        ErrorsOnPasswordChange errorsOnPasswordChange = new ErrorsOnPasswordChange();

        //ЗАДАЕТСЯ ДЛИНА КОДА ПОЛЬЗОВАТЕЛЯ. ЕСЛИ ИЗМЕНИТЬ В СОЗДАНИИ ТО МЕНЯТЬ И ЗДЕСЬ
        String userCode = changePasswordRequest.getCode().substring(0,(int)hashLength);
        Optional<User> optionalUser = userRepository.findOneByCode(userCode);
        User user = optionalUser.get();
        String captchaBySecretCode="";
        Captcha requiredCaptcha = new Captcha();
        requiredCaptcha = captchaRepository.findOneBySecretCode(changePasswordRequest.getCaptchaSecret());
        changePasswordResponse.setResult(true);

        //Ошибки смены пароля
        //Капча не совпадает
        if (requiredCaptcha==null || !changePasswordRequest.getCaptcha().equals(requiredCaptcha.getCode())){
            changePasswordResponse.setResult(false);
            errorsOnPasswordChange.setCaptcha("Код с картинки введён неверно");
        }
        //Пароль короче 6 символов
        if (changePasswordRequest.getPassword().length()<6){
            changePasswordResponse.setResult(false);
            errorsOnPasswordChange.setPassword("Пароль короче 6 символов");
        }
        long whenItWasSent = Long.parseLong(changePasswordRequest.getCode().substring(changePasswordRequest.getCode().length()-13));

//        Время жизни ссылки
        if (System.currentTimeMillis() - whenItWasSent > linkTTL){
            changePasswordResponse.setResult(false);
            errorsOnPasswordChange.setCode("Ссылка для восстановления пароля устарела. <a href=/auth/restore>Запросить ссылку снова</a>");
        }
        if (!changePasswordResponse.isResult()){
            changePasswordResponse.setErrors(errorsOnPasswordChange);
        }
        else{
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getPassword()));
            userRepository.save(user);
        }
        return changePasswordResponse;
    }
}
