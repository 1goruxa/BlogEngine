package main;

import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagsResponse;
import main.service.SettingsService;
import main.service.TagsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
//@RequestMapping(value="/api", method= RequestMethod.GET, produces={"application/json; charset=UTF-8"})
public class ApiGeneralController {
    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final TagsService tagsService;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagsService tagsService) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagsService = tagsService;
    }

    @GetMapping("/api/settings")
    private SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }


    @GetMapping("/api/init")
    private InitResponse init(){
        initResponse.setSubtitle("Блог друзей");
        return initResponse;
    }

    @GetMapping("/api/tag")
    private TagsResponse showTags(){
        return tagsService.getTags();
    }


}
