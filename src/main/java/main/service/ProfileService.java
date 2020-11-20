package main.service;

import main.Repo.UserRepository;
import main.api.request.EditMyProfileRequest;
import main.api.response.EditMyProfileResponse;
import main.model.User;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.imgscalr.Scalr.*;
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
    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public EditMyProfileResponse edit(EditMyProfileRequest editMyProfileRequest, Principal principal){
        EditMyProfileResponse editMyProfileResponse = new EditMyProfileResponse();

        editMyProfileResponse.setResult(false);
        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            currentUser.setName(editMyProfileRequest.getName());
            currentUser.setEmail(editMyProfileRequest.getEmail());

            if (editMyProfileRequest.getPassword() != null){
                currentUser.setPassword(passwordEncoder.encode(editMyProfileRequest.getPassword()));
            }

            if (editMyProfileRequest.getRemovePhoto() == 1){
                currentUser.setPhoto("");
            }
            editMyProfileResponse.setResult(true);
            if (!editMyProfileRequest.getPhoto().isEmpty()){
                MultipartFile file = editMyProfileRequest.getPhoto();
                String email = currentUser.getEmail();
                email = email.replace('@', '-');
                email = email.replace('.', '-');

                String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                String fileName = "logo" + currentUser.getName() + email + "." + extension;

                assert extension != null;
                if (extension.equals("jpg") || extension.equals("png")) {
                    String pathName = System.getProperty("user.dir") + "\\images\\" + fileName;

                    try {
                        File fileLogo = new File(pathName);
                        byte[] bytes = editMyProfileRequest.getPhoto().getBytes();
                        BufferedOutputStream stream =
                                new BufferedOutputStream(new FileOutputStream(fileLogo));
                        stream.write(bytes);
                        stream.close();
                        //Конвертируем в jpeg если это png
                        BufferedImage bufferedImage = ImageIO.read(fileLogo);
                        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
                        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                        //работаем с jpeg (Scalr.resize)
                        ImageIO.write(Scalr.resize(newBufferedImage,36), "jpg", new File(System.getProperty("user.dir") + "\\images\\" + "logo" + currentUser.getName() + email + ".jpg"));
                        if(extension.equals("png")) {
                            fileLogo.delete();
                        }
                        fileName = "logo" + currentUser.getName() + email + ".jpg";
                        currentUser.setPhoto("http://localhost:8080/images/" + fileName);
                    } catch (Exception e) {
                        editMyProfileResponse.setResult(false);
                        System.out.println("ФАЙЛ не загружен");
                    }
                }
                else{
                    editMyProfileResponse.setResult(false);
                }
            }
            userRepository.save(currentUser);
        }
        return editMyProfileResponse;
    }

}
