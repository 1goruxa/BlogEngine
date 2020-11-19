package main.service;

import main.Repo.SettingsRepository;
import main.Repo.UserRepository;
import main.api.request.SettingsRequest;
import main.api.response.SettingsResponse;
import main.model.User;
import org.hibernate.event.spi.PreInsertEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.Optional;

@Service
@Transactional
public class SettingsService {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    public SettingsResponse getGlobalSettings(){

        SettingsResponse settingsResponse = new SettingsResponse();
        //Запросы
        settingsResponse.setMultiuserMode(settingsRepository.getMultiUser());
        settingsResponse.setStatisticsIsPublic(settingsRepository.getStats());
        settingsResponse.setPostPremoderation(settingsRepository.getPostModer());

        return settingsResponse;
    }

    public SettingsResponse setGlobalSettings(SettingsRequest settingsRequest, Principal principal){
        SettingsResponse settingsResponse = new SettingsResponse();
        //Пользователь авторизован и модератор
        Optional<User> optionalUser = null;
        if(principal != null) {optionalUser = userRepository.findOneByEmail(principal.getName());}
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getIsModerator() == 1) {
                //очищаем
                settingsRepository.dropSettings();
                //перезаливаем
                settingsRepository.setMultiUser(settingsRequest.isMultiuserMode());
                settingsRepository.setPostModer(settingsRequest.isPostPremoderation());
                settingsRepository.setStats(settingsRequest.isStatisticsIsPublic());
                //возвращаем
                settingsResponse = getGlobalSettings();
            }

        }

        return settingsResponse;

    }
}
