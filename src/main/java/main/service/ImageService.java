package main.service;

import main.repo.UserRepository;
import main.api.response.ErrorsOnImageLoad;
import main.api.response.ImageResponse;
import main.model.User;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

@Service
public class ImageService {
    @Autowired
    private UserRepository userRepository;

    public ResponseEntity saveImage(MultipartFile image, Principal principal) {

        ImageResponse imageResponse = new ImageResponse();
        ErrorsOnImageLoad errorsOnImageLoad = new ErrorsOnImageLoad();
        imageResponse.setResult(true);
        String pathName = "";
        ArrayList<String> folders = new ArrayList<>();

        //Метод загружает на сервер изображение в папку upload и три случайные подпапки. Имена подпапок и изображения
        // можно формировать из случайного хэша, разделяя его на части.

        Optional<User> optionalUser = null;
        if (principal != null) {
            optionalUser = userRepository.findOneByEmail(principal.getName());
        }
        if (optionalUser.isPresent()) {

            if (image != null && image.getSize()<=5242880) {
              String extension = FilenameUtils.getExtension(image.getOriginalFilename());
              assert extension != null;
                if (extension.equals("jpg") || extension.equals("png")) {
                    if (imageResponse.isResult()) {
                        //String pathName = System.getProperty("user.dir") + "\\uploads\\" + image.getOriginalFilename();
                       //3 случайные подпапки
                        folders = getRandomFolderNames();
                        String folderPath = System.getProperty("user.dir")+"/uploads/"+"/"+folders.get(0)+"/"+folders.get(1)+"/"+folders.get(2);
                        boolean n1 = new File(folderPath).mkdirs();
                        pathName = folderPath + "/" + image.getOriginalFilename();
                       //-------------------
                        try {
                            File fileLogo = new File(pathName);
                            byte[] bytes = image.getBytes();
                            BufferedOutputStream stream =
                                    new BufferedOutputStream(new FileOutputStream(fileLogo));
                            stream.write(bytes);
                            stream.close();
                        } catch (Exception e) {
                            imageResponse.setResult(false);
                            errorsOnImageLoad.setImage("Ошибка загрузки файла");
                            imageResponse.setErrors(errorsOnImageLoad);
                        }
                    }
                } else {
                    imageResponse.setResult(false);
                    errorsOnImageLoad.setImage("Загрузка возможна только для файлов jpg и png");
                    imageResponse.setErrors(errorsOnImageLoad);
                }
            }
            else {
                imageResponse.setResult(false);
                if(image.getSize()>5242880){
                    errorsOnImageLoad.setImage("Фото слишком большое, нужно не более 5 Мб");
                    imageResponse.setErrors(errorsOnImageLoad);
                }
            }
        }
        HttpStatus httpStatus;
        ResponseEntity responseEntity;
        if (imageResponse.isResult()){
            httpStatus = HttpStatus.OK;
            pathName = "/uploads/" + folders.get(0) + "/" + folders.get(1) + "/" + folders.get(2) + "/" + image.getOriginalFilename();
            responseEntity = new ResponseEntity(pathName, httpStatus);
        }
        else{
            httpStatus = HttpStatus.BAD_REQUEST;
            responseEntity = new ResponseEntity(imageResponse, httpStatus);
        }
        return responseEntity;
    }

    private ArrayList<String> getRandomFolderNames(){
        ArrayList<String> folders = new ArrayList<>();
        StringBuilder allFolders= new StringBuilder();
        char[] array = new char[6];
        int rand;
        Random r = new Random();
        for (int i = 0; i< 6; i++) {
            rand = r.nextInt(25) + 97; //тут менять нужные диапазоны ((max - min) + 1) + min (см ASCII) (97-122)
            array[i] = (char)rand;
        }
        for(char c : array){
          allFolders.append(c);
        }
        folders.add(allFolders.substring(0,2));
        folders.add(allFolders.substring(2,4));
        folders.add(allFolders.substring(4,6));
        return folders;
    }

}


