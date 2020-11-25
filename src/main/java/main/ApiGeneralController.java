package main;

import main.api.request.AddCommentRequest;
import main.api.request.EditMyProfileRequest;
import main.api.request.SettingsRequest;

import main.api.response.*;
import main.service.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.SizeLimitExceededException;
import java.security.Principal;
import java.time.Year;


@RestController
public class ApiGeneralController {
    private final SettingsService settingsService;
    private final InitResponse initResponse;
    private final TagService tagService;
    private final CalendarService calendarService;
    private final StatService statService;
    private final CommentService commentService;
    private final ProfileService profileService;
    private final ImageService imageService;

    public ApiGeneralController(SettingsService settingsService, InitResponse initResponse, TagService tagService, CalendarService calendarService, StatService statService, CommentService commentService, ProfileService profileService, ImageService imageService) {
        this.settingsService = settingsService;
        this.initResponse = initResponse;
        this.tagService = tagService;
        this.calendarService = calendarService;
        this.statService = statService;
        this.commentService = commentService;
        this.profileService = profileService;
        this.imageService = imageService;
    }

    @GetMapping("/api/settings")
    private SettingsResponse getSettings(){

        return settingsService.getGlobalSettings();
    }

    @PutMapping("/api/settings")
    private SettingsResponse setSettings(@RequestBody SettingsRequest settingsRequest, Principal principal){

        return settingsService.setGlobalSettings(settingsRequest, principal);
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
    private CalendarResponse calendarResponse(@RequestParam(required = false, defaultValue = "0") int year){
        if (year == 0){
            year = Year.now().getValue();
        }
        return calendarService.getPostsByYear(year);
    }

    @GetMapping("api/statistics/all")
    private StatResponse statResponse(){
        return statService.getAllStat();
    }

    @GetMapping("api/statistics/my")
    private StatResponse myStatResponse(Principal principal){
        return statService.getMyStats(principal);
    }

    @PostMapping("/api/comment")
    private AddCommentResponse addCommentResponse(@RequestBody AddCommentRequest addCommentRequest, Principal principal){
        return commentService.addComment(addCommentRequest, principal);
    }

    @PostMapping (value = "/api/profile/my")
    private EditMyProfileResponse editMyProfileResponse(@RequestBody EditMyProfileRequest editMyProfileRequest, Principal principal) {
        MultipartFile photo = null;
        String name = editMyProfileRequest.getName();
        String email = editMyProfileRequest.getEmail();
        int removePhoto = editMyProfileRequest.getRemovePhoto();
        String password = editMyProfileRequest.getPassword();
        return profileService.edit(name, email, photo, removePhoto, password, principal);
    }

    @PostMapping (value = "/api/profile/my", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = "*/*")
    private EditMyProfileResponse editMyProfileResponse2 (@ModelAttribute(name="photo") MultipartFile photo,
                                                        @ModelAttribute(name="name") String name,
                                                        @ModelAttribute(name="email") String email,
                                                        @ModelAttribute(name="password") String password,
                                                        Integer removePhoto,
                                                        Principal principal) throws SizeLimitExceededException {
        if(photo.getSize() > 5242880){
            removePhoto = 2;
        }

        return profileService.edit(name, email, photo, removePhoto, password, principal);
    }

    @PostMapping(value = "api/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces ="*/*")
    private ResponseEntity saveImage(@RequestParam(name = "image") MultipartFile multipartFile, Principal principal){

        return imageService.saveImage(multipartFile, principal);
    }

    //!global settings

}




