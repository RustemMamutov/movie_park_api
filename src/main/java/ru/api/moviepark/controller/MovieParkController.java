package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.controller.valueobjects.BlockPlaceInputJson;
import ru.api.moviepark.controller.valueobjects.CommonResponse;
import ru.api.moviepark.services.DBPostgreService;


@Controller
@Slf4j
public class MovieParkController {

    private DBPostgreService service;

    public MovieParkController(DBPostgreService service) {
        this.service = service;
    }

    @PostMapping("/block-place")
    @ResponseBody
    public CommonResponse blockPlace(@RequestBody BlockPlaceInputJson inputJson) {
        try {
            service.blockPlaceOnSeance(inputJson);
            return CommonResponse.PLACE_BLOCKED;
        } catch (Exception e){
            log.error(e.getMessage());
            return CommonResponse.ERROR;
        }

    }

    @PostMapping("/update-tables")
    @ResponseBody
    public CommonResponse updateSeanceTables(){
        try {
            service.createTablesForAllMissingSeancesTodayAndTomorrow();
            service.deleteOldSeanceTablesBeforeToday();
            return CommonResponse.TABLES_UPDATED;
        } catch (Exception e){
            log.error(e.getMessage());
            return CommonResponse.ERROR;
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
