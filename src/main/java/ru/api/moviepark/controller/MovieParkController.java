package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.controller.valueobjects.BlockPlaceInput;
import ru.api.moviepark.controller.valueobjects.CommonResponse;
import ru.api.moviepark.controller.valueobjects.CreateSeanceInput;
import ru.api.moviepark.services.DBPostgreService;
import ru.api.moviepark.services.valueobjects.AllSeancesViewPojo;
import ru.api.moviepark.services.valueobjects.PlaceInHallInfo;

import java.time.LocalDate;
import java.util.List;


@Controller
@Slf4j
@RequestMapping("/movie-park")
public class MovieParkController {

    private DBPostgreService service;

    public MovieParkController(DBPostgreService service) {
        this.service = service;
    }

    @GetMapping("/get-seances-for-date/{dateStr}")
    @ResponseBody
    public List<AllSeancesViewPojo> getAllTodaySeances(@PathVariable String dateStr) {
        try {
            String[] dateParams = dateStr.split("-");
            int year = Integer.parseInt(dateParams[0]);
            int month = Integer.parseInt(dateParams[1]);
            int day = Integer.parseInt(dateParams[2]);
            List<AllSeancesViewPojo> result = service.getAllSeancesForDate(LocalDate.of(year, month, day));
            return result;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-all-seances")
    @ResponseBody
    public List<AllSeancesViewPojo> getAllSeances() {
        return service.getAllSeances();
    }

    @PostMapping("/add-seance")
    @ResponseBody
    public CommonResponse addSeance(@RequestBody CreateSeanceInput inputJson) {
        return service.addSeance(inputJson);
    }

    @PostMapping("/block-place")
    @ResponseBody
    public CommonResponse blockPlace(@RequestBody BlockPlaceInput inputJson) {
        try {
            service.blockPlaceOnSeance(inputJson);
            return CommonResponse.PLACE_BLOCKED;
        } catch (Exception e){
            log.error(e.getMessage());
            return CommonResponse.ERROR;
        }
    }

    @PostMapping("/create-schedule-for-date/{dateStr}")
    @ResponseBody
    public CommonResponse createScheduleTableForDate(@PathVariable String dateStr){
        try {
            String[] dateParams = dateStr.split("-");
            int year = Integer.parseInt(dateParams[0]);
            int month = Integer.parseInt(dateParams[1]);
            int day = Integer.parseInt(dateParams[2]);
            service.createAndFillScheduleTableForDate(LocalDate.of(year, month, day));
            return CommonResponse.TABLES_UPDATED;
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-seance-info/{seanceId}")
    @ResponseBody
    public List<PlaceInHallInfo> getSeanceFullInfo(@PathVariable int seanceId){
        return service.getSeanceFullInfo(seanceId);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
