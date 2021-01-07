package ru.api.moviepark.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.api.moviepark.swagger.AddToSwagger;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@AddToSwagger
@RequestMapping("/movie-park/demo")
public class DemoController {

    @GetMapping
    public String test() {
        return "test";
    }
}
