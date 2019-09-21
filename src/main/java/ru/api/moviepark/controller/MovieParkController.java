package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.api.moviepark.data.dbclient.DatabaseClient;
import ru.api.moviepark.data.dbclient.RemoteDatabaseClientImpl;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.valueobjects.AllSeancesView;
import ru.api.moviepark.data.valueobjects.BlockUnblockPlaceInput;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static ru.api.moviepark.config.Constants.dateTimeFormatter;
import static ru.api.moviepark.controller.CommonResponse.ERROR;
import static ru.api.moviepark.controller.CommonResponse.PLACE_BLOCKED;
import static ru.api.moviepark.controller.CommonResponse.PLACE_UNBLOCKED;
import static ru.api.moviepark.controller.CommonResponse.TABLE_FILLED;

@Controller
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/movie-park")
public class MovieParkController {

    private DatabaseClient databaseClient;

    public MovieParkController(RemoteDatabaseClientImpl databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/get-seance-info/{seanceId}")
    @ResponseBody
    public AllSeancesView getAllSeancesByDate(@PathVariable int seanceId) {
        try {
            return databaseClient.getSeanceById(seanceId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/get-seances-for-date/{dateStr}")
    @ResponseBody
    public List<AllSeancesView> getAllSeancesByDate(@PathVariable String dateStr) {
        try {
            LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
            return databaseClient.getAllSeancesForDate(localDate);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/get-all-seances")
    @ResponseBody
    public List<AllSeancesView> getAllSeances() {
        return databaseClient.getAllSeances();
    }

    @GetMapping("/get-all-movies-by-date/{dateStr}")
    @ResponseBody
    public Map<Integer, String> getAllMoviesByDate(@PathVariable String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
        return databaseClient.getAllMoviesByDate(localDate);
    }

    @GetMapping("/get-seances-by-movie-and-date/{movieId}/{dateStr}")
    @ResponseBody
    public Map<String, List<AllSeancesView>> getAllSeancesByMovieAndDateGroupByMoviePark(
            @PathVariable int movieId, @PathVariable String dateStr) {
        LocalDate localDate = LocalDate.parse(dateStr, dateTimeFormatter);
        return databaseClient.getAllSeancesByMovieAndDateGroupByMoviePark(movieId, localDate);
    }

    @PostMapping("/add-seance")
    @ResponseBody
    public CommonResponse addSeance(@RequestBody CreateSeanceInput inputJson) {
        return databaseClient.createNewSeance(inputJson);
    }

    @PostMapping("/block-unblock-place")
    @ResponseBody
    public CommonResponse blockOrUnblockPlace(@RequestBody BlockUnblockPlaceInput inputJson) {
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
            return ERROR;
        }
    }

    @GetMapping("/get-hall-places-info/{hallId}")
    @ResponseBody
    public List<HallsEntity> getHallFullInfo(@PathVariable int hallId) {
        try {
            return databaseClient.getHallPlacesInfo(hallId);
        } catch (Exception e) {
            throw e;
        }
    }


    @GetMapping("/get-seance-places-info/{seanceId}")
    @ResponseBody
    public List<SeancePlacesEntity> getSeanceFullInfo(@PathVariable int seanceId) {
        try {
            return databaseClient.getSeancePlacesInfo(seanceId);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/test")
    public String test() {
        return "test";
    }
}
