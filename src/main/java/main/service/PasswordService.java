package main.service;

import main.Repo.UserRepository;
import main.api.request.PasswordRestoreRequest;
import main.api.response.PasswordRestoreResponse;
import main.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSender;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PasswordService {

    @Autowired
    private UserRepository userRepository;

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
            //Путь к восстановлению хранить где-нибудь в настройках
            String restore = "To restore your password please follow the link: \n\n <a href=http://localhost:8080/login/change-password/" + user.getCode() + "> LINK </a>";

            ApplicationContext context =
                    new ClassPathXmlApplicationContext("Spring-Mail.xml");

            PasswordService passwordService = (PasswordService) context.getBean("mailMail");
            passwordService.sendMail("no-reply@blogengine.com",
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
}
