package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.api.moviepark.data.dbclient.DatabaseClient;
import ru.api.moviepark.data.dbclient.RemoteDatabaseClientImpl;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.List;

import static ru.api.moviepark.config.Constants.dateTimeFormatter;
import static ru.api.moviepark.controller.CommonResponse.ERROR;
import static ru.api.moviepark.controller.CommonResponse.PLACE_BLOCKED;
import static ru.api.moviepark.controller.CommonResponse.PLACE_UNBLOCKED;
import static ru.api.moviepark.controller.CommonResponse.TABLE_FILLED;

@Controller
@Slf4j
@RequestMapping("/movie_park")
public class MovieParkController {

    private DatabaseClient databaseClient;

    public MovieParkController(RemoteDatabaseClientImpl databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/get_seances_for_date/{dateStr}")
    @ResponseBody
    public List<AllSeancesView> getAllTodaySeances(@PathVariable String dateStr) {
        try {
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return databaseClient.getAllSeancesForDate(localDate);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
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
            return ERROR;
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
