package main.model;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "captcha_codes")
public class Captcha {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Date time;

    private String code;

    @Column(name="secret_code")
    private String secretCode;
}
