package main.repo;
import main.model.Captcha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface CaptchaRepository extends JpaRepository<Captcha, Integer> {

    @Transactional
    @Modifying
    @Query(value="INSERT INTO captcha_codes(code, secret_code, time) values (:code, :secretCode, :time)",nativeQuery = true)
    void createCaptcha(String code, String secretCode, Date time);

    @Query(value = "SELECT COUNT(id) FROM captcha_codes", nativeQuery = true)
    int countAll();

    @Transactional
    @Modifying
    @Query(value="DELETE FROM captcha_codes WHERE time < (NOW() - INTERVAL 60 MINUTE)", nativeQuery = true)
    void killOldCaptchas();

    Captcha findOneBySecretCode(String secretCode);

}