package ru.api.moviepark.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/movie-park/demo")
public class DemoController {

    @GetMapping
    public String test() {
        return "test";
    }
}
