package main.service;

import main.repo.UserRepository;
import main.api.request.EditMyProfileRequest;

import main.api.response.EditMyProfileResponse;
import main.api.response.ErrorsOnProfileEdit;
import main.model.User;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Optional;

@Service
public class ProfileService {
    @Value("${hostname}")
    String address;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public EditMyProfileResponse edit(String name, String emailReq, MultipartFile photo, int removePhoto, String password, Principal principal){

        EditMyProfileResponse editMyProfileResponse = new EditMyProfileResponse();
        EditMyProfileRequest editMyProfileRequest = new EditMyProfileRequest();
        editMyProfileRequest.setName(name);
        editMyProfileRequest.setEmail(emailReq);
        editMyProfileRequest.setPassword(password);
        editMyProfileRequest.setRemovePhoto(removePhoto);
        editMyProfileResponse.setResult(false);
        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            editMyProfileResponse.setResult(true);
            ErrorsOnProfileEdit errorsOnProfileEdit = new ErrorsOnProfileEdit();

            //Ошибки редактирования профиля. Дубликаты имени и почты
            Optional<User> duplicatedNameUserOptional = userRepository.findOneByName(editMyProfileRequest.getName());
            User duplicatedNameUser = new User();
            User duplicatedEmailUser = new User();
            if (duplicatedNameUserOptional.isPresent()){
                duplicatedNameUser = duplicatedNameUserOptional.get();
            }
            Optional<User> duplicatedEmailUserOptional = userRepository.findOneByEmail(editMyProfileRequest.getEmail());
            if (duplicatedEmailUserOptional.isPresent()){
                duplicatedEmailUser = duplicatedEmailUserOptional.get();
            }
            if (currentUser.getId() != duplicatedNameUser.getId() && duplicatedNameUser.getId() !=0){
                editMyProfileResponse.setResult(false);
                errorsOnProfileEdit.setName("Имя указано неверно. Такой пользователь уже зарегистрирован");
            }
            else{
                currentUser.setName(editMyProfileRequest.getName());
            }
            if (currentUser.getId() != duplicatedEmailUser.getId() && duplicatedNameUser.getId() !=0){
                editMyProfileResponse.setResult(false);
                errorsOnProfileEdit.setEmail("Этот e-mail уже зарегистрирован");
            }
            else{
                currentUser.setEmail(editMyProfileRequest.getEmail());
            }

            //Длина пароля
            if (editMyProfileRequest.getPassword() != null && !editMyProfileRequest.getPassword().equals("")){
                if (editMyProfileRequest.getPassword().length()>5){
                    currentUser.setPassword(passwordEncoder.encode(editMyProfileRequest.getPassword()));
                }
                else{
                    errorsOnProfileEdit.setPassword("Пароль короче 6-ти символов");
                    editMyProfileResponse.setResult(false);
                }
            }
            //Фото больше 5мб
            if (removePhoto == 2){
                photo = null;
                errorsOnProfileEdit.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                editMyProfileResponse.setResult(false);
            }

            //Обработка фото
            if (photo != null){

                String email = currentUser.getEmail();
                email = email.replace('@', '-');
                email = email.replace('.', '-');

                String extension = FilenameUtils.getExtension(photo.getOriginalFilename());

                String fileName = "logo" + currentUser.getName() + "" + email + "." + extension;

                assert extension != null;
                if (extension.equals("jpg") || extension.equals("png")) {
                    if (editMyProfileResponse.isResult()){
                        String pathName = System.getProperty("user.dir") + "/images/";
                        try {
                            File fileLogo = new File(pathName, fileName);
                            byte[] bytes = photo.getBytes();
                            //exist
                            boolean n1 = new File(pathName).mkdirs();
                            if (!fileLogo.exists()){fileLogo.createNewFile();}

                            FileOutputStream flStream = new FileOutputStream(fileLogo);
                            BufferedOutputStream stream = new BufferedOutputStream(flStream);
                            stream.write(bytes);
                            stream.close();
                            //Конвертируем в jpeg если это png
                            BufferedImage bufferedImage = ImageIO.read(fileLogo);
                            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                            //работаем с jpeg (Scalr.resize)
                            ImageIO.write(Scalr.resize(newBufferedImage, 36), "jpg", new File(System.getProperty("user.dir") + "/images/" + "logo" + currentUser.getName() + email + ".jpg"));
                            if (extension.equals("png")) {
                                fileLogo.delete();
                            }
                            fileName = "logo" + currentUser.getName() + email + ".jpg";

                            //локальный вызов аватара
                            currentUser.setPhoto(address + "/images/" + fileName);

                        } catch (Exception e) {
                            editMyProfileResponse.setResult(false);
                            errorsOnProfileEdit.setPhoto("Фото слишком большое, нужно не более 5 Мб");
                            System.out.println("ФАЙЛ не загружен");
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    editMyProfileResponse.setResult(false);
                }
            }

            if (editMyProfileRequest.getRemovePhoto() == 1){
                currentUser.setPhoto("");
            }

            if(editMyProfileResponse.isResult()){
                userRepository.save(currentUser);
            }
            else{
                editMyProfileResponse.setErrors(errorsOnProfileEdit);
            }
        }
        return editMyProfileResponse;
    }
}


