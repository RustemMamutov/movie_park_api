package ru.api.moviepark.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.controller.valueobjects.InputJson;
import ru.api.moviepark.services.DBPostgreService;


@Controller
public class MovieParkController {

    private DBPostgreService service;

    public MovieParkController(DBPostgreService service) {
        this.service = service;
    }

    @PostMapping("/block-place")
    @ResponseBody
    public void blockPlace(@RequestBody InputJson inputJson) {
        service.blockPlaceOnSeance(inputJson);
    }

    @PostMapping("/update-tables")
    @ResponseBody
    public void updateSeanceTables(){
        service.createTablesForAllMissingSeancesTodayAndTomorrow();
        service.deleteOldSeanceTablesBeforeToday();
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
