package main.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@EnableAutoConfiguration
@Controller
public class DefaultController {

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(method = {RequestMethod.OPTIONS, RequestMethod.GET},
            value = "/**/{path:[^\\.]*}")
    public String redirectToIndex() {
        return "forward:/";
    }

    

}

