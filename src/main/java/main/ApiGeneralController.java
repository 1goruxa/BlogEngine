package main;

import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.TagsResponse;
import main.service.CalendarService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@RestController
public class ApiGeneralController {
    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final TagService tagService;
    private final CalendarService calendarService;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagService tagService, CalendarService calendarService) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagService = tagService;
        this.calendarService = calendarService;
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
    private TagsResponse showTagsNoQuery(){
        return tagService.getTags("");
    }

    @GetMapping("/api/tag/{query}")
    private TagsResponse showTags(@PathVariable String query){
        return tagService.getTags(query);
    }

    @GetMapping("api/calendar")
    private CalendarResponse calendarResponse(@RequestParam int year){
        if (year == 0){
            year = Year.now().getValue();
        }
        return calendarService.getPostsByYear(year);
    }

    //!stat
}
