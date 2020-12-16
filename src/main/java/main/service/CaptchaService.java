package main.service;

import com.github.cage.Cage;
import com.github.cage.YCage;
import main.repo.CaptchaRepository;
import main.api.response.CaptchaResponse;
import main.model.Captcha;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;


@Service
public class CaptchaService {
    @Value("${hashCaptchaLength}")
    long hashLength;

    @Autowired
    private CaptchaRepository captchaRepository;

    public CaptchaResponse getCaptcha() throws IOException {
        CaptchaResponse captchaResponse = new CaptchaResponse();

        //бэк генерирует изображение image и без сохранения на диск конвертит в строку base64, обязательно добавляя к результату
        //заголовок data:image/png;base64,
        Captcha captcha = generateCaptcha();
        Cage cage = new YCage();
        BufferedImage bufferedImage = cage.drawImage(captcha.getCode());
        bufferedImage = Scalr.resize(bufferedImage, 145,40);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( bufferedImage, "jpg", baos );
        baos.flush();
        byte[] fileContent = baos.toByteArray();
        baos.close();
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        captchaResponse.setSecret(captcha.getSecretCode());
        captchaResponse.setImage("data:image/png;base64," + encodedString);

        return captchaResponse;
    }

    //Метод генерирует коды капчи, - отображаемый и секретный, - сохраняет их в базу данных
    //Метод также удаляет старые капчи (старше 1 часа)
    private Captcha generateCaptcha(){
        Captcha captcha = new Captcha();
        SecureRandom random = new SecureRandom();

        int bound = 10;
        for(int i=1;i<hashLength;i++){
            bound = bound*10;
        }
        int num = random.nextInt(bound);
        String secretCode = String.format("%05d", num);
        num = random.nextInt(100000);
        String code = String.format("%05d", num);

        Date time = new Date();
        captchaRepository.killOldCaptchas();
        captchaRepository.createCaptcha(code, secretCode, time);
        captcha.setId(captchaRepository.countAll());
        captcha.setCode(code);
        captcha.setSecretCode(secretCode);
        captcha.setTime(time);
        return captcha;
    }

}
