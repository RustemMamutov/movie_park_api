package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.api.moviepark.data.RemoteDatabaseClient;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.api.moviepark.controller.CommonResponse.*;


@Controller
@Slf4j
@RequestMapping("/movie_park")
public class MovieParkController {

    private RemoteDatabaseClient databaseClient;

    public MovieParkController(RemoteDatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/get-seances-for-date/{dateStr}")
    @ResponseBody
    public List<AllSeancesView> getAllTodaySeances(@PathVariable String dateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(dateStr, formatter);
            return databaseClient.getAllSeancesForDate(localDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-all-seances")
    @ResponseBody
    public List<AllSeancesView> getAllSeances() {
        return databaseClient.getAllSeances();
    }

    @PostMapping("/add-seance")
    @ResponseBody
    public CommonResponse addSeance(@RequestBody CreateSeanceInput inputJson) {
        return databaseClient.createNewSeance(inputJson);
    }

    @PostMapping("/block-unblock-place")
    @ResponseBody
    public CommonResponse blockOrUnblockPlace(@RequestBody BlockPlaceInput inputJson) {
        try {
            databaseClient.blockOrUnblockPlaceOnSeance(inputJson);
            if (inputJson.getBlocked()) {
                return PLACE_BLOCKED;
            } else {
                return PLACE_UNBLOCKED;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    @PostMapping("/update-seances-schedule/{days}")
    @ResponseBody
    public CommonResponse updateScheduleTable(@PathVariable int days) {
        try {
            databaseClient.updateScheduleTable(days);
            return TABLE_FILLED;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get-seance-info/{seanceId}")
    @ResponseBody
    public List<SeancePlacesEntity> getSeanceFullInfo(@PathVariable int seanceId) {
        return databaseClient.getSeanceFullInfo(seanceId);
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
