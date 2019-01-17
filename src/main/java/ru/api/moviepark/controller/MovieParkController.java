package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.data.DBPostgreWorker;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.PlaceInHallInfo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@Slf4j
@RequestMapping("/movie-park")
public class MovieParkController {

    private DBPostgreWorker worker;

    public MovieParkController(DBPostgreWorker worker) {
        this.worker = worker;
    }

    @GetMapping("/get-seances-for-date/{dateStr}")
    @ResponseBody
    public List<AllSeancesView> getAllTodaySeances(@PathVariable String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateStr, formatter);
            return worker.getAllSeancesForDate(localDate);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-all-seances")
    @ResponseBody
    public List<AllSeancesView> getAllSeances() {
        return worker.getAllSeances();
    }

    @PostMapping("/add-seance")
    @ResponseBody
    public CommonResponse addSeance(@RequestBody CreateSeanceInput inputJson) {
        return worker.createAndAddNewSeance(inputJson);
    }

    @PostMapping("/block-unblock-place")
    @ResponseBody
    public CommonResponse blockOrUnblockPlace(@RequestBody BlockPlaceInput inputJson) {
        try {
            worker.blockOrUnblockPlaceOnSeance(inputJson);
            if (inputJson.getIsBlocked()) {
                return CommonResponse.PLACE_BLOCKED;
            } else {
                return CommonResponse.PLACE_UNBLOCKED;
            }
        } catch (Exception e){
            log.error(e.getMessage());
            return CommonResponse.ERROR;
        }
    }

    @PostMapping("/create-schedule-for-date/{dateStr}")
    @ResponseBody
    public CommonResponse createScheduleTableForDate(@PathVariable String dateStr){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateStr, formatter);
            worker.fillScheduleTableForDate(localDate);
            return CommonResponse.TABLE_FILLED;
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-seance-info/{seanceId}")
    @ResponseBody
    public List<PlaceInHallInfo> getSeanceFullInfo(@PathVariable int seanceId){
        return worker.getSeanceFullInfo(seanceId);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
