package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorsOnProfileEdit {
    String email;
    String photo;
    String name;
    String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


//"errors": {
//   	"email": "Этот e-mail уже зарегистрирован",
//   	"photo": "Фото слишком большое, нужно не более 5 Мб",
//   	"name": "Имя указано неверно",
//   	"password": "Пароль короче 6-ти символов",