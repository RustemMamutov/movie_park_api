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
import java.util.List;

import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;
import static ru.api.moviepark.controller.CommonResponse.TABLE_FILLED;
import static ru.api.moviepark.controller.CommonResponse.PLACE_BLOCKED;
import static ru.api.moviepark.controller.CommonResponse.PLACE_UNBLOCKED;
import static ru.api.moviepark.controller.CommonResponse.ERROR;

import static ru.api.moviepark.config.CONSTANTS.dateTimeFormatter;

@Controller
@Slf4j
@RequestMapping("/movie_park")
public class MovieParkController {

    private RemoteDatabaseClient databaseClient;

    public MovieParkController(RemoteDatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/change_cache_ttl/{ttl}")
    @ResponseBody
    public CommonResponse changeCacheTtl(@PathVariable String ttl) {
        try {
            databaseClient.changeCacheLifeTime(Integer.parseInt(ttl));
            return VALID_DATA;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get_seances_for_date/{dateStr}")
    @ResponseBody
    public List<AllSeancesView> getAllTodaySeances(@PathVariable String dateStr) {
        try {
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return databaseClient.getAllSeancesForDate(localDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get_all_seances")
    @ResponseBody
    public List<AllSeancesView> getAllSeances() {
        return databaseClient.getAllSeances();
    }

    @PostMapping("/add_seance")
    @ResponseBody
    public CommonResponse addSeance(@RequestBody CreateSeanceInput inputJson) {
        return databaseClient.createNewSeance(inputJson);
    }

    @PostMapping("/block_unblock_place")
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

    @PostMapping("/update_seances_schedule/{days}")
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

    @GetMapping("/get_seance_info/{seanceId}")
    @ResponseBody
    public List<SeancePlacesEntity> getSeanceFullInfo(@PathVariable int seanceId) {
        try {
            return databaseClient.getSeanceFullInfo(seanceId);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }

}
